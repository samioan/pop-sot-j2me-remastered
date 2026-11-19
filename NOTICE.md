# Provenance

Prince of Persia is a trademark of Ubisoft. The J2ME game *Prince of Persia: The Sands of Time*
and its original code, data and artwork belong to their respective rights holders, including
Ubisoft and Gameloft. This workspace is an independent reverse-engineering and porting project,
not an official release. No rights to the original game or recovered source are granted by this
setup.

The supplied PrinceOfPersia-TheSandsOfTime.jar is kept in roms/ for the reference workflow. JARs,
extracted/decoded assets, screenshots, analysis output and fetched tool binaries are ignored by
git. Raw decompilation and renamed reference Java follow the sibling projects' tracked-source
convention; recovered code retains its original ownership.

The extraction/decompilation infrastructure, symbol renamer, Java-to-C++ translator, runtime,
Win32 host, launcher and VS discovery were adapted from the user's own sibling workspaces
(sod-remastered, itself from gow-betrayal, rac-j2me, tes-travels and shadowkey). No license for
that code is inferred or assigned here. Vineflower is Apache-2.0 software; MIDP/CLDC/Nokia stub
binaries retain their respective owners' terms.

The launcher's banner and icon (sot/port/launcher/assets) and .github/social-preview.jpg are made
by tools/make_art.py (and a fitted copy for the preview) from "Prince of Persia: Remastered" key art
supplied by the project owner. It shows the Prince of Persia logo and character, which belong to
Ubisoft; it is used to identify the game, as on the project page, not as a claim to it.

The native build/package contains the launcher, the game executable, its checker, a readme and
this notice only. Players' game data stays separate: the launcher copies the player's own JAR into
the install at run time, and updates never replace it.
