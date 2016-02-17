# gdx-spriter
A [Spriter](http://www.brashmonkey.com) implementation exclusively for [LibGDX](https://libgdx.badlogicgames.com/).
Totally ripped off from loodakrawa's brilliant .Net implementation [SpriterDotNet](https://github.com/loodakrawa/SpriterDotNet).

## Yet another implementation, come on!
gdx-spriter aims at providing the smoothest out-of-the-box experience for LibGDX aficionados.
To do so, it relies on the efficiency of SpriterDotNet's design and borrows some of LibGDX's superpowers.

gdx-spriter should support roughly every feature supported by SpriterDotNet.
SCML as well as SCON formats are supported.

Bonus feature: character maps affect sprites *and* sounds, yay!

## Ok, still here and interested, what do I do now?
In order to bring your shiny Spriter animations into your beloved GDX game, please follow these steps:

### Before coding
* Add a reference to gdx-spriter (download source, gradle it with jitpack, ...).

### Initialization
* In your initialization code, create some SCMLReader (resp. SCONReader) for your SCML (SCON) file.
* Feed this reader a FileHandle of your Spriter file and get back a SpriterData object.
* Add a SpriterAssetProvider to this object (it will manage Texture/Sound loading), the simplest one is DefaultSpriterAssetProvider.
* For every SpriterEntity in your SpriterData, create a SpriterAnimator.

### Game loop
* Change properties of your SpriterAnimators at will.
* In your game loop, update your SpriterAnimators with SpriterAnimator.update(deltaTime).
* Then draw your SpriterAnimators with SpriterAnimator.draw(batch, shapeRenderer) -- the shape renderer is only used to display points and boxes and may be omitted.

### Finalization
* Don't forget to dispose your DefaultSpriterAssetProvider!

## Some GDX candy
Chances are you're using an AssetManager in your game. Good for you, gdx-spriter comes with full integration.
The Initialization and Finalization part seen above can then be made much much simpler:
* Skip everything from previous Initialization and Finalization steps.
* Add a SpriterDataLoader to your AssetManager.
* Load your Spriter file like you load anything else, using AssetManager.load(...).
* Here you go, now create your SpriterAnimators.

## Example
Please look at [the example](src/test/java/com/badlogic/gdx/spriter/SpriterExample.java) for a more thorough explanation.

## Demo
A demo displaying most of the library's features is also available. Feel free to tinker with it in the _test_ folder or use the _demoJar_ gradle task to build it.

## What's left to be done
Nothing's over yet, of course, and here are some points that are next in line when it comes to development schedule.
* Hitboxes all the way down.
* Always some issues to fix, _sigh_.

## I'm in, let me contribute!
Anything more to say?

Post an issue here on GitHub, a thread on any relevant forum or simply drop me a line by any means you deem good. I also take pull requests, _indeed_.

## Credits
All credits due to loodakrawa for the original .Net implementation, lucid from BrashMonkey for the official implementation and, of course, for the great piece of software that is Spriter.

UI in the demo wouldn't feel so glam without the work of [Kenney.nl](http://kenney.nl/) and [@Haedri](https://twitter.com/haedri) ([_here_](http://www.microbasic.net/2014/05/free-cc0-ui-elements-to-be-used-in-your-libgdx-games)).