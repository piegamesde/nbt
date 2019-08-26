/*
 * This file is part of Flow NBT, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2011 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.piegames.nbt.stream;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import de.piegames.nbt.*;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object graph of subclasses of the {@link Tag} object.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may be found at <a href="https://flowpowered.com/nbt/spec.txt">
 * https://flowpowered.com/nbt/spec.txt</a>.
 */
public final class NBTInputStream implements Closeable {

	/**
	 * Flag indicating that the given data stream is not compressed.
	 */
	public static final int		NO_COMPRESSION		= 0;
	/**
	 * Flag indicating that the given data will be compressed with the GZIP compression algorithm. This is the default compression method used
	 * to compress nbt files. Chunks in Minecraft Region/Anvil files with compression method {@code 1} (see the respective format documentation)
	 * will use this compression method too, although this is not actively used anymore.
	 */
	public static final int		GZIP_COMPRESSION	= 1;
	/**
	 * Flag indicating that the given data will be compressed with the ZLIB compression algorithm. This is the default compression method used
	 * to compress the nbt data of the chunks in Minecraft Region/Anvil files, but only if its compression method is {@code 2} (see the
	 * respective format documentation), which is default for all newer versions.
	 */
	public static final int		ZLIB_COMPRESSION	= 2;

	private final DataInput		dataIn;
	private final InputStream	inputStream;
	private boolean rawArrays = true;

