#!/usr/bin/env python3
"""Whole-engine equivalence check: original class files vs. the translated C++ engine.

Both sides run the game's loop iteration by iteration with the same scripted key events and the
same simulation clock, and print a fingerprint of every static field of Game/Actor/Prince/Level/
Sprite, the canvas, the prince and every live actor after each iteration. Any divergence fails
with the first differing tick and fields.

    python tools/check_engine.py                                # Release pop_sot_check, default scenarios
    python tools/check_engine.py --ticks 6000 --seeds 1 2 3     # custom fuzz
    python tools/check_engine.py --exe <other pop_sot_check>    # e.g. a Debug build

pop_sot_check runs at width 176 with the original menus unless told otherwise; in that
configuration the desktop patches are required to return the original values, so the shipped
engine itself is what gets compared. Drawing is not compared (the oracle's Graphics draws nothing).

The game reads System.currentTimeMillis() for its input debounce and level timer. The oracle's
copies of the original class files have their java/lang/System class reference rewritten to
oracle/OracleSyst (constant pool only, nothing else changes), whose clock the oracle advances
exactly as the native glue does. Font metrics come from the engine (--font-metrics), since the
game keeps the font height in its state. Results go to sot/analysis/.
"""
import argparse
import random
import struct
import subprocess
import sys
import tempfile
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from decompile import find_java  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
EXTRACTED = ROOT / "sot" / "extracted"
DEFAULT_EXE = ROOT / "sot/port/build/Release/pop_sot_check.exe"
STUBS = ROOT / "tools" / "midp-stubs"
CHECKS = ROOT / "tools" / "reference_checks"
ANALYSIS = ROOT / "sot" / "analysis"
SYS_PROPS = {"oracle.sys.microedition.platform": "Nokia7650", "oracle.prop.MIDlet-Version": "1.1.0",
             "oracle.width": "176", "oracle.height": "208"}

UP, DOWN, LEFT, RIGHT, FIRE, SOFT_L, SOFT_R = -1, -2, -3, -4, -5, -6, -7
NUM = {n: 48 + n for n in range(10)}


def rewrite_system(data: bytes, replacement: str = "oracle/OracleSyst") -> bytes:
    """Rewrites the Utf8 constant "java/lang/System" (the System class reference) in a class file."""
    out = bytearray(data[:10])
    count = struct.unpack(">H", data[8:10])[0]
    pos, i = 10, 1
    while i < count:
        tag = data[pos]
        if tag == 1:
            n = struct.unpack(">H", data[pos + 1:pos + 3])[0]
            s = data[pos + 3:pos + 3 + n]
            if s == b"java/lang/System":
                r = replacement.encode()
                out += bytes([1]) + struct.pack(">H", len(r)) + r
            else:
                out += data[pos:pos + 3 + n]
            pos += 3 + n
        else:
            size = {3: 5, 4: 5, 5: 9, 6: 9, 7: 3, 8: 3, 9: 5, 10: 5, 11: 5, 12: 5, 15: 4, 16: 3, 17: 5, 18: 5, 19: 3, 20: 3}[tag]
            out += data[pos:pos + size]
            pos += size
            if tag in (5, 6):
                i += 1
        i += 1
    return bytes(out + data[pos:])


def tap(events, tick, key, hold=1):
    events.append((tick, "+", key))
    events.append((tick + hold, "-", key))


def boot_to_level(events, start):
    """Splash ("press any key"), the story text, New game, level 1. Returns the next free tick."""
    t = start
    for _ in range(6):
        tap(events, t, FIRE)
        t += 40
    return t + 40


def fuzz(events, rng, start, end, keys):
    """Random overlapping key holds, like mashing a keypad."""
    t = start
    while t < end:
        key = rng.choice(keys)
        hold = rng.choice((1, 1, 2, 3, 5, 8, 13, 21, 34))
        tap(events, t, key, hold)
        t += rng.choice((1, 1, 2, 3, 4, 6, 9))


def level_select(events, start, level):
    """With every level completed in the save: splash, Levels (the cursor starts there), level N."""
    tap(events, start, FIRE)
    tap(events, start + 40, FIRE)
    t = start + 70
    for _ in range(level):
        tap(events, t, DOWN)
        t += 6
    tap(events, t + 10, FIRE)
    return t + 60


def save_record():
    """Record 1 of POP2RECORD (Game.n): upgrades 0..9, levels completed 10..17, sound, hints, keys, scores."""
    y = bytearray(50)
    for k in range(8):
        y[10 + k] = 1
    y[20], y[21] = 1, 0
    return bytes(y)


def save_store(record):
    """The native engine's store file (midp.cpp RecordStore::persist) holding that record as id 1."""
    return b"PRMS" + bytes([1]) + struct.pack(">II", 2, 1) + struct.pack(">II", 1, len(record)) + record


def scenario(seed, ticks, level=None):
    rng = random.Random(seed)
    events = []
    t = boot_to_level(events, 160) if level is None else level_select(events, 160, level)
    move = [LEFT, RIGHT, RIGHT, RIGHT, UP, UP, DOWN, FIRE, FIRE, NUM[2], NUM[4], NUM[6], NUM[8], NUM[5], NUM[1], NUM[3]]
    if seed % 3 == 0:
        move += [SOFT_L, SOFT_R]   # the in-game menu, its settings and back
    fuzz(events, rng, t, ticks, move)
    return events


