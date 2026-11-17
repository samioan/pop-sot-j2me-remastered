# Prince of Persia: The Sands of Time Remastered

Decompilation and Windows port of Gameloft's **Prince of Persia: The Sands of Time** for J2ME (2003,
the Nokia Series 60 build, 176x208). Follows the approach of `../sod-remastered`,
`../tes-travels-decomp`, `../shadowkey-decomp`, gow-betrayal-remastered and rac-j2me-remastered:
recover the Java, verify it against the original bytecode, then build a native C++17 port with
CMake, Ninja and MSVC.

**`pop_sot.exe` plays the game natively** — the verified reference Java translated to C++ on a
small Java/MIDP runtime, not an emulator — with widescreen, display-rate frame interpolation,
keyboard and XInput gamepad, and a "PC options" screen inside the game's own Settings menu, drawn
in the game's style. The shipped Release engine at the original 176 width matches the original
class files field for field over tens of thousands of fuzzed ticks across all eight levels and the
menus (state under random input, not pixels); see [sot/docs/ROADMAP.md](sot/docs/ROADMAP.md).

```powershell
.\setup.ps1 -Jar <PrinceOfPersia-TheSandsOfTime.jar>   # once: tools, reference Java, C++, build, check
python tools/gen_port.py              # regenerate sot/port/gen after Java or patch changes
.\sot\port\build_dist.bat             # Release build, tests, package in sot/port/dist
python tools/check_engine.py          # original bytecode vs. the built engine (also --levels)
.\sot\port\dist\PrinceOfPersiaSoT.exe # the launcher
```

`PrinceOfPersiaSoT.exe` is the launcher: it shows the banner, takes your JAR (picked, or found
next to it), checks it with the game's SHA-256 and resource rules, keeps a copy in `game\`, offers
3x/4x or fullscreen, and starts `bin\pop_sot.exe`. It updates the install from this repository's
GitHub releases. In the game, Settings > PC options covers display, audio, keyboard and gamepad;
F1 shows controls, F8 cycles width (auto, 176, 278, 333, 370, 486), F9 frame rate, F10
interpolation, F11 fullscreen. See [sot/port/dist_readme.txt](sot/port/dist_readme.txt).

## Releases

Pushing a tag `vX.Y.Z` runs [.github/workflows/release.yml](.github/workflows/release.yml): it
builds with that version, runs the data-free tests, checks the package and publishes
`PrinceOfPersiaSoTRemastered-vX.Y.Z-win64.zip`. Installed launchers then offer it as an update
(the check is an unauthenticated GitHub API call, so it only sees releases of a public
repository). Local builds carry a `-dev` version and never offer updates.

## Layout

| Path | Contents |
|---|---|
| `roms/` | The supplied JAR (ignored) |
| `sot/extracted/` | JAR contents (ignored) |
| `sot/decompiled/` | Raw Vineflower output (generated) |
| `sot/src/` | Renamed reference Java with bytecode-verified decompiler fixes (generated) |
| `sot/docs/` | [ROADMAP.md](sot/docs/ROADMAP.md), [NATIVE_ENGINE.md](sot/docs/NATIVE_ENGINE.md), `names.map` |
| `sot/port/gen/` | The reference Java translated to C++ (generated, tracked) |
| `sot/port/engine/` | Hand-written Java/MIDP runtime, glue and Win32 host |
| `sot/port/launcher/` | The launcher (`PrinceOfPersiaSoT.exe`) and its original artwork |
| `tools/` | Pipeline and checks; see [tools/README.md](tools/README.md) |

## Pipeline

1. `python tools/build_reference.py` extracts the JAR (SHA-256 checked), decompiles it, renames it
   with `sot/docs/names.map`, applies the asserting decompiler-artifact fixes and compile-checks
   `sot/src`. `python tools/check_narrowing.py` must report 0 findings.
2. `python tools/gen_port.py` applies the desktop patches (`tools/port_patches.py`), adds
   `tools/port_java`, and translates the result to `sot/port/gen` with `tools/j2cpp`.
3. `sot\port\build.bat [Debug|Release]` builds and runs the data-free tests;
   `sot\port\build_dist.bat [version]` packages the Release build (launcher at the top, game in
   `bin\`).
4. `python tools/check_engine.py` runs the original class files on a JVM and the built engine side
   by side and fails on the first tick whose state differs.

CI builds without any game data.