	/**
	 * Creates a new {@link NBTInputStream}, which will source its data from the specified input stream. This assumes the stream is compressed.
	 *
	 * @param is
	 *            The input stream.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is) throws IOException {
		this(is, GZIP_COMPRESSION, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which will source its data from the specified input stream. The stream may be wrapped into a
	 * decompressing input stream depending on the chosen compression method. This assumes the stream uses big endian encoding.
	 *
	 * @param is
	 *            The input stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link #NO_COMPRESSION}, {@link #GZIP_COMPRESSION} or
	 *            {@link #ZLIB_COMPRESSION}.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, int compression) throws IOException {
		this(is, compression, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which will source its data from the specified input stream. The stream may be wrapped into a
	 * decompressing input stream depending on the chosen compression method. This assumes the stream uses big endian encoding.
	 *
	 * @param is
	 *            The input stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link #NO_COMPRESSION}, {@link #GZIP_COMPRESSION} or
	 *            {@link #ZLIB_COMPRESSION}.
	 * @param rawArrays
	 *            Enabling this will parse all array types as {@link ByteArrayTag} to reduce overhead.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, int compression, boolean rawArrays) throws IOException {
		this(is, compression, rawArrays, ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which sources its data from the specified input stream. The stream may be wrapped into a
	 * decompressing input stream depending on the chosen compression method.
	 *
	 * @param is
	 *            The input stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link #NO_COMPRESSION}, {@link #GZIP_COMPRESSION} or
	 *            {@link #ZLIB_COMPRESSION}.
	 * @param endianness
	 *            Whether to read numbers from the InputStream with little endian encoding.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, int compression, ByteOrder endianness) throws IOException {
		this(is, compression, false, endianness);
	}

	/**
	 * Creates a new {@link NBTInputStream}, which sources its data from the specified input stream. The stream may be wrapped into a
	 * decompressing input stream depending on the chosen compression method.
	 *
	 * @param is
	 *            The input stream.
	 * @param compression
	 *            The compression algorithm used for the input stream. Must be {@link #NO_COMPRESSION}, {@link #GZIP_COMPRESSION} or
	 *            {@link #ZLIB_COMPRESSION}.
	 * @param rawArrays
	 *            Enabling this will parse all array types as {@link ByteArrayTag} to reduce overhead.
	 * @param endianness
	 *            Whether to read numbers from the InputStream with little endian encoding.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public NBTInputStream(InputStream is, int compression, boolean rawArrays, ByteOrder endianness) throws IOException {
		switch (compression) {
		case NO_COMPRESSION:
			break;
		case GZIP_COMPRESSION:
			is = new GZIPInputStream(is);
			break;
		case ZLIB_COMPRESSION:
			is = new InflaterInputStream(is);
			break;
		default:
			throw new IllegalArgumentException("Unsupported compression type, must be between 0 and 2 (inclusive)");
		}
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			this.inputStream = (InputStream) (this.dataIn = new LittleEndianInputStream(is));
		else
			this.inputStream = (InputStream) (this.dataIn = new DataInputStream(is));
		this.rawArrays = rawArrays;
	}

	/**
	 * Reads an NBT {@link Tag} from the stream.
	 *
	 * @return The tag that was read.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	public Tag<?> readTag() throws IOException {
		return readTag(0);
	}

	/**
	 * Reads an NBT {@link Tag} from the stream.
	 *
	 * @param depth
	 *            The depth of this tag.
	 * @return The tag that was read.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	private Tag<?> readTag(int depth) throws IOException {
		int typeId = dataIn.readByte() & 0xFF;
		TagType type = TagType.getById(typeId);

		String name;
		if (type != TagType.TAG_END) {
			name = dataIn.readUTF();
		} else {
			name = "";
		}

		return readTagPayload(type, name, depth);
	}

	/**
	 * Reads the payload of a {@link Tag}, given the name and type.
	 *
	 * @param type
	 *            The type.
	 * @param name
	 *            The name.
	 * @param depth
	 *            The depth.
	 * @return The tag.
	 * @throws java.io.IOException
	 *             if an I/O error occurs.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Tag readTagPayload(TagType type, String name, int depth) throws IOException {
		switch (type) {
		case TAG_END:
			if (depth == 0) {
				throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
			} else {
				return new EndTag();
			}

		case TAG_BYTE:
			return new ByteTag(name, dataIn.readByte());

		case TAG_SHORT:
			return new ShortTag(name, dataIn.readShort());

		case TAG_INT:
			return new IntTag(name, dataIn.readInt());

		case TAG_LONG:
			return new LongTag(name, dataIn.readLong());

		case TAG_FLOAT:
			return new FloatTag(name, dataIn.readFloat());

		case TAG_DOUBLE:
			return new DoubleTag(name, dataIn.readDouble());

		case TAG_STRING:
			return new StringTag(name, dataIn.readUTF());

		case TAG_LIST: {
			TagType childType = TagType.getById(dataIn.readByte());
			int length = dataIn.readInt();

			Class<? extends Tag> clazz = childType.getTagClass();
			List<Tag> tagList = new ArrayList<Tag>(length);
			for (int i = 0; i < length; i++) {
				Tag tag = readTagPayload(childType, "", depth + 1);
				if (tag instanceof EndTag) {
					throw new IOException("TAG_End not permitted in a list.");
				} else if (!clazz.isInstance(tag)) {
					throw new IOException("Mixed tag types within a list.");
				}
				tagList.add(tag);
			}

			return new ListTag(name, childType, tagList);
		}
		case TAG_COMPOUND:
			CompoundMap compoundTagList = new CompoundMap();
			while (true) {
				Tag tag = readTag(depth + 1);
				if (tag instanceof EndTag) {
					break;
				} else {
					compoundTagList.put(tag);
				}
			}

			return new CompoundTag(name, compoundTagList);

			/* Array types. Warning: fall-through! */
		case TAG_INT_ARRAY:
			if (!rawArrays) {
				int length = dataIn.readInt();
				int[] ints = new int[length];
				for (int i = 0; i < length; i++) {
					ints[i] = dataIn.readInt();
				}
				return new IntArrayTag(name, ints);
			}
		case TAG_LONG_ARRAY:
			if (!rawArrays) {
				int length = dataIn.readInt();
				long[] longs = new long[length];
				for (int i = 0; i < length; i++) {
					longs[i] = dataIn.readLong();
				}
				return new LongArrayTag(name, longs);
			}
		case TAG_SHORT_ARRAY:
			if (!rawArrays) {
				int length = dataIn.readInt();
				short[] shorts = new short[length];
				for (int i = 0; i < length; i++) {
					shorts[i] = dataIn.readShort();
				}
				return new ShortArrayTag(name, shorts);
			}
		case TAG_BYTE_ARRAY:
			int length = dataIn.readInt() * type.getSize();
			byte[] bytes = new byte[length];
			dataIn.readFully(bytes);
			return new ByteArrayTag(name, bytes);

		default:
			throw new IOException("Invalid tag type: " + type + ".");
		}
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}
}
