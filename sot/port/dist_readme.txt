Prince of Persia: The Sands of Time - desktop port (unofficial)
==============================================================

This package contains no game data. You need your own copy of Gameloft's
"Prince of Persia: The Sands of Time" for J2ME (a .jar file).

Which JAR
---------
This port plays the Nokia Series 60 build (176x208, MIDlet 1.1.0,
PrinceOfPersia-TheSandsOfTime.jar), in English: eight levels, sand powers,
the Gameloft ringing-tone music and sound effects.
    SHA-256 40e7115a32e9e584106bc6565ed78cd6af189b41a426f5d61f53bbf8d0ac3e9f

Other builds with the same resource layout are offered with a warning and are
untested.

Starting
--------
1. Run PrinceOfPersiaSoT.exe (the launcher).
2. Choose your .jar when it asks -- or put the .jar next to
   PrinceOfPersiaSoT.exe before starting it, and the supported build is picked
   up by itself. The launcher checks it and keeps a copy in game\, so the
   original can be moved or deleted.
3. Pick a window size or fullscreen, and press Play.

The launcher also updates this folder when a new release is published: an
Update button appears beside the version number. Your JAR (game\), the
launcher's choices (launcher.cfg), settings and saves are never touched.

bin\pop_sot.exe is the game itself and can be started directly too
(--jar FILE, or a .jar next to it).

Settings
--------
Open Settings > PC options in the game's main menu or in-game menu:
  Display   width (auto, 176 original, 278, 333, 370, 486), frame rate,
            smooth motion, fullscreen, scaling, fps counter, pause in background
  Audio     volume, mute
  Keyboard  select an action, press Enter, then the new key
  Gamepad   select an action, press Enter, then the new button
In that screen Up/Down choose, Left/Right change a value, Enter activates,
Esc goes back.

Default controls
----------------
Move               arrows or A / D           d-pad or left stick
Jump / climb       Space, Up or W            A
Crouch             Down or S                 d-pad / stick down
Sword / action     J, E, Z or Ctrl           X, B or RT
Select (menus)     Enter                     A
Menu / back        Esc or Backspace          Start, Back or Y
Right softkey      Tab
Phone keypad       0-9 and the numpad still work as the phone's keys
Roll               tap a direction twice quickly (once the power is earned)

Shortcuts: F1 help, F3 fps counter, F6 mute, F8 width, F9 frame rate,
F10 smooth motion, F11 or Alt+Enter fullscreen.

The game always simulates at its original rate (one step every 80 ms);
higher frame rates only add in-between frames. Width 176 with smooth motion
off is the original presentation.

Files
-----
Settings and the save (POP2RECORD) live in %LOCALAPPDATA%\PrinceOfPersiaSoT.
A settings.ini placed next to bin\pop_sot.exe takes precedence. Run the game
with --help for command line options.

bin\pop_sot_check.exe is the project's verification tool; it is not needed
to play.
