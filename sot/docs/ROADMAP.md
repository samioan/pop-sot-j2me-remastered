# Prince of Persia: The Sands of Time roadmap

The current remaining-work list. Update it in place when work lands.

## Target and current status

- **v1.0.0** is the first release (GitHub `samioan/pop-sot-j2me-remastered`).

- **Prince of Persia: The Sands of Time, Nokia Series 60 build (176x208, MIDlet 1.1.0)**,
  `roms/PrinceOfPersia-TheSandsOfTime.jar`, SHA-256
  `40e7115a32e9e584106bc6565ed78cd6af189b41a426f5d61f53bbf8d0ac3e9f`. Six classes in package
  `pop2`, 5.3k lines decompiled; no java.util.Random (the game is deterministic given input).
- `pop_sot.exe` plays it natively with widescreen, interpolated frame rates, keyboard/gamepad and
  an in-game PC options screen. Design and limits: [NATIVE_ENGINE.md](NATIVE_ENGINE.md).
- The Release engine at width 176 with the original menus matches the original class files over
  77,465 fuzzed ticks in 22 scenarios (2026-11-18): six from a fresh start (title, story, menus,
  settings, in-game menu, quitting) and sixteen entering each of the eight levels from the Levels
  menu with a full save. State comparison under random input, not pixels.

## Done

- [x] Decompile, rename the classes (Game, Actor, Prince, Level, Sprite), compile-check; fix the
  two narrowed locals `tools/check_narrowing.py` found (0 findings remain).
- [x] Translate to C++ and run natively on the shared Java/MIDP/Nokia runtime; tick-by-tick oracle
  (`tools/check_engine.py`, `--levels`), which found and fixed two runtime bugs (RMS store creation,
  save on quit).
- [x] Simulation clock tied to the loop; Nokia ringing tones converted to MIDI and played.
- [x] Widescreen (auto, 176, 278, 333, 370, 486): camera, activation, tile buffer and blits use the
  view width; narrow levels centred; interface centred with screen-edge softkeys.
- [x] Render-only interpolation from recorded draw calls, wrapped tile buffer replayed inside its
  clips; replay at alpha 1 verified at 176 and 370.
- [x] Keyboard and XInput gamepad with context mapping; PC options screen in the game's Settings
  menu (main and in-game); system font rendering through GDI.
- [x] Smoother input: newest of opposite directions wins, stick hysteresis and wider horizontal
  sector, optional input buffer for held jump/sword presses (scripted checks, no playtest yet).
- [x] Launcher (`PrinceOfPersiaSoT.exe`) with the Remastered key art (tools/make_art.py), JAR check and install,
  window size or fullscreen, self-update from GitHub releases; data-free CI and package check.

## Remaining

- [ ] Play the game start to finish on the desktop build (no human playthrough yet), in each
  display mode and at native/60/144 fps.
- [ ] Human check of the PC options flows (rebinding by capture, gamepad page) and of gamepad and
  audio output on real hardware; both were only exercised by unattended runs.
- [ ] Decide whether the game's "Controls" layouts (Game.h 1..4, other Nokia keypads) should be
  hidden on the desktop; they remap the phone keys the host sends.
- [ ] Interpolation refinements: sub-pixel motion, per-effect layers.
- [ ] The launcher's self-update exercised end to end (v1.0.0 is the first release; the update
  path runs once a later release exists).
- [ ] A scripted, replayable playthrough checked against the original.

## Verification

- `python tools/build_reference.py` then `python tools/check_narrowing.py` (0 findings).
- `python tools/gen_port.py`, `sot\port\build.bat`, `sot\port\build_dist.bat` (Release, tests,
  package validation). CI needs no game data.
- `python tools/check_engine.py` and `python tools/check_engine.py --levels` after any change to the
  translator, runtime, port patches or port Java; they must stay identical.
- `pop_sot_check --verify-replay --width 370 --options-menu` after touching recording or drawing.
