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

import java.util.Arrays;
import java.util.Optional;

public class ShortArrayTag extends Tag<short[]> {
    /**
     * The value.
     */
    private short[] value;

    /**
     * Creates the tag.
     *
     * @param name The name.
     * @param value The value.
     */
    public ShortArrayTag(String name, short[] value) {
        super(TagType.TAG_SHORT_ARRAY, name);
        this.value = value;
    }

    @Override
    public short[] getValue() {
        return value;
    }

    @Override
    public void setValue(short[] value) {
        this.value = value;
    }

	@Override
	public Optional<ShortArrayTag> getAsShortArrayTag() {
		return Optional.of(this);
	}

    @Override
    public String toString() {
        StringBuilder hex = new StringBuilder();
        for (short s : value) {
            String hexDigits = Integer.toHexString(s).toUpperCase();
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
        return "TAG_Short_Array" + append + ": " + hex.toString();
    }

    @Override
	public ShortArrayTag clone() {
        short[] clonedArray = cloneArray(value);

        return new ShortArrayTag(getName(), clonedArray);
    }

    private short[] cloneArray(short[] shortArray) {
        if (shortArray == null) {
            return null;
        } else {
            int length = shortArray.length;
            short[] newArray = new short[length];
            System.arraycopy(shortArray, 0, newArray, 0, length);
            return shortArray;
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
		ShortArrayTag other = (ShortArrayTag) obj;
		if (!Arrays.equals(value, other.value))
			return false;
		return true;
	}
}
