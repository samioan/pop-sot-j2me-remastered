#!/usr/bin/env python3
"""Find decompiler type-narrowing artifacts in a reference tree.

Vineflower sometimes declares an int local as byte/short/char/boolean when the bytecode only ever
stores small constants into it. Java source then narrows silently in compound assignments
(`b <<= 1`, `b += x`), which the original code never did. Such methods recompile to bytecode with
MORE i2b/i2s/i2c instructions than the original.

This recompiles sot/src and compares, per method, the number of each narrowing instruction with
the original class files (methods are matched through docs/names.map).

    python tools/check_narrowing.py

Exit status 1 if any method differs. A reported method needs an asserting WIDEN/PATCH entry in tools/build_reference.py
that restores the int local, with this tool's output as the evidence.
"""
import re
import subprocess
import sys
import tempfile
from collections import Counter
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from decompile import find_java  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
STUBS = ROOT / "tools" / "midp-stubs"
OPS = ("i2b", "i2s", "i2c")


def methods(javap: Path, classpath: Path, cls: str):
    out = subprocess.run([str(javap), "-p", "-c", "-s", "-cp", str(classpath), cls], capture_output=True, text=True, check=True).stdout
    result, name, desc = {}, None, None
    pending = None
    for line in out.splitlines():
        m = re.match(r"^  (?:[\w.$\[\]<>, ]+ )?([\w$<>]+)\((.*)\)(?: throws .*)?;$", line)
        if m:
            # javap prints constructors under the class name; key them as <init> so the original and the
            # renamed class line up.
            prefix = line[:m.start(1)].split()
            ctor = m.group(1) == cls.rsplit(".", 1)[-1] and all(w in ("public", "protected", "private") for w in prefix)
            pending = "<init>" if ctor else m.group(1)
            continue
        m = re.match(r"^    descriptor: (\S+)$", line)
        if m and pending:
            name, desc = pending, m.group(1)
            result[(name, desc)] = Counter()
            pending = None
            continue
        m = re.match(r"^\s+\d+: (\w+)", line)
        if m and name and m.group(1) in OPS:
            result[(name, desc)][m.group(1)] += 1
        # calls into the game's own classes: an overload resolved differently shows up as a changed descriptor
        m = re.match(r"^\s+\d+: invoke\w+\s+#\d+(?:,\s*\d+)?\s+// (?:Interface)?Method ([\w$/]+\.)?\"?([\w$<>]+)\"?:(\S+)$", line)
        if m and name:
            owner = (m.group(1) or cls + ".").rstrip(".").replace(".", "/")
            if PACKAGE and owner.startswith(PACKAGE + "/"):
                owner = owner[len(PACKAGE) + 1:]
            if "/" not in owner:
                result[(name, desc)][("raw", owner, m.group(2), m.group(3))] += 1
    m = re.search(r"class [\w.$]+ extends ([\w.$]+)", out)
    in_game = m and (m.group(1).startswith(PACKAGE + ".") if PACKAGE else "." not in m.group(1))
    superclass = m.group(1).rsplit(".", 1)[-1] if in_game else None
    return result, superclass


def resolve_calls(classpath: Path, javap: Path, names) -> dict:
    """Per class: methods with calls keyed "call <declaring class>(<descriptor>)".

    Old compilers name the class that declares a method as the owner of an inherited call, javac 8
    names the receiver's static type; resolving both to the declaring class makes them comparable.
    """
    parsed = {n: methods(javap, classpath, (PACKAGE + "." if PACKAGE else "") + n) for n in names}
    declared = {n: {k for k in parsed[n][0]} for n in names}

    def owner_of(owner, name, desc):
        o = owner
        while o in parsed and (name, desc) not in declared[o] and parsed[o][1]:
            o = parsed[o][1]
        # not declared by any game class: inherited from the platform (MIDlet, Canvas), not compared
        return o if o in parsed and (name, desc) in declared[o] else None

    out = {}
    for n in names:
        res = {}
        for key, counts in parsed[n][0].items():
            c = Counter()
            for k, v in counts.items():
                if isinstance(k, tuple):
                    o = owner_of(k[1], k[2], k[3])
                    if o is not None:
                        c["call " + o + k[3]] += v
                else:
                    c[k] += v
            res[key] = c
        out[n] = res
    return out


