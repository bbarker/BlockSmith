# BlockSmith

BlockSmith is a simple [Minecraft](https://minecraft.net) clone written in Scala, using [LWJGL](https://www.lwjgl.org). It is based on [Mitchell Kember](https://github.com/mk12)'s [Mycraft](https://github.com/bbarker/mycraft).

Although I'm not actively looking for help at the moment, help and contribution are certainly appreciated! At the moment I aim to keep features fairly similar to MineCraft (similar to [TrueCraft](https://truecraft.io/)), though I'm not tied to a particular version, and I would like to support alternative builds that allow for experimentation or customization.

## Building

### SBT

You can build and run simply using [SBT](http://www.scala-sbt.org/) by doing `sbt run` from the directory, after you have installed `sbt` on your `PATH`.


## Future Goals

### Support anti-cheating features

I honestly haven't even thought about this much or looked into it, but I would hope to engineer a system that prevents the kind of "x-ray vision" mods that are found in Minecraft. Post an issue if you know of a solution, possible solution, or other potential cheating mechanisms that are in Minecraft.

### Support multiple (virtual) machines.

In principle, we could abstract to run on native hardware using [Scala Native](http://www.scala-native.org/), in the browser using [Scala.js](https://www.scala-js.org/) and [voxel.js](http://voxeljs.com/), and of course on the JVM (or Android, etc.)*. 

## License

"Minecraft" is an official trademark of Mojang AB. This work is not formally related to, endorsed by, or affiliated with Minecraft or Mojang AB.

Original work © 2012 Mitchell Kember

Modified work © 2016 Brandon Barker

BlockSmith is available under the MPL 2.0 License; see [LICENSE](LICENSE.md) for details.

BlockSmith v0.0.1 and earlier is also available under the MIT License; see [LICENSE-Origina](LICENSE-Original.md) for details.
