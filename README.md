# BlockSmith

BlockSmith is a simple [Minecraft](https://minecraft.net) clone written in Scala, using [LWJGL](https://www.lwjgl.org). It is based on [Mitchell Kember](https://github.com/mk12)'s [Mycraft](https://github.com/bbarker/mycraft).

Although I'm not actively looking for help (yet), contributions and advice are certainly appreciated! At the moment I aim to keep features fairly similar to Minecraft (similar to [TrueCraft](https://truecraft.io/)), though I'm not tied to a particular version, and I would like to support alternative builds that allow for experimentation or customization.

## Building

### SBT

You can build and run simply using [SBT](http://www.scala-sbt.org/) by doing the following from the command line in the project directory after you have installed `sbt` on your `PATH`:

```
sbt 
compile
project BlockSmithJVM
run
```


## Future Goals

### Support anti-cheating features

I haven't even thought about this much or looked into it, but I would hope to have a system that prevents the kind of "x-ray vision" mods that are found in Minecraft. Please post an issue if you know of a solution, possible solution, or other potential cheating mechanisms that are in Minecraft.

### Support multiple targets

In principle, we could abstract to run on native hardware using [Scala Native](http://www.scala-native.org/), in the browser using [Scala.js](https://www.scala-js.org/) and [voxel.js](http://voxeljs.com/), and of course on the JVM (or Android, etc.)*. 

## License

"Minecraft" is an official trademark of Mojang AB. This work is not formally related to, endorsed by, or affiliated with Minecraft or Mojang AB.

Original work © 2012 Mitchell Kember

Modified work © 2016 Brandon Barker

BlockSmith is available under the MPL 2.0 License; see [LICENSE](LICENSE.md) for details.

BlockSmith v0.0.1 and earlier is also available under the MIT License; see [LICENSE-Original](LICENSE-Original.md) for details.

## Acknowledgements 

* [Mycraft](https://github.com/bbarker/mycraft) ([Mitchell Kember](https://github.com/mk12)) - providing a very small Java-based voxel game to help bootstrap the project.
* [TrueCraft](https://truecraft.io/) ([Drew](https://github.com/SirCmpwn) [DeVault](https://drewdevault.com/)) - for providing an implementation of a similar voxel-game in C# that helps serve as a reference and for occasional discussion. 
* [Minecraft](https://minecraft.net/) ([Notch](https://en.wikipedia.org/wiki/Markus_Persson)) - obviously for making it all possible.


