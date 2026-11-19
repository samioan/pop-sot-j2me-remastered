# Translated engine: the game as native code

`pop_sot.exe` runs the original game (menus, story, eight levels, sand powers, saves, ringing-tone
music and effects) as native C++. It is not an emulator: the verified reference Java in `sot/src`
is translated to C++ mechanically and linked against a small runtime.

## Pipeline

```
sot/src/*.java  (generated reference, unchanged)
   + tools/port_patches.py   asserting desktop patches (exact text, exact match counts)
   + tools/port_java/*.java  PortConfig (wide view, settings menu rows), PortMenu (PC options), PortNative (stub)
   -> tools/j2cpp/J2Cpp.java  Java -> C++17, driven by javac's attributed syntax trees
   -> sot/port/gen/          generated C++ (tracked, do not edit)
sot/port/engine/   jrt (Java core), midp (Graphics/Image/Font/RMS/Sound/Player), engine_glue, host_win32
```

The translator makes Java semantics explicit (left-to-right evaluation, wrapping arithmetic,
masked shifts, Java exceptions for bounds/null/division, interned literals, reference counting);
see `tools/j2cpp/J2Cpp.java`. For this game it also names methods consistently across the
`Actor`/`Prince` hierarchy (a name overloaded anywhere in a class family is mangled with its
signature everywhere in it, so virtual overrides bind) and translates `super.field`.

The runtime (`engine/midp.*`) implements the MIDP 1.0/2.0 and Nokia UI slice the Prince of Persia
games use: `Graphics` with clipping and transforms, `Image` with a built-in PNG decoder, `Font`
(system fonts rasterized by GDI without anti-aliasing at the handset's pixel sizes, so text looks
like the phone's bitmap font), Nokia `FullCanvas`/`DirectGraphics` (manipulated images, polygons,
pixel arrays), `com.nokia.mid.sound.Sound`, MIDP media `Player`, multi-record `RecordStore`s.

### Game loop and clock

`Game.run()` shows the Gameloft logo, loads, waits one second and starts the title; then each
iteration calls `Game.d()` (update) and paints, sleeping the rest of `Game.H` ms (80 in play,
70 after the intro, 0 while loading). The glue runs exactly that sequence per tick. The game reads
`System.currentTimeMillis()` for its input debounce (300 ms after a menu action) and the level
timer that feeds the score, so the clock it sees is the simulation clock: it advances by `Game.H`
per iteration, and by 20 ms when `Game.H` is 0 (a pass of the loop still took time on the phone;
with a 1 ms step the debounce held keys for hundreds of iterations).

### Sound

The game plays Nokia Smart Messaging ringing tones (`Sound(..., FORMAT_TONE)`, all in `sound.bin`).
`host_midi.cpp` parses them (patterns, notes, scale, style, tempo, volume) into MIDI events on a
square-wave lead and plays them on the existing sequencer thread through the system synthesizer.

## Equivalence with the original bytecode

`python tools/check_engine.py` runs the **original class files** on a JVM
(`tools/reference_checks/EngineOracle.java`, drawing stubbed out by `engine_adapter/`) and the
built `pop_sot_check.exe` side by side with the same key script and clock, and after every
iteration compares a fingerprint of every static field of the five game classes, the canvas, the
prince and every live actor (inherited fields included). `--levels` preloads a save with every
level completed on both sides and enters level `seed % 8` from the Levels menu.

Two runtime bugs were found this way: `openRecordStore(name, true)` must create the store
persistently (a later open without create failed and the sound/hint settings were never saved),
and quitting must run `destroyApp` (the save on exit).

Limits, stated plainly: it compares state, not pixels; fuzzed input explores the first rooms of
each level well and deeper rooms only by chance; no playthrough is recorded and replayed.

## Desktop changes

All in `tools/port_patches.py` and `tools/port_java`; no field is added to the game's classes and
every helper returns the original value at width 176 with the original menus.

### Widescreen

The camera (`Level.d`, `Level.l`) keeps its rules with the view width (`PortConfig.view()`) in
place of 176: it centres the prince (or the prince and the enemy he fights), clamps to the level,
and the region activation test uses the same width, so the wider screen shows more of the level
and enemies wake at its real edges. The tile scroll buffer is created for the widest view (486)
in the desktop configuration and its column range and four wrapped blits cover the view. A level
narrower than the screen is shown whole, centred between black bars. Interface screens keep their
176-pixel layout centred (story picture, menu panels, loading bar); text centres on the screen;
the right softkey label and the "more" arrow sit at the screen's right edge.

### Frame rate and interpolation

Presentation is independent of the tick. Draw calls of world-drawing iterations (play and the
menus over it) are recorded and tagged by layer (screen, world, one per actor); in-between frames
replay them with each layer moved part of the way back to where it was drawn on the previous tick.
The world is blitted from a wrapped scroll buffer, so its blits replay shifted inside their own,
unmoved clips together with the buffer's wrapped copies, which fills the screen edges from the
buffer's margin. `pop_sot_check --verify-replay` confirms that replaying at alpha 1 reproduces each
tick's frame exactly (verified at 176 and 370).

### Input, saves, data

Keyboard and XInput gamepad map to the phone keys by context (`popapi::inputContext`): jump is Up
in play and Select in menus. The phone keypad (digits, numpad) keeps working.

The game serves one key per tick in a fixed order (standing: Down, Left/Right, Up, FIRE) and acts
on a jump or sword press only on the tick it arrives. The host leaves that alone and smooths what
it sends: of two opposite directions the newest wins (the older is released to the game and
pressed again if still held when the newer ends), and the stick has a radial deadzone with
hysteresis (engage at the deadzone, let go at 60% of it; a flickering stick otherwise double-taps
into the sand dash) and a wider Left/Right sector (Up/Down from 40 degrees above horizontal, held
down to 30). The INPUT BUFFER option (on by default, keyboard page; `input_buffer` in the ini)
runs `PortConfig.inputTick` at the start of `Game.e()`: a jump or sword press still held that has
not started a move is queued again in `Game.ax` for up to 3 ticks while the prince stands (or, for
jump, runs), so Right+Up from standing is a running jump and a jump held through a landing
happens on landing. Off, and in `pop_sot_check` without `--input-buffer`, nothing changes.

The save is the
original `POP2RECORD` store in `%LOCALAPPDATA%\PrinceOfPersiaSoT`. Game data is read from the
user's JAR (SHA-256 checked, other builds with the same layout offered with a warning).

The handset is reported as a Nokia 7650 (`microedition.platform`), which selects the game's
default key layout; the game's own "Controls" setting still changes it.
