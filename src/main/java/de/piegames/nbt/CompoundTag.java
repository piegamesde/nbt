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
 * The {@code TAG_Compound} tag.
 */
public class CompoundTag extends Tag<CompoundMap> {
	/**
	 * The value.
	 */
	private CompoundMap value;

	/**
	 * Creates the tag.
	 *
	 * @param name
	 *            The name.
	 * @param value
	 *            The value.
	 */
	public CompoundTag(String name, CompoundMap value) {
		super(TagType.TAG_COMPOUND, name);
		this.value = value;
	}

	@Override
	public CompoundMap getValue() {
		return value;
	}

	@Override
	public void setValue(CompoundMap value) {
		this.value = value;
	}
	
	@Override
	public Optional<CompoundTag> getAsCompoundTag() {
		return Optional.of(this);
	}

	public Optional<ByteTag> getAsByteTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsByteTag);
	}

	public Optional<ShortTag> getAsShortTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsShortTag);
	}

	public Optional<IntTag> getAsIntTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsIntTag);
	}

	public Optional<LongTag> getAsLongTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsLongTag);
	}

	public Optional<FloatTag> getAsFloatTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsFloatTag);
	}

	public Optional<DoubleTag> getAsDoubleTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsDoubleTag);
	}

	public Optional<ByteArrayTag> getAsByteArrayTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsByteArrayTag);
	}

	public Optional<StringTag> getAsStringTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsStringTag);
	}

	public Optional<ListTag<?>> getAsListTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsListTag);
	}

	public Optional<CompoundTag> getAsCompoundTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsCompoundTag);
	}

	public Optional<IntArrayTag> getAsIntArrayTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsIntArrayTag);
	}

	public Optional<LongArrayTag> getAsLongArrayTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsLongArrayTag);
	}

	public Optional<ShortArrayTag> getAsShortArrayTag(String childName) {
		return Optional.ofNullable(value.get(childName)).flatMap(Tag::getAsShortArrayTag);
	}
	
	public Optional<Byte> getByteValue(String childName) {
		return getAsByteTag(childName).map(Tag::getValue);
	}

	public Optional<Short> getShortValue(String childName) {
		return getAsShortTag(childName).map(Tag::getValue);
	}

	public Optional<Integer> getIntValue(String childName) {
		return getAsIntTag(childName).map(Tag::getValue);
	}

	public Optional<Long> getLongValue(String childName) {
		return getAsLongTag(childName).map(Tag::getValue);
	}

	public Optional<Float> getFloatValue(String childName) {
		return getAsFloatTag(childName).map(Tag::getValue);
	}

	public Optional<Double> getDoubleValue(String childName) {
		return getAsDoubleTag(childName).map(Tag::getValue);
	}

	public Optional<byte[]> getByteArrayValue(String childName) {
		return getAsByteArrayTag(childName).map(Tag::getValue);
	}

	public Optional<String> getStringValue(String childName) {
		return getAsStringTag(childName).map(Tag::getValue);
	}

	public Optional<int[]> getIntArrayValue(String childName) {
		return getAsIntArrayTag(childName).map(Tag::getValue);
	}

	public Optional<long[]> getLongArrayValue(String childName) {
		return getAsLongArrayTag(childName).map(Tag::getValue);
	}

	public Optional<short[]> getShortArrayValue(String childName) {
		return getAsShortArrayTag(childName).map(Tag::getValue);
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}

		StringBuilder bldr = new StringBuilder();
		bldr.append("TAG_Compound").append(append).append(": ").append(value.size()).append(" entries\r\n{\r\n");
		for (Tag<?> entry : value.values()) {
			bldr.append("   ").append(entry.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
		}
		bldr.append("}");
		return bldr.toString();
	}

	@Override
	public CompoundTag clone() {
		CompoundMap map = new CompoundMap(value);
		return new CompoundTag(getName(), map);
	}
}
