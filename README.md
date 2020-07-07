# NBT [![](https://jitpack.io/v/piegamesde/nbt.svg)](https://jitpack.io/#piegamesde/nbt)

Named Binary Tag (NBT) library for Java at the end of a long fork chain. This has been forked from flow-nbt, which itself has its root in JNBT.

NBT is a tag based binary format designed to carry large amounts of binary data with smaller amounts of additional data. See [one of the wikis](https://wiki.vg/NBT) for more information

## Features

- Hide the pain through the usage of Java 8's `Optional`
- Parse whole Region files in the Anvil format
- Helper methods to analyse a chunk's content
- Option to keep all arrays as `byte[]` to improve performance

## Getting started

Go to [Jitpack.io](https://jitpack.io/#piegamesde/gson-fire) and follow the instructions.

Dependency string in Gradle:
```groovy
dependencies {
	implementation 'com.github.piegamesde:nbt:3.0.0'
}
```

Maven:
```xml
<dependency>
	<groupId>com.github.piegamesde</groupId>
	<artifactId>nbt</artifactId>
	<version>3.0.0</version>
</dependency>
```

## Usage
