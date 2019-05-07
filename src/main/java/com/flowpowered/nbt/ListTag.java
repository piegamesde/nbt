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
package com.flowpowered.nbt;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The {@code TAG_List} tag.
 */
public class ListTag<T extends Tag<?>> extends Tag<List<T>> {
    /**
     * The type of entries within this list.
     */
    private final TagType type;
    /**
     * The value.
     */
    private List<T> value;

    /**
     * Creates the tag.
     *
     * @param name The name.
     * @param type The type of item in the list.
     * @param value The value.
     */
    public ListTag(String name, TagType type, List<T> value) {
        super(TagType.TAG_LIST, name);
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the type of item in this list.
     *
     * @return The type of item in this list.
     */
    public TagType getElementType() {
        return type;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public void setValue(List<T> value) {
        this.value = value;
    }

	@Override
	public Optional<ListTag<?>> getAsListTag() {
		return Optional.of(this);
	}

	/**
	 * Typesafe way to cast this object into a specialized instance.
	 * 
	 * @param type
	 *            The target type of child elements that is desired
	 * @return {@code this}, cast in adequate way and wrapped in an {@link Option} if the current tag type matches, or {@link Optional#empty()}
	 *         otherwise.
	 * @param <T>
	 *            The tag type of the child tags. Must match {@code type.getTagClass()}.
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	private <T extends Tag<?>> Optional<ListTag<T>> getAsList(TagType type) {
    	if (this.type == type)
    		return Optional.of((ListTag<T>) this);
    	else
    		return Optional.empty();
    }
    
	public Optional<ListTag<ByteTag>> getAsByteTagList() {
		return getAsList(TagType.TAG_BYTE);
	}

	public Optional<ListTag<ShortTag>> getAsShortTagList() {
		return getAsList(TagType.TAG_SHORT);
	}

	public Optional<ListTag<IntTag>> getAsIntTagList() {
		return getAsList(TagType.TAG_INT);
	}

	public Optional<ListTag<LongTag>> getAsLongTagList() {
		return getAsList(TagType.TAG_LONG);
	}

	public Optional<ListTag<FloatTag>> getAsFloatTagList() {
		return getAsList(TagType.TAG_FLOAT);
	}

	public Optional<ListTag<DoubleTag>> getAsDoubleTagList() {
		return getAsList(TagType.TAG_DOUBLE);
	}

	public Optional<ListTag<ByteArrayTag>> getAsByteArrayTagList() {
		return getAsList(TagType.TAG_BYTE_ARRAY);
	}

	public Optional<ListTag<StringTag>> getAsStringTagList() {
		return getAsList(TagType.TAG_STRING);
	}

	public Optional<ListTag<ListTag<?>>> getAsListTagList() {
		return getAsList(TagType.TAG_LIST);
	}

	public Optional<ListTag<CompoundTag>> getAsCompoundTagList() {
		return getAsList(TagType.TAG_COMPOUND);
	}

	public Optional<ListTag<IntArrayTag>> getAsIntArrayTagList() {
		return getAsList(TagType.TAG_INT_ARRAY);
	}

	public Optional<ListTag<LongArrayTag>> getAsLongArrayTagList() {
		return getAsList(TagType.TAG_LONG_ARRAY);
	}

	public Optional<ListTag<ShortArrayTag>> getAsShortArrayTagList() {
		return getAsList(TagType.TAG_SHORT_ARRAY);
	}

	public List<ByteTag> getByteListValue() {
		return getAsByteTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<ShortTag> getShortListValue() {
		return getAsShortTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<IntTag> getIntListValue() {
		return getAsIntTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<LongTag> getLongListValue() {
		return getAsLongTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<FloatTag> getFloatListValue() {
		return getAsFloatTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<DoubleTag> getDoubleListValue() {
		return getAsDoubleTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<ByteArrayTag> getByteArrayListValue() {
		return getAsByteArrayTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<StringTag> getStringListValue() {
		return getAsStringTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<ListTag<?>> getListTagListValue() {
		return getAsListTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<CompoundTag> getCompoundTagListValue() {
		return getAsCompoundTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<IntArrayTag> getIntArrayListValue() {
		return getAsIntArrayTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<LongArrayTag> getLongArrayListValue() {
		return getAsLongArrayTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

	public List<ShortArrayTag> getShortArrayListValue() {
		return getAsShortArrayTagList().map(ListTag::getValue).orElse(Collections.emptyList());
	}

    @Override
    public String toString() {
        String name = getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_List").append(append).append(": ").append(value.size()).append(" entries of type ").append(type.getTypeName()).append("\r\n{\r\n");
        for (Tag<?> t : value) {
            bldr.append("   ").append(t.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }

    @Override
	@SuppressWarnings ("unchecked")
    public ListTag<T> clone() {
        List<T> newList = new ArrayList<T>();

        for (T v : value) {
            newList.add((T) v.clone());
        }

        return new ListTag<T>(getName(), type, newList);
    }
}
