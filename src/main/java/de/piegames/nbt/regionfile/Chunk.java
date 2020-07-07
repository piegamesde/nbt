package de.piegames.nbt.regionfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.piegames.nbt.CompoundMap;
import de.piegames.nbt.CompoundTag;
import de.piegames.nbt.DoubleTag;
import de.piegames.nbt.IntTag;
import de.piegames.nbt.ListTag;
import de.piegames.nbt.Tag;
import de.piegames.nbt.TagType;
import de.piegames.nbt.stream.NBTInputStream;
import de.piegames.nbt.stream.NBTOutputStream;

/**
 * Each instance of this class represents a Minecraft chunk within a Region file. The data is represented as a binary blob in form of a
 * {@link ByteBuffer}. Each object is self-contained and not linked to any physical file or other object. Each object is immutable and
 * should be treated as such. The data is set in the constructor</br>
 * The data must have a length of a multiple of 4096 bytes (to be able to write it to disk more easily). The four first bytes specify the
 * amount of actual data bytes in the buffer following. The fifth byte contains the compression method. All following bytes are NBT data.
 * 
 * @author piegames
 */
public class Chunk {
	/** The x coordinate of the chunk relative to its RegionFile's origin */
	public final int			x;
	/** The z coordinate of the chunk relative to its RegionFile's origin */
	public final int			z;
	/**
	 * The point in time when this chunk last got written. Equal to {@code (int) (System.currentTimeMillis() / 1000L)}
	 */
	public final int			timestamp;

	protected final ByteBuffer	data;

	/**
	 * Create a Chunk object by setting its data directly through a buffer.
	 * 
	 * @param x
	 *            The x coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param z
	 *            The z coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param data
	 *            The data of the chunk
	 * @throws IllegalArgumentException
	 *             if the coordinates are out of bounds or the data does not have a size multiple of 4096
	 * @author piegames
	 */
	public Chunk(int x, int z, int timestamp, ByteBuffer data) {
		if (x < 0 || z < 0 || x >= 32 || z >= 32)
			throw new IllegalArgumentException("Coordinates must be in range [0..32), but were x=" + x + ", z=" + z + ")");
		this.x = x;
		this.z = z;
		this.timestamp = timestamp;

		if ((data.capacity() & 4095) != 0)
			throw new IllegalArgumentException("Data buffer size must be multiple of 4096, but is " + data.capacity());
		this.data = Objects.requireNonNull(data);
	}

	/**
	 * Create a Chunk object by reading specified data from a region file (*.mca, *.mcr).
	 * 
	 * @param x
	 *            The x coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param z
	 *            The z coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param raf
	 *            The file channel to the region file from which to load the data
	 * @param start
	 *            The number of the 4096 byte sector where the chunk is located in the file. Don't forget that the first five bytes are used to
	 *            store the size and compression of the chunk. The position of the first byte of NBT data is thus {@code start*4096 + 5}.
	 * @param length
	 *            The amount of 4096 byte sectors to load. It should be large enough to contain all NBT data in that chunk or it will be
	 *            corrupted.
	 * @throws IllegalArgumentException
	 *             if the coordinates are out of bounds
	 * @author piegames
	 */
	Chunk(int x, int z, int timestamp, FileChannel raf, int start, int length) throws IOException {
		if (x < 0 || z < 0 || x >= 32 || z >= 32)
			throw new IllegalArgumentException("Coordinates must be in range [0..32), but were x=" + x + ", z=" + z + ")");
		this.x = x;
		this.z = z;
		this.timestamp = timestamp;

		data = ByteBuffer.allocate(4096 * length);
		raf.read(data, 4096 * start);
		data.flip();
	}

