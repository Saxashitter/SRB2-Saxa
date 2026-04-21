# *SRB2 Android (Saxashitter, romoney5)*

[Sonic Robo Blast 2](https://srb2.org/) is a 3D Sonic the Hedgehog fangame based on a modified version of [Doom Legacy](http://doomlegacy.sourceforge.net/).

[SRB2 Android](https://github.com/Saxashitter/SRB2-Android-Port) is a unofficial remake of the port by Lactozilla, and also unofficially continued by bitten2up and StarManiaKG. It aims to offer a more PC-like experience while polishing up the rough edges of the port that came before.

This port aims to stay as vanilla as we can make it, with QoL changes remaining in external branches. The only exception to this rule is the current branch, which houses the Android controls, and the really smooth UI done by our buddy **sanicblack!**

## *Features*
- ***Touch controls made in Java***
  - Both the controls and the game itself are handled separately, with very few times where C has to be used. This means that these controls are insanely easy to drop into other SRB2 ports, **and even other SDL2 games.**
- ***Minimum conflicts with SRB2's vanilla codebase.***
  - in the **port** branch, C is only touched in the game's backend whenever it's not functioning correctly. However, there are other branches that contain modifications.
  - ***vanilla***
    - Only provides the basic code needed to get the game working. All praise goes to romoney5! Without him, we wouldn't be here.
  - ***port***
    - The star of the show! This contains all the code we use to get touch controls (and more) working.
  - ***any-resolution***
    - Adds full-screen to the port, similar to Lactozilla.

## *Dependencies*
- SDL2 (Linux/OS X only)
- SDL2-Mixer (Linux/OS X only)
- libupnp (Linux/OS X only)
- libgme (Linux/OS X only)
- libopenmpt (Linux/OS X only)
- Android Studio (We used Panda 3)
- Gradle JDK (JetBrains Runtime 21.0.10) (Comes with Android Studio)

## Compiling
### *PC*
> see [SRB2 Wiki/Source code compiling](http://wiki.srb2.org/wiki/Source_code_compiling).
PC instructions should work just fine with this repo!
### *Android*
> see [TO-DO: Wiki with compilation instructions.](https://www.google.com)

## *Disclaimer*
We are in no way affiliated with Sonic Team Jr. Lactozilla, SEGA, or Sonic Team, and don't claim ownership of anything here. This port was made out of love for the game.

Sonic Team Junior is in no way affiliated with SEGA or Sonic Team. We do not claim ownership of any of SEGA's intellectual property used in SRB2.

## *Why is all the cool stuff restricted to 16+ players?*
Lactozilla's port had a horrible playerbase. So much so, that multiple players, ***and even the dev themselves*** left the SRB2 community due to harassment, or being general nuisances. To circumvent this, I'm locking the full port behind the Blastdroid Discord server.

If you know how to compile, then good for you. But please don't distribute the 16+ build publicly. Instead, talk with me on Discord so I can make a dedicated channel for you in Blastdroid!