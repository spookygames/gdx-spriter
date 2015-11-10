# gdx-spriter
A [Spriter](http://www.brashmonkey.com) implementation exclusively for [LibGDX](https://libgdx.badlogicgames.com/).
Totally ripped off from loodakrawa's brilliant C# implementation [SpriterDotNet](https://github.com/loodakrawa/SpriterDotNet).

## Yet another implementation, come on!
gdx-spriter aims at providing the smoothest out-of-the-box experience for LibGDX aficionados.
To do so, it relies on SpriterDotNet efficiency, skips any form of cross-framework genericity and uses most of LibGDX's specificities.

gdx-spriter should support every feature supported by SpriterDotNet.
SCML as well as SCON formats are supported.

## Ok, still here and interested, what do I do now?
In order to bring your shiny Spriter animations into your beloved GDX game, please proceed as follows:

### Before coding
* Add a reference to gdx-spriter (download source, gradle it with jitpack, ...)

### Initialization
* In your initialization code, create some SCMLReader (resp. SCONReader) for your SCML (SCON) file
* Feed this reader a FileHandle of your Spriter file and get back a SpriterData object
* Add a SpriterAssetProvider to this object (it will manage Texture/Sound loading), the simplest one is DefaultSpriterAssetProvider
* For every SpriterEntity in your SpriterData, create a SpriterAnimator

### Game loop
* Change properties of your SpriterAnimators at will
* In your game loop, update your SpriterAnimators with SpriterAnimator.update(deltaTime)
* Then draw your SpriterAnimators with SpriterAnimator.draw(batch, shapeRenderer)

### Finalization
* Don't forget to dispose your DefaultSpriterAssetProvider!

## Some GDX candy
Chances are you're using an AssetManager in your game. Good for you, gdx-spriter comes integrated with it.
The Initialization and Finalization part can then be made much much simpler:
* Skip everything from previous Initialization and Finalization steps
* Add a SpriterDataLoader to your AssetManager
* Load your Spriter file like you load anything else, using AssetManager.load(...)
* Here you go, now create your SpriterAnimators

### Example
Please look at [the example](https://github.com/thorthur/gdx-spriter/blob/master/src/test/java/com/badlogic/gdx/spriter/SpriterExample.java) for a more thorough explanation.


## I'm in, let me contribute!
Anything more to say? Post an issue here on GitHub, a thread on any relevant forum or simply drop me a line by any means you deem good.

## Credits
All credits due to loodakrawa for the original C# implementation, lucid from BrashMonkey for the official implementation and, of course, for the great piece of software that is Spriter.