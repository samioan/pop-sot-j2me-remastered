<!--
The body of every GitHub release, with {{TAG}} substituted by .github/workflows/release.yml.
-->
## Prince of Persia: The Sands of Time Remastered {{TAG}}

A Windows port of Gameloft's *Prince of Persia: The Sands of Time* (2003, J2ME, Nokia Series 60),
the original game's own code translated to native C++, with widescreen, smooth frame rates,
keyboard and gamepad. All eight levels, the sand powers, the menus and saving work.

### Getting it running

1. Download `PrinceOfPersiaSoTRemastered-{{TAG}}-win64.zip` below and unzip it anywhere.
2. Run **PrinceOfPersiaSoT.exe**.
3. Point it at your own copy of the Series 60 (176x208, MIDlet 1.1.0) `.jar`, or put the `.jar`
   next to `PrinceOfPersiaSoT.exe` before starting it.
4. Press **Play**.

Installed copies update themselves: the launcher offers each new release.

### This download does not include the game

It cannot: the game belongs to its rights holders. You supply your own copy.

### PC options

**Settings > PC options**, in the main menu and the in-game menu, has **Width** (176 original,
Auto, 4:3, 16:10, 16:9, 21:9: widescreen shows more of the level, nothing is stretched), **Frame
rate** (the game keeps its original speed; extra frames are interpolated), fullscreen and scaling,
audio, rebindable keyboard and gamepad controls, and an **Input buffer** that lets a held jump or
sword press count once the prince can act. F1 in the game lists the controls.

### Notes

- Windows 64-bit. No installer and no Visual C++ redistributable: unzip and run.
- Your JAR is copied into `game\` beside the launcher. Settings and saves live in
  `%LOCALAPPDATA%\PrinceOfPersiaSoT`.
- No complete human playthrough has been recorded; please report anything that goes wrong at
  https://github.com/samioan/pop-sot-j2me-remastered/issues.

*Prince of Persia* is a trademark of Ubisoft; the J2ME game was made by Gameloft. This project is
not affiliated with or endorsed by either.