def write_script(path, events):
    path.write_text("".join(f"{t} {op} {key}\n" for t, op, key in sorted(events, key=lambda e: e[0])), encoding="ascii")


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--exe", type=Path, default=DEFAULT_EXE, help="headless driver to test (default: the Release pop_sot_check)")
    ap.add_argument("--ticks", type=int, default=3000)
    ap.add_argument("--seeds", type=int, nargs="*", default=[1, 2, 3, 4, 5, 6])
    ap.add_argument("--script", type=Path, help="use this key script instead of generated fuzz")
    ap.add_argument("--levels", action="store_true", help="start each scenario from a save with every level done, at level seed % 8")
    args = ap.parse_args()

    java = Path(find_java())
    javac = java.with_name("javac.exe" if java.suffix == ".exe" else "javac")
    sep = ";" if sys.platform == "win32" else ":"
    jars = sorted(STUBS.glob("*.jar"))
    ANALYSIS.mkdir(exist_ok=True)
    total = 0
    with tempfile.TemporaryDirectory(prefix="pop_oracle_") as tmp:
        tmp = Path(tmp)
        adapter, tool, classes, data = tmp / "adapter", tmp / "tool", tmp / "classes", tmp / "data"
        for d in (adapter, tool, classes, data):
            d.mkdir()
        sources = [str(p) for p in (CHECKS / "engine_adapter").rglob("*.java")]
        subprocess.run([str(javac), "-nowarn", "-d", str(adapter), *sources], check=True)
        subprocess.run([str(javac), "-nowarn", "-cp", str(adapter), "-d", str(tool), str(CHECKS / "EngineOracle.java")], check=True)
        for f in EXTRACTED.rglob("*"):
            if f.is_file() and f.suffix == ".class":
                dest = classes / f.relative_to(EXTRACTED)
                dest.parent.mkdir(parents=True, exist_ok=True)
                dest.write_bytes(rewrite_system(f.read_bytes()))
            elif f.is_file() and "META-INF" not in f.parts:
                (data / f.name).write_bytes(f.read_bytes())
        fonts = tmp / "fonts.txt"
        subprocess.run([str(args.exe), "--data", str(EXTRACTED), "--ticks", "0", "--font-metrics", str(fonts), "--quiet"], check=True)
        # adapter first, so its API classes are found before nothing else; the game loads resources
        # through Class.getResourceAsStream on bootstrap classes, i.e. as java.base resources
        boot = sep.join([str(adapter), str(tool), str(classes)])
        props = [f"-D{k}={v}" for k, v in SYS_PROPS.items()] + [f"-Doracle.fonts={fonts}"]
        record, store = tmp / "record.bin", tmp / "store.sav"
        record.write_bytes(save_record())
        store.write_bytes(save_store(save_record()))
        for seed in args.seeds:
            script = tmp / f"script{seed}.txt"
            if args.script:
                script = args.script
            else:
                write_script(script, scenario(seed, args.ticks, seed % 8 if args.levels else None))
            java_trace, cpp_trace = ANALYSIS / f"engine-oracle-{seed}.txt", ANALYSIS / f"engine-native-{seed}.txt"

            def run_java(extra=()):
                cmd = [str(java), "-Djava.awt.headless=true", *props, f"-Xbootclasspath/a:{boot}",
                       f"--patch-module=java.base={data}", "EngineOracle", str(script), str(args.ticks), str(java_trace),
                       *(extra or ("-1", "-")), *([str(record)] if args.levels else [])]
                subprocess.run(cmd, check=True)

            def run_cpp(extra=()):
                cmd = [str(args.exe), "--data", str(EXTRACTED), "--ticks", str(args.ticks), "--script", str(script),
                       "--trace", str(cpp_trace), "--quiet", *extra, *(["--save-in", str(store)] if args.levels else [])]
                subprocess.run(cmd, check=True)

            run_java()
            run_cpp()
            a, b = java_trace.read_text().splitlines(), cpp_trace.read_text().splitlines()
            states = sorted({int(line.split()[2]) for line in a})
            for i, (x, y) in enumerate(zip(a, b)):
                if x != y:
                    dj, dc = ANALYSIS / "engine-oracle-dump.txt", ANALYSIS / "engine-native-dump.txt"
                    run_java((str(i), str(dj)))
                    run_cpp(("--dump", str(i), str(dc)))
                    jl, cl = dj.read_text().splitlines(), dc.read_text().splitlines()
                    print(f"seed {seed}: first divergence at tick {i}: oracle '{x}' native '{y}'")
                    shown = 0
                    for n, (p, q) in enumerate(zip(jl, cl)):
                        if p.split("=", 1)[-1] != q.split("=", 1)[-1]:
                            pv, qv = p.split(" [", 1), q.split(" [", 1)
                            detail = ""
                            if len(pv) == 2 and len(qv) == 2:
                                pe, qe = pv[1].rstrip("]").split(","), qv[1].rstrip("]").split(",")
                                cells = [f"[{k}] {u} vs {v}" for k, (u, v) in enumerate(zip(pe, qe)) if u != v][:12]
                                detail = "  cells (oracle vs native): " + "; ".join(cells)
                            print(f"  field #{n}: oracle {pv[0]}   native {qv[0]}{detail}")
                            shown += 1
                            if shown >= 25:
                                break
                    if len(jl) != len(cl):
                        print(f"  field counts differ: oracle {len(jl)} native {len(cl)}")
                    sys.exit(1)
            if len(a) != len(b):
                sys.exit(f"seed {seed}: trace lengths differ: oracle {len(a)} native {len(b)}")
            total += len(a)
            print(f"seed {seed}: {len(a)} ticks identical (states visited: {states})", flush=True)
    print(f"engine equivalence: {total} ticks, {len(args.seeds)} scenario(s), all state fingerprints identical")


if __name__ == "__main__":
    main()
