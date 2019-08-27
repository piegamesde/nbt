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
package de.piegames.nbt;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Optional;

import de.piegames.nbt.stream.NBTInputStream;

/**
 * The {@code TAG_Byte_Array} tag.
 */
public final class ByteArrayTag extends Tag<byte[]> {
	/**
	 * The value.
	 */
	private byte[] value;

	/**
	 * Creates the tag.
	 *
	 * @param name
	 *            The name.
	 * @param value
	 *            The value.
	 */
	public ByteArrayTag(String name, byte[] value) {
		super(TagType.TAG_BYTE_ARRAY, name);
		this.value = value;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public void setValue(byte[] value) {
		this.value = value;
	}

	/**
	 * Interpret the value of this tag as `short[]`. Useful to deal with data read with {@link NBTInputStream#rawArrays} set. This will do a full
	 * conversion each time, so be sure to cache the result if required.
	 * <p/>
	 * <b>Warning:</b> this won't deal with byte order. Implement your own and use {@link ByteBuffer#order(java.nio.ByteOrder)} to change it if
	 * you have non-standard data. This won't deal with data length as well. Make sure the data size is an appropriate multiple.
	 */
	public short[] getShortArrayValue() {
		ShortBuffer buffer = ByteBuffer.wrap(value).asShortBuffer();
		short[] array = new short[buffer.remaining()];
		buffer.get(array);
		return array;
	}

	/**
	 * Interpret the value of this tag as `int[]`. Useful to deal with data read with {@link NBTInputStream#rawArrays} set. This will do a full
	 * conversion each time, so be sure to cache the result if required.
	 * <p/>
	 * <b>Warning:</b> this won't deal with byte order. Implement your own and use {@link ByteBuffer#order(java.nio.ByteOrder)} to change it if
	 * you have non-standard data. This won't deal with data length as well. Make sure the data size is an appropriate multiple.
	 */
	public int[] getIntArrayValue() {
		IntBuffer buffer = ByteBuffer.wrap(value).asIntBuffer();
		int[] array = new int[buffer.remaining()];
		buffer.get(array);
		return array;
	}

	/**
	 * Interpret the value of this tag as `long[]`. Useful to deal with data read with {@link NBTInputStream#rawArrays} set. This will do a full
	 * conversion each time, so be sure to cache the result if required.
	 * <p/>
	 * <b>Warning:</b> this won't deal with byte order. Implement your own and use {@link ByteBuffer#order(java.nio.ByteOrder)} to change it if
	 * you have non-standard data. This won't deal with data length as well. Make sure the data size is an appropriate multiple.
	 */
	public long[] getLongArrayValue() {
		LongBuffer buffer = ByteBuffer.wrap(value).asLongBuffer();
		long[] array = new long[buffer.remaining()];
		buffer.get(array);
		return array;
	}

	@Override
	public Optional<ByteArrayTag> getAsByteArrayTag() {
		return Optional.of(this);
	}
	
	@Override
	public Optional<IntArrayTag> getAsIntArrayTag() {
		return Optional.of(new IntArrayTag(getName(), getIntArrayValue()));
	}

	@Override
	public Optional<LongArrayTag> getAsLongArrayTag() {
		return Optional.of(new LongArrayTag(getName(), getLongArrayValue()));
	}
	
	@Override
	public Optional<ShortArrayTag> getAsShortArrayTag() {
		return Optional.of(new ShortArrayTag(getName(), getShortArrayValue()));
	}

	@Override
	public String toString() {
		StringBuilder hex = new StringBuilder();
		for (byte b : value) {
			String hexDigits = Integer.toHexString(b).toUpperCase();
			if (hexDigits.length() == 1) {
				hex.append("0");
			}
			hex.append(hexDigits).append(" ");
		}

		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Byte_Array" + append + ": " + hex.toString();
	}

	@Override
	public ByteArrayTag clone() {
		byte[] clonedArray = cloneArray(value);

		return new ByteArrayTag(getName(), clonedArray);
	}

	private byte[] cloneArray(byte[] byteArray) {
		if (byteArray == null) {
			return null;
		} else {
			int length = byteArray.length;
			byte[] newArray = new byte[length];
			System.arraycopy(byteArray, 0, newArray, 0, length);
			return newArray;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArrayTag other = (ByteArrayTag) obj;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}
}
