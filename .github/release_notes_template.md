<!--
The body of every GitHub release, with {{TAG}} substituted by .github/workflows/release.yml.
-->
## Prince of Persia: The Sands of Time Remastered {{TAG}}

A Windows port of Gameloft's *Prince of Persia: The Sands of Time* (2003, J2ME, Nokia Series 60),
the original game's own code translated to native C++, with widescreen, smooth frame rates,
keyboard and gamepad.

### Getting it running

1. Download `PrinceOfPersiaSoTRemastered-{{TAG}}-win64.zip` below and unzip it anywhere.
2. Run **PrinceOfPersiaSoT.exe**.
3. Point it at your own copy of the Series 60 (176x208) `.jar`, or put the `.jar` next to
   `PrinceOfPersiaSoT.exe` before starting it.
4. Press **Play**.

Installed copies update themselves: the launcher offers each new release.

### This download does not include the game

It cannot: the game belongs to its rights holders. You supply your own copy.

### Notes

- Windows 64-bit. No installer and no Visual C++ redistributable: unzip and run.
- Your JAR is copied into `game\` beside the launcher. Settings and saves live in
  `%LOCALAPPDATA%\PrinceOfPersiaSoT`.
- No complete playthrough has been recorded yet; please report anything that goes wrong.

*Prince of Persia* is a trademark of Ubisoft; the J2ME game was made by Gameloft. This project is
not affiliated with or endorsed by either.
