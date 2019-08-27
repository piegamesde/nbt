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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Represents a single NBT tag.
 */
public abstract class Tag<T> implements Comparable<Tag<?>> {
	/**
	 * The name of this tag.
	 */
	private final String	name;
	private final TagType	type;

	/**
	 * Creates the tag with no name.
	 */
	public Tag(TagType type) {
		this(type, "");
	}

	/**
	 * Creates the tag with the specified name.
	 *
	 * @param name
	 *            The name.
	 */
	public Tag(TagType type, String name) {
		this.name = name;
		this.type = type;
	}

	/**
	 * Gets the name of this tag.
	 *
	 * @return The name of this tag.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the type of this tag
	 *
	 * @return The type of this tag.
	 */
	public TagType getType() {
		return type;
	}

	/**
	 * Gets the value of this tag.
	 *
	 * @return The value of this tag.
	 */
	public abstract T getValue();

	/**
	 * Sets the value of this tag
	 * 
	 * @param value
	 *            The value of this tag.
	 */
	public abstract void setValue(T value);

	public Optional<EndTag> getAsEndTag() {
		return Optional.empty();
	}

	public Optional<ByteTag> getAsByteTag() {
		return Optional.empty();
	}

	public Optional<ShortTag> getAsShortTag() {
		return Optional.empty();
	}

	public Optional<IntTag> getAsIntTag() {
		return Optional.empty();
	}

	public Optional<LongTag> getAsLongTag() {
		return Optional.empty();
	}

	public Optional<FloatTag> getAsFloatTag() {
		return Optional.empty();
	}

	public Optional<DoubleTag> getAsDoubleTag() {
		return Optional.empty();
	}

	public Optional<ByteArrayTag> getAsByteArrayTag() {
		return Optional.empty();
	}

	public Optional<StringTag> getAsStringTag() {
		return Optional.empty();
	}

	public Optional<ListTag<?>> getAsListTag() {
		return Optional.empty();
	}

	public Optional<CompoundTag> getAsCompoundTag() {
		return Optional.empty();
	}

	public Optional<IntArrayTag> getAsIntArrayTag() {
		return Optional.empty();
	}

	public Optional<LongArrayTag> getAsLongArrayTag() {
		return Optional.empty();
	}

	public Optional<ShortArrayTag> getAsShortArrayTag() {
		return Optional.empty();
	}
	
	/**
	 * Clones a Map<String, Tag>
	 *
	 * @param map
	 *            the map
	 * @return a clone of the map
	 */
	public static Map<String, Tag<?>> cloneMap(Map<String, Tag<?>> map) {
		if (map == null) {
			return null;
		}

		Map<String, Tag<?>> newMap = new HashMap<String, Tag<?>>();
		for (Entry<String, Tag<?>> entry : map.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue().clone());
		}
		return newMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag<?> other = (Tag<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tag<?> other) {
		if (equals(other)) {
			return 0;
		} else {
			if (other.getName().equals(getName())) {
				throw new IllegalStateException("Cannot compare two Tags with the same name but different values for sorting");
			} else {
				return getName().compareTo(other.getName());
			}
		}
	}

	/**
	 * Clones the Tag
	 *
	 * @return the clone
	 */
	@Override
	public abstract Tag<T> clone();
}
