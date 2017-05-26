Klooni 1010!
============
Play 1010! for free! Add your own themes! Contribute! Make the game yours!

[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="60">](https://f-droid.org/app/io.github.lonamiwebs.klooni)

This project is licensed under [GPLv3+](LICENSE).

**Table of contents**:

1. [Disclaimer](#disclaimer)
2. [Description](#description)
3. [Building](#building)
4. [Playing](#playing)
5. [Contributing](#contributing)
6. [Contributors](#contributors)

Disclaimer
----------
**Klooni 1010!** is a [libGDX](https://libgdx.badlogicgames.com/)-based game
which is a *klooni* from the original [1010!](http://1010ga.me/) game by the
[Gram Games](http://gram.gs/) team. I am **not affiliated** in any way with
them and this is only a project I made for fun, and to improve the gaming
experience of the open source community. If you really enjoy playing this,
**please support the original developers**. They made this project possible.

Description
-----------
*Klooni 1010!* is an open source copy of *1010!* with the same game-play
dynamics of the original game. I tried to mimic it as much as possible,
yet, not everything is exactly the same. For example, the customize screen
is different, there are no ads or in-app purchases, different sounds, etc.

Building
--------
Building the project should be very straight forward:

1. `git clone https://github.com/LonamiWebs/Klooni1010.git`.
2. `cd Klooni1010`
3. Now you can choose to either build for `desktop` or `android`:
   1. For desktop, use `./gradlew desktop:dist`
   2. For Android, use `./gradlew android:assembleRelease`
4. You're done! The generated files are under `build`:
   1. Desktop build is under `desktop/build/libs/*.jar`
   2. Android build is under `android/build/outputs/apk/*.apk`

Playing
-------
If you're on desktop, you should be able to play the game by either double
clicking the built game `.jar` (Windows) or running `java -jar {file}.jar`.

If you want to play the game on Android, move the built `.apk` to your phone's
internal memory, find it with an Android file explorer and install it.
Make sure you have `Unknown sources` (`Settings -> Security`) enabled!

Contributing
------------
Found a bug? Did you add new sounds? Explosions (that would be cool)? Did
you create a new theme you'd like to see in the game? That's **awesome**!
Every pull request is appreciated (or even drop an issue if you don't know
how these work), although they will probably be discussed before merging!

All you need to do is make sure you did not violate any of the following:
- New sounds must be either *yours* or be under a `Creative Commons` license.
  The same applies to new textures.
- There is no real rule when writing code to patch a bug or adding features.
  Just make sure it's readable enough and, if you write a method which name
  is not self-explanatory, document it and explain what it does.
- Make sure you add yourself (in **alphabetical order**) to the
  `CONTRIBUTORS.md` file :)

Contributors
------------
Check out the [`CONTRIBUTORS.md`](CONTRIBUTORS.md) file!
