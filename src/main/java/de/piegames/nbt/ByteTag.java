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

import java.util.Optional;

/**
 * The {@code TAG_Byte} tag.
 */
public final class ByteTag extends Tag<Byte> {
    /**
     * The value.
     */
    private byte value;

    /**
     * Creates the tag.<br> Boolean true is stored as 1 and boolean false is stored as 0.
     *
     * @param name The name.
     * @param value The value.
     */
    public ByteTag(String name, boolean value) {
        this(name, (byte) (value ? 1 : 0));
    }

    /**
     * Creates the tag.
     *
     * @param name The name.
     * @param value The value.
     */
    public ByteTag(String name, byte value) {
        super(TagType.TAG_BYTE, name);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public void setValue(Byte value) {
        this.value = value;
    }

    public boolean getBooleanValue() {
        return value != 0;
    }

	@Override
	public Optional<ByteTag> getAsByteTag() {
		return Optional.of(this);
	}

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Byte" + append + ": " + value;
    }

    @Override
	public ByteTag clone() {
        return new ByteTag(getName(), value);
    }

    public static Boolean getBooleanValue(Tag<?> t) {
        if (t == null) {
            return null;
        }
        try {
            ByteTag byteTag = (ByteTag) t;
            return byteTag.getBooleanValue();
        } catch (ClassCastException e) {
            return null;
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + value;
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
		ByteTag other = (ByteTag) obj;
		if (value != other.value)
			return false;
		return true;
	}
}
