# NBT

Named Binary Tag (NBT) library for Java at the end of a long fork chain. This has been forked from flow-nbt (which has been moved to the [Sponge project](https://github.com/SpongePowered/nbt)), which itself has its root in JNBT.

NBT is a tag based binary format designed to carry large amounts of binary data with smaller amounts of additional data. See [https://wiki.vg/NBT](one of the wikis) for more information

## Features

- Hide the pain through the usage of Java 8's `Optional`
- Parse whole Region files in the Anvil format
- Helper methods to analyse a chunk's content
- Option to keep all arrays as `byte[]` to improve performance

## Usage