GAME = ROOT / "sot"
PACKAGE = "pop2"  # package of the game's classes ("" for the default package)


def main() -> None:
    game = GAME
    pkg = PACKAGE + "." if PACKAGE else ""
    pkg_path = PACKAGE + "/" if PACKAGE else ""
    java = Path(find_java())
    javac = java.with_name("javac.exe" if java.suffix == ".exe" else "javac")
    javap = java.with_name("javap.exe" if java.suffix == ".exe" else "javap")
    sep = ";" if sys.platform == "win32" else ":"
    classes, method_names = {}, {}
    for line in (game / "docs" / "names.map").read_text(encoding="utf-8").splitlines():
        parts = line.split()
        parts = [x.removeprefix(PACKAGE + "/") if PACKAGE else x for x in parts]
        if len(parts) == 3 and parts[0] == "class":
            classes[parts[1]] = parts[2]
        elif len(parts) == 3 and parts[0] == "method":
            owner, rest = parts[1].split(".", 1)
            method_names[(owner, rest.split("(")[0], rest[rest.index("("):])] = parts[2]
    # every original class is checked; unrenamed ones keep their name
    for c in (game / "extracted").rglob("*.class"):
        classes.setdefault(c.stem, c.stem)
    with tempfile.TemporaryDirectory(prefix="pop_narrow_") as tmp:
        build = Path(tmp)
        cp = sep.join(str(j) for j in sorted(STUBS.glob("*.jar")))
        subprocess.run([str(javac), "-nowarn", "-proc:none", "-encoding", "UTF-8", "--release", "8", "-cp", cp, "-d", str(build),
                        *map(str, sorted((game / "src").rglob("*.java")))], check=True, capture_output=True)
        bad = 0
        originals = resolve_calls(game / "extracted", javap, list(classes.keys()))
        rebuilts = resolve_calls(build, javap, list(classes.values()))
        for old, new in sorted(classes.items()):
            original = originals[old]
            rebuilt = rebuilts[new]

            def rename_desc(d):
                for o, n in classes.items():
                    d = d.replace(f"L{pkg_path}{o};", f"L{pkg_path}{n};")
                return d

            # names.map keys use Java-style parameter lists; match renamed methods by descriptor + new name
            renamed = {}
            for (owner, name, params), target in method_names.items():
                if owner == old:
                    renamed.setdefault(name, []).append(target)
            for (name, desc), counts in sorted(original.items()):
                # Vineflower appends _ to members whose obfuscated name is a Java keyword (do -> do_).
                candidates = [name] + renamed.get(name, []) + [name + "_"]
                found = None
                for c in candidates:
                    if (c, rename_desc(desc)) in rebuilt:
                        found = rebuilt[(c, rename_desc(desc))]
                        if c != name:
                            break
                if found is None:
                    print(f"{new}.{name}{desc}: not found in rebuilt classes")
                    bad += 1
                elif any(found[op] != counts[op] for op in OPS):
                    print(f"{new}.{name}{desc}: original {dict((k, counts[k]) for k in OPS if counts[k])} rebuilt {dict((k, found[k]) for k in OPS if found[k])}")
                    bad += 1
                if found is None:
                    continue
                # Calls into the game's own classes, as owner + descriptor (member names differ after
                # renaming). A decompiled call that javac resolves to another overload changes this set.
                original_calls, rebuilt_calls = Counter(), Counter()
                for key, n in counts.items():
                    if key.startswith("call "):
                        owner, d = key[5:].split("(", 1)
                        original_calls[classes.get(owner, owner) + "(" + rename_desc(d)] += n
                for key, n in found.items():
                    if key.startswith("call "):
                        rebuilt_calls[key[5:]] += n
                if original_calls != rebuilt_calls:
                    print(f"{new}.{name}{desc}: call targets differ: original-only {dict(original_calls - rebuilt_calls)} "
                          f"rebuilt-only {dict(rebuilt_calls - original_calls)}")
                    bad += 1
        print(f"{game.name}: {bad} finding(s): narrowing-cast counts or call targets that differ from the original bytecode")
        sys.exit(1 if bad else 0)


if __name__ == "__main__":
    main()