	/**
	 * Create a Chunk object by filling the NBT tag's data to a {@link ByteBuffer} using the specified compression method.
	 * 
	 * @param x
	 *            The x coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param z
	 *            The z coordinate of the chunk within its region file. Must be in the range of [0..32) because region files are always 32*32
	 *            chunks large.
	 * @param data
	 *            The NBT data the chunk will contain. Should be a {@link CompoundTag}
	 * @param compression
	 *            The compression to use
	 * @throws IllegalArgumentException
	 *             if the coordinates are out of bounds
	 * @author piegames
	 */
	public Chunk(int x, int z, int timestamp, Tag<?> data, byte compression) throws IOException {
		if (x < 0 || z < 0 || x >= 32 || z >= 32)
			throw new IllegalArgumentException("Coordinates must be in range [0..32), but were x=" + x + ", z=" + z + ")");
		this.x = x;
		this.z = z;
		this.timestamp = timestamp;

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(4096)) {
			try (NBTOutputStream out = new NBTOutputStream(baos, compression)) {
				out.writeTag(data);
				out.flush();
			}

			byte[] bytes = baos.toByteArray();
			int sectionLength = (bytes.length + 5) / 4096 + 1;
			this.data = ByteBuffer.allocate(sectionLength * 4096);
			this.data.putInt(bytes.length + 1);
			this.data.put(compression);
			this.data.put(bytes);
			this.data.flip();
		}
	}

	/**
	 * Create a Chunk object identical to an existing one, except for a different timestamp.
	 * 
	 * @param data
	 *            The chunk to clone
	 * @author piegames
	 */
	public Chunk(Chunk data, int timestamp) {
		this.x = data.x;
		this.z = data.z;
		this.timestamp = timestamp;

		this.data = data.data;
	}

	/**
	 * Returns the compression method used in this chunk as specified by the format. This value corresponds to the compression that an
	 * {@link NBTInputStream} takes in its constructor.
	 */
	public byte getCompression() {
		return data.get(4);
	}

	/**
	 * The real length of the NBT data in this chunk in bytes.
	 */
	public int getRealLength() {
		return data.getInt(0) - 1;
	}

	/**
	 * Get the amount of 4kiB-sized sectors on the hard disk that would be required to save this chunk.
	 */
	public int getSectorLength() {
		return (getRealLength() + 5) / 4096 + 1;
	}

	/**
	 * Returns the {@link ByteBuffer} containing all the data in this chunk, including the five bytes before the actual NBT data. It will always
	 * contain a multiple of 4096 bytes. Altering its content will result in undefined behavior!
	 */
	public ByteBuffer getData() {
		return data;
	}

	/**
	 * Open an {@link NBTInputStream} for reading the NBT data contained in that chunk.
	 */
	public NBTInputStream getInputStream() throws IOException {
		return new NBTInputStream(new ByteArrayInputStream(data.array(), 5, getRealLength()), getCompression());
	}

	/**
	 * Reads the NBT chunk data and returns it. The normally nameless root tag will be renamed to "chunk".
	 */
	public CompoundTag readTag() throws IOException {
		try (NBTInputStream nbtIn = getInputStream();) {
			return new CompoundTag("chunk", ((CompoundTag) nbtIn.readTag()).getValue());
		}
	}

	/**
	 * Return a timestamp in the format used by Minecraft representing the point in time this method was called
	 * 
	 * @see #timestamp
	 */
	public static int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public static int bitsPerIndex(long[] blocks) {
		/* There are {@code 16*16*16=4096} blocks in each chunk, and a long has 64 bits */
		return blocks.length * 64 / 4096;
	}

	/**
	 * @deprecated Renamed to {@link #extractFromLong1_13(long[], int, int)}.
	 * @see #extractFromLong1_13(long[], int, int)
	 * @see #extractFromLong1_16(long[])
	 */
	@Deprecated
	public static long extractFromLong(long[] blocks, int i, int bitsPerIndex) {
		return extractFromLong1_13(blocks, i, bitsPerIndex);
	}

	/**
	 * Extract a palette index from the long array. This data is located at {@code /Level/Sections[i]/BlockStates}.
	 *
	 * @param blocks
	 *            a long array containing all the block states as Minecraft encodes them to {@code /Level/Sections[i]/BlockStates} within each
	 *            section of a chunk.
	 * @param i
	 *            The index of the block to be extracted. Since the data is mapped XZY, {@code i = x | (z<<4) | (y<<8)}.
	 * @param bitsPerIndex
	 *            The amount of bits each index has. This is to avoid redundant calculation on each call.
	 *
	 * @see #bitsPerIndex(long[])
	 * @see Palette1_13 {@code Palette1_13} If this method pops up in your profiler results, the Palette class does the same thing but in faster.
	 * @see #extractFromLong1_16(long[],int) {@code extractFromLong1_16} for newer Minecraft versions
	 * @author piegames
	 */
	public static long extractFromLong1_13(long[] blocks, int i, int bitsPerIndex) {
		/* Bit shifts and logical ands are faster division and modulo on power of two numbers */
		
		/* The bit within the long where our value starts. Counting from the right LSB (!). */
		int startByteBit = (bitsPerIndex * i) & 63;
		/* The first long containing the desired value */
		int startIndex = (bitsPerIndex * i) >> 6;
		/* The last long containing the desired value. -1 to make the range inclusive */
		int endIndex = ((bitsPerIndex * (i + 1)) - 1) >> 6;
		/* Use bit shifting and & bit masking to extract bit sequences out of longs as numbers */
		/* Technically, we could pass this one as parameter too like bitsPerIndex, but backwards compatibility */
		long bitMask = 0xFFFFFFFFFFFFFFFFL >>> (64 - bitsPerIndex);
		
		if (startIndex == endIndex)
			/* Shortcut: Only evaluate one long if the desired value is not split across values. */
			return((blocks[startIndex] >>> startByteBit) & bitMask);
		else
			return(((blocks[startIndex] >>> startByteBit)
					| (blocks[endIndex] << (64 - startByteBit))) & bitMask);
	}


	/**
	 * Convert a packed palette to actual indices. This data is located at {@code /Level/Sections[i]/BlockStates}.
	 *
	 * @param blocks
	 *            a long array containing all the block states as Minecraft encodes them to {@code /Level/Sections[i]/BlockStates} within each
	 *            section of a chunk.
	 * @param paletteSize
	 *            The number of entries in the palette used to encode these blocks
	 *
	 * @see #extractFromLong1_13(long[],int,int) {@code extractFromLong1_13} for older Minecraft versions
	 * @author piegames
	 */
	@SuppressWarnings("fallthrough")
	public static long[] extractFromLong1_16(long[] blocks, int paletteSize) {
		int bitsPerIndex = Math.max(4, Integer.SIZE - Integer.numberOfLeadingZeros(paletteSize - 1));
		int shortsPerLong = Math.floorDiv(64, bitsPerIndex);
		int mask = (1 << bitsPerIndex) - 1;

		long[] result = new long[4096];

		/* Special case the most common packing sizes */
		if (bitsPerIndex == 4) {
			int index = 0;
			for (long l : blocks) {
				// Bit 64 ............................................................... Bit 0
				// ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ (from right to left)
				// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
				result[index++] = (l >> 0x00) & 0xF;
				result[index++] = (l >> 0x04) & 0xF;
				result[index++] = (l >> 0x08) & 0xF;
				result[index++] = (l >> 0x0C) & 0xF;
				result[index++] = (l >> 0x10) & 0xF;
				result[index++] = (l >> 0x14) & 0xF;
				result[index++] = (l >> 0x18) & 0xF;
				result[index++] = (l >> 0x1C) & 0xF;
				result[index++] = (l >> 0x20) & 0xF;
				result[index++] = (l >> 0x24) & 0xF;
				result[index++] = (l >> 0x28) & 0xF;
				result[index++] = (l >> 0x2C) & 0xF;
				result[index++] = (l >> 0x30) & 0xF;
				result[index++] = (l >> 0x34) & 0xF;
				result[index++] = (l >> 0x38) & 0xF;
				result[index++] = (l >> 0x3C) & 0xF;
			}
		} else if (bitsPerIndex == 5) {
			int index = 0;
			for (int i = 0; i < blocks.length - 1; i++) {
				long l = blocks[i];
				// Bit 64 ............................................................... Bit 0
				// ....┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ (from right to left)
				// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
				result[index++] = (l >> 0) & 0x1F;
				result[index++] = (l >> 5) & 0x1F;
				result[index++] = (l >> 10) & 0x1F;
				result[index++] = (l >> 15) & 0x1F;
				result[index++] = (l >> 20) & 0x1F;
				result[index++] = (l >> 25) & 0x1F;
				result[index++] = (l >> 30) & 0x1F;
				result[index++] = (l >> 35) & 0x1F;
				result[index++] = (l >> 40) & 0x1F;
				result[index++] = (l >> 45) & 0x1F;
				result[index++] = (l >> 50) & 0x1F;
				result[index++] = (l >> 55) & 0x1F;
			}
			{ /* Last iteration – almost Duff's device! */
				long lastData = blocks[blocks.length - 1];
				switch (result.length - index) {
				case 10:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 9:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 8:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 7:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 6:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 5:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 4:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 3:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 2:
					result[index++] = lastData & 0x1F;
					lastData >>= 5;
				case 1:
					result[index++] = lastData & 0x1F;
				case 0:
					break;
				default:
					throw new InternalError();
				}
			}
		} else if (bitsPerIndex == 6) {
			int index = 0;
			for (int i = 0; i < blocks.length - 1; i++) {
				long l = blocks[i];
				// Bit 64 ............................................................... Bit 0
				// ....┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ (from right to left)
				// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
				result[index++] = (l >> 0) & 0x3F;
				result[index++] = (l >> 6) & 0x3F;
				result[index++] = (l >> 12) & 0x3F;
				result[index++] = (l >> 18) & 0x3F;
				result[index++] = (l >> 24) & 0x3F;
				result[index++] = (l >> 30) & 0x3F;
				result[index++] = (l >> 36) & 0x3F;
				result[index++] = (l >> 42) & 0x3F;
				result[index++] = (l >> 48) & 0x3F;
				result[index++] = (l >> 54) & 0x3F;
			}
			{ /* Last iteration – almost Duff's device! */
				long lastData = blocks[blocks.length - 1];
				switch (result.length - index) {
				case 10:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 9:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 8:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 7:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 6:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 5:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 4:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 3:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 2:
					result[index++] = lastData & 0x3F;
					lastData >>= 6;
				case 1:
					result[index++] = lastData & 0x3F;
				case 0:
					break;
				default:
					throw new InternalError();
				}
			}
		} else {
			/* Fallback: general case (yes, the whole method could be this short) */
			int index = 0;
			for (long l : blocks) {
				for (int s = 0; s < shortsPerLong && index < 4096; s++) {
					result[index++] = l & mask;
					l >>= bitsPerIndex;
				}
			}
		}
		return result;
	}

	/** Incubating utility method, use with care */
	@SuppressWarnings("unchecked")
	public static void moveChunk(CompoundTag level, int sourceX, int sourceZ, int destX, int destZ) {
		CompoundMap value = level.getValue();
		/* The difference in blocks between the two chunks */
		int diffX = (destX - sourceX) << 4;
		int diffY = 0;
		int diffZ = (destZ - sourceZ) << 4;

		value.put(new IntTag("xPos", destX));
		value.put(new IntTag("zPos", destZ));

		/* Update entities */
		for (CompoundTag entity : ((ListTag<CompoundTag>) value.get("Entities")).getValue()) {
			List<DoubleTag> pos = ((ListTag<DoubleTag>) entity.getValue().get("Pos")).getValue();
			entity.getValue().put(new ListTag<>("Pos", TagType.TAG_DOUBLE,
					Arrays.asList(
							new DoubleTag(null, pos.get(0).getValue() + diffX),
							new DoubleTag(null, pos.get(1).getValue() + diffY),
							new DoubleTag(null, pos.get(2).getValue() + diffZ))));
		}
		/* Update tile entities */
		for (CompoundTag tileEntity : ((ListTag<CompoundTag>) value.get("TileEntities")).getValue()) {
			CompoundMap map = tileEntity.getValue();
			map.put(new IntTag("x", ((IntTag) map.get("x")).getValue() + diffX));
			map.put(new IntTag("y", ((IntTag) map.get("y")).getValue() + diffY));
			map.put(new IntTag("z", ((IntTag) map.get("z")).getValue() + diffZ));
		}
		/* Update tile ticks */
		for (CompoundTag tileTick : ((ListTag<CompoundTag>) value.get("TileTicks")).getValue()) {
			CompoundMap map = tileTick.getValue();
			map.put(new IntTag("x", ((IntTag) map.get("x")).getValue() + diffX));
			map.put(new IntTag("y", ((IntTag) map.get("y")).getValue() + diffY));
			map.put(new IntTag("z", ((IntTag) map.get("z")).getValue() + diffZ));
		}
		/* Update liquid ticks */
		for (CompoundTag liquidTick : ((ListTag<CompoundTag>) value.get("LiquidTicks")).getValue()) {
			CompoundMap map = liquidTick.getValue();
			map.put(new IntTag("x", ((IntTag) map.get("x")).getValue() + diffX));
			map.put(new IntTag("y", ((IntTag) map.get("y")).getValue() + diffY));
			map.put(new IntTag("z", ((IntTag) map.get("z")).getValue() + diffZ));
		}
	}
}
