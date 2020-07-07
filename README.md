# NBT [![](https://jitpack.io/v/piegamesde/nbt.svg)](https://jitpack.io/#piegamesde/nbt)

Named Binary Tag (NBT) library for Java at the end of a long fork chain. This has been forked from flow-nbt, which itself has its root in JNBT.

NBT is a tag based binary format designed to carry large amounts of binary data with smaller amounts of additional data. See [one of the wikis](https://wiki.vg/NBT) for more information

## Features

- Hide the pain through the usage of Java 8's `Optional`
- Parse whole Region files in the Anvil format
- Helper methods to analyse a chunk's content (e.g. parse the Minecraft packed palette data)
- Option to keep all arrays as `byte[]` to improve performance
- Compatible with Minecraft 1.16

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

### NBT file usage

Entry points to the API are the `NBTInputStream` and `NBTOutputStream` classes, which are used to read or write one complex NBT tag. Different data compressions are available. NBT data is stored in a `Tag`. A tag has a name and a value, there is one sub-class for each tag type. Because casting yourself around the NBT tree is not a lot of fun, there are a ton of helper methods that return the cast type if possible, or an empty `Optional` if not. This leverages the whole power and syntax sugar of Java's `Optional` class.

If you don't like `Optional`, you can still call `Tag::getValue` and manually cast things around. There are cases where this is actually preferable.

```java
try (NBTInputStream in = new NBTInputStream(Files.newInputStream(myPath), NBTInputStream.GZIP_COMPRESSION)) {
	/* Read all the data into a tag */
	Tag<?> dataRaw = in.readTag();
	/* Those helper method give an `Optional<FooTag>` that's better to use than a plain downcast */
	CompoundTag data = dataRaw.getAsCompoundTag().get();
	
	// Do something with the data in here:
	
	/* Directly access child values with the correct type, if present */
	int version = data.getIntValue("version").get();
	/* Get sub tags (not only their value) as well */
	CompoundTag subData = data.getCompoundTag("subDataTag");
	
	/* Reald-world use case example (simplified): get the player dimension of a Minecraft level.dat, but the data type changed from int to string over time. */
	var dimension = subData.getIntValue("Dimension")
			.map(dim -> (new String[] { "Overworld", "Nether", "End"})[dim])
			.orElseGet(() -> subData.getStringValue("Dimension").get());
}
```

### Region/Anvil file usage

A Minecraft save file can be loaded using class `RegionFile`. It allows reading and writing chunks and metadata. The (lazily) loaded data of a chunk is represented through a `Chunk` object, which may give access to its data (usually containing a `ComoundTag`). Saving modified chunks back to the region file is also supported.

```java
try (RegionFile regionFile = new RegionFile(Paths.get(myWorld, "region", "r.0.0.mca"))) {
	for (Chunk chunk : regionFile.listChunks()) {
		CompoundTag data = chunk.readTag();
	}
}
```

### Primitive array optimization

- Pass `rawArrays = true` when initializing the `NBTInputStream`
- All primitive arrays will now be parsed as `ByteArrayTag`
- Use methods like `ByteArrayTag::getShortArrayValue` to lazily convert the bytes as needed
- If a specialized `Tag` subclass is somehow required, this can be done as well using e.g. `ByteArrayTag::getAsIntArrayTag`

```java
try (NBTInputStream in = new NBTInputStream(Files.newInputStream(myPath), NBTInputStream.GZIP_COMPRESSION, true)) {
	CompoundTag data = in.readTag().getAsCompoundTag().get();
	
	ByteArrayTag array = data.getByteArrayValue("uuid").get();
	int[] uuid = array.getIntArrayValue();
}
```
