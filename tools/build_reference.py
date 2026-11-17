#!/usr/bin/env python3
"""Reference pipeline for Prince of Persia: The Sands of Time 1.1.0 (Nokia Series 60 build, 176x208).

    python tools/build_reference.py              # extract (if needed), decompile, rename, compile-check
    python tools/build_reference.py --jar PATH   # use this JAR instead of roms/

Output:
    sot/extracted/   JAR contents (ignored)
    sot/decompiled/  raw Vineflower output
    sot/src/         renamed reference Java (sot/docs/names.map + PATCHES), compile-checked

PATCHES are decompiler artifacts fixed textually after renaming: (file, old, new), each must match
exactly once and carries its bytecode evidence in the comment. tools/check_engine.py is what
finds them: it compares the translated source with the original class files tick by tick.
"""
import hashlib
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path, PurePosixPath

sys.path.insert(0, str(Path(__file__).resolve().parent))
from decompile import VINEFLOWER, find_java  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
GAME = ROOT / "sot"
JAR = ROOT / "roms" / "PrinceOfPersia-TheSandsOfTime.jar"
SHA256 = "40e7115a32e9e584106bc6565ed78cd6af189b41a426f5d61f53bbf8d0ac3e9f"
STUBS = ROOT / "tools" / "midp-stubs"
RENAMER_SRC = ROOT / "tools" / "renamer" / "ChRenamer.java"

PATCHES: list[tuple[str, str, str]] = [
]

# Locals the decompiler declared narrower than the bytecode treats them (tools/check_narrowing.py).
# (file, first line of the method, declaration to widen)
WIDEN: list[tuple[str, str, str]] = [
    # Sprite.drawFrame: the module loop counter (step 4) is an int in the bytecode (no i2b after iadd);
    # as a byte it would wrap on frames with more than 31 modules.
    ("Sprite.java", "   public void a(int var1, int var2, boolean var3, boolean var4, int var5) {", "byte var6"),
    # tile renderer: the Nokia transform flags local is an int (no i2s after ior).
    ("Game.java", "   void a(Graphics var1, int var2, int var3, int var4, int var5) {", "short var13"),
]

# Calls whose Java overload resolution differs from the bytecode's target. (file, regex, replacement, count)
OVERLOADS: list[tuple[str, str, str, int]] = [
]


def widen(text: str, fname: str, method: str, decl: str) -> str:
    if text.count(method + "\n") != 1:
        sys.exit(f"widen: method line not unique in {fname}: {method.strip()!r}")
    start = text.index(method + "\n")
    end = text.find("\n   }\n", start)
    body = text[start:end]
    import re
    hits = re.findall(r"\b" + re.escape(decl) + r"\b", body)
    if len(hits) != 1:
        sys.exit(f"widen: expected one {decl!r} in {fname} {method.strip()!r}, found {len(hits)}")
    body = re.sub(r"\b" + re.escape(decl) + r"\b", "int " + decl.split()[1], body)
    return text[:start] + body + text[end:]


def run(cmd, **kw):
    return subprocess.run([str(c) for c in cmd], check=True, **kw)


def extract(jar: Path) -> None:
    data = jar.read_bytes()
    digest = hashlib.sha256(data).hexdigest()
    if digest != SHA256:
        sys.exit(f"{jar.name}: SHA-256 {digest} is not the supported Series 60 build ({SHA256})")
    out = GAME / "extracted"
    with zipfile.ZipFile(jar) as archive:
        for entry in archive.infolist():
            path = PurePosixPath(entry.filename)
            if path.is_absolute() or ".." in path.parts or "\\" in entry.filename or ":" in entry.filename:
                sys.exit(f"unsafe archive entry: {entry.filename}")
        if archive.testzip() is not None:
            sys.exit("archive CRC check failed")
        out.mkdir(parents=True, exist_ok=True)
        for old in out.rglob("*"):
            if old.is_file():
                old.unlink()
        archive.extractall(out)
    print(f"extracted {jar.name} -> {out.relative_to(ROOT)}", flush=True)


def main() -> None:
    jar = JAR
    if "--jar" in sys.argv:
        jar = Path(sys.argv[sys.argv.index("--jar") + 1])
        JAR.parent.mkdir(exist_ok=True)
        if jar.resolve() != JAR.resolve():
            shutil.copyfile(jar, JAR)
    extracted = GAME / "extracted"
    if "--jar" in sys.argv or not (extracted / "pop2" / "e.class").exists():
        if not JAR.exists():
            sys.exit(f"missing {JAR}; pass --jar PATH")
        extract(JAR)

    java = Path(find_java())
    javac = java.with_name("javac.exe" if java.suffix == ".exe" else "javac")
    sep = ";" if sys.platform == "win32" else ":"
    names = GAME / "docs" / "names.map"
    with tempfile.TemporaryDirectory(prefix="sot_ref_") as tmp:
        tmp = Path(tmp)
        classes, raw, renamed, tool = tmp / "classes", tmp / "raw", tmp / "renamed", tmp / "tool"
        for d in (classes, raw, renamed, tool):
            d.mkdir()
        for c in extracted.rglob("*.class"):
            dest = classes / c.relative_to(extracted)
            dest.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(c, dest)
        run([java, "-jar", VINEFLOWER, "-log=WARN", classes, raw])
        run([javac, "-d", tool, "-cp", VINEFLOWER, RENAMER_SRC])
        run([java, f"-Dch.map={names}", "-cp", f"{VINEFLOWER}{sep}{tool}",
             "org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler",
             "-log=WARN", "-ren=1", "-urc=ChRenamer", classes, renamed])
        for sub, source in (("decompiled", raw), ("src", renamed)):
            out = GAME / sub
            out.mkdir(exist_ok=True)
            texts = {p.name: p.read_text(encoding="utf-8") for p in sorted(source.rglob("*.java"))}
            if not texts:
                sys.exit("Vineflower produced no sources")
            if sub == "src":
                for fname, old, new in PATCHES:
                    if texts[fname].count(old) != 1:
                        sys.exit(f"patch does not match exactly once in {fname}: {old[:60]!r}")
                    texts[fname] = texts[fname].replace(old, new)
                for fname, method, decl in WIDEN:
                    texts[fname] = widen(texts[fname], fname, method, decl)
                import re
                for fname, pattern, replacement, count in OVERLOADS:
                    texts[fname], done = re.subn(pattern, replacement, texts[fname])
                    if done != count:
                        sys.exit(f"overload fix expected {count} substitution(s) in {fname}, made {done}: {pattern}")
            for old in out.rglob("*.java"):
                old.unlink()
            for name, text in texts.items():
                (out / name).write_text(text, encoding="utf-8", newline="\n")
            print(f"wrote {len(texts)} files -> {out.relative_to(ROOT)}", flush=True)
        classpath = sep.join(str(j) for j in sorted(STUBS.glob("*.jar")))
        build = tmp / "build"
        build.mkdir()
        run([javac, "-nowarn", "-proc:none", "-encoding", "UTF-8", "--release", "8", "-cp", classpath, "-d", build,
             *sorted((GAME / "src").rglob("*.java"))])
        print("compile check: OK (0 errors)", flush=True)


if __name__ == "__main__":
    main()
