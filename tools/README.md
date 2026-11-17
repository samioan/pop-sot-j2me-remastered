# Tooling

The pipeline is adapted from `../sod-remastered` (itself following the gow-betrayal, Ratchet &
Clank and TES Travels workflows). All scripts resolve project paths relative to their own location.

| Command | Result |
|---|---|
| `python tools/bootstrap.py` | Verify/provision pinned tools, locate Java 17+ |
| `python tools/build_reference.py [--jar PATH]` | Extract (SHA-256 checked), decompile to `sot/decompiled`, rename to `sot/src`, apply asserting decompiler fixes, compile-check |
| `python tools/check_narrowing.py` | Narrowing-cast counts and call targets of `sot/src` vs. the original class files |
| `python tools/gen_port.py [--vanilla]` | Apply `port_patches.py` + `port_java`, translate to `sot/port/gen` (or the unpatched translation to ignored `sot/analysis/gen_vanilla`) |
| `python tools/check_engine.py [--levels]` | Original class files vs. the built engine, tick by tick |
| `python tools/outline.py Game` | Member line ranges; `--big N` filters large methods; `--calls NAME` approximates callers/callees |
| `python tools/bytecode.py pop2.e` | Original instructions in ignored `sot/analysis/` |
| `python tools/make_art.py` | The launcher's banner and icon, drawn from scratch with Pillow (no game data) |

`bootstrap.py` contains SHA-256 pins for the tool JARs. Matching copies in sibling projects
(`../sod-remastered`, the other Prince of Persia ports, gow/rac/tes) are used first; Vineflower
alone also has a pinned download fallback ([Vineflower 1.12.0](https://github.com/Vineflower/vineflower/releases/tag/1.12.0),
Apache-2.0). The API stub JARs (`midpapi20.jar`, `cldcapi11.jar`, `nokiaui.jar`) are compile-time
API definitions, not an emulator; provide them in `tools/midp-stubs/` on a machine without the
siblings. The native build and CI need no Java, stubs, Python or game data.

## Reference Java

The game is one package, `pop2`. Renames go in `sot/docs/names.map` with simple class names
(the package is implied); `renamer/ChRenamer.java` applies them on the class files' own symbol
tables through Vineflower's renamer hook. Decompiler artifacts are fixed in
`build_reference.py` as asserting entries (`PATCHES`, `WIDEN`, `OVERLOADS`) with their bytecode
evidence; never edit `sot/src` or `sot/decompiled` by hand.

`check_narrowing.py` recompiles `sot/src` and compares each method's narrowing casts and call
targets with the original class files. Calls are resolved to the class that declares the method,
since the 2003 compiler named the declaring class where javac 8 names the receiver's type.

## Translated engine (the playable game)

`gen_port.py` copies `sot/src`, applies `port_patches.py`, adds `port_java/*.java` (package
`pop2`), compile-checks and translates to C++17 with `j2cpp/J2Cpp.java` (javac's attributed trees).
Method names are mangled with their signature whenever a name is overloaded anywhere in a class's
inheritance family, so overrides (`Prince` over `Actor`) keep one C++ name.

## Equivalence oracle

`check_engine.py` runs the original class files (`reference_checks/EngineOracle.java` on the
no-draw API in `reference_checks/engine_adapter/`) against `pop_sot_check.exe` with the same key
script, comparing fingerprints of all game statics, the canvas, the prince and every actor after
every iteration. The original classes read `System.currentTimeMillis()`; the oracle's copies have
their `java/lang/System` class reference rewritten to `oracle/OracleSyst` (constant pool only), a
clock advanced exactly like the native glue's. The game keeps the font height in its state, so the
oracle's `Font` reads the engine's own metrics (`pop_sot_check --font-metrics`). `--levels`
preloads the same save (every level done) on both sides and starts from the Levels menu.

`pop_sot_check.exe` options: `--data DIR --ticks N --script FILE --trace FILE --width W
--options-menu --shot TICK FILE --mid-shot TICK FILE --verify-replay --describe --dump TICK FILE
--save-in FILE --save-out FILE --font-metrics FILE --trace-throws N`.
