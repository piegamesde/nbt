package de.piegames.nbt.regionfile;

import java.util.Objects;

/**
 * Utility class to extract palette indices from the packed array containing all the block states. The data may be found in each chunk at
 * {@code /Level/Sections[i]/BlockStates}.
 * <p/>
 * This does exactly the same thing as {@link Chunk#extractFromLong1_13(long[], int, int)}, but faster. Only use this if said method shows up in
 * your profiler with >1% CPU usage. It is intended for one-time sequential parsing, like an iterator. If you need only a few elements, or
 * random access, don't use it.
 * 
 * @see Chunk#extractFromLong1_13(long[], int, int)
 * @author piegames
 */
public class Palette1_13 {

	final long[]	data;
	final long[]	output;
	final int		bitsPerIndex;
	int				index;

	/**
	 * @param data
	 *            a long array densely packed with n-bit unsigned values, with @{code n = data.length/64 <= 64}. Therefore, {@code data.length}
	 *            must be divisible through 64. The data always contains 4096 packed values.
	 */
	public Palette1_13(long[] data) {
		this.data = Objects.requireNonNull(data);
		if (data.length % 64 != 0)
			throw new IllegalArgumentException("Data must contain a multiple of 64 longs");
		bitsPerIndex = data.length >> 6;
		output = new long[64];
	}

	/**
	 * Extracts and returns the next 64 packed values from the data. The returned array is cached internally, so its content will be overwritten
	 * the next time this method is called.
	 * <p/>
	 * You can call this method exactly 64 times (64*64=4096 values). No explicit bounds checks are made.
	 * <p/>
	 * The code path is only optimized for {@code bitsPerIndex < 7}, because other values never appear in natural Minecraft data (even 6 is
	 * <i>really</i> rare).
	 * 
	 * @see Chunk#extractFromLong(long[], int, int)
	 */
	public long[] next64() {
		if (bitsPerIndex == 4) {
			// Bit 64 ............................................................... Bit 0
			// ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ ┌──┐┌──┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			output[0] = (data[index] >> 0x00) & 0xF;
			output[1] = (data[index] >> 0x04) & 0xF;
			output[2] = (data[index] >> 0x08) & 0xF;
			output[3] = (data[index] >> 0x0C) & 0xF;
			output[4] = (data[index] >> 0x10) & 0xF;
			output[5] = (data[index] >> 0x14) & 0xF;
			output[6] = (data[index] >> 0x18) & 0xF;
			output[7] = (data[index] >> 0x1C) & 0xF;
			output[8] = (data[index] >> 0x20) & 0xF;
			output[9] = (data[index] >> 0x24) & 0xF;
			output[10] = (data[index] >> 0x28) & 0xF;
			output[11] = (data[index] >> 0x2C) & 0xF;
			output[12] = (data[index] >> 0x30) & 0xF;
			output[13] = (data[index] >> 0x34) & 0xF;
			output[14] = (data[index] >> 0x38) & 0xF;
			output[15] = (data[index++] >> 0x3C) & 0xF;
			output[16] = (data[index] >> 0x00) & 0xF;
			output[17] = (data[index] >> 0x04) & 0xF;
			output[18] = (data[index] >> 0x08) & 0xF;
			output[19] = (data[index] >> 0x0C) & 0xF;
			output[20] = (data[index] >> 0x10) & 0xF;
			output[21] = (data[index] >> 0x14) & 0xF;
			output[22] = (data[index] >> 0x18) & 0xF;
			output[23] = (data[index] >> 0x1C) & 0xF;
			output[24] = (data[index] >> 0x20) & 0xF;
			output[25] = (data[index] >> 0x24) & 0xF;
			output[26] = (data[index] >> 0x28) & 0xF;
			output[27] = (data[index] >> 0x2C) & 0xF;
			output[28] = (data[index] >> 0x30) & 0xF;
			output[29] = (data[index] >> 0x34) & 0xF;
			output[30] = (data[index] >> 0x38) & 0xF;
			output[31] = (data[index++] >> 0x3C) & 0xF;
			output[32] = (data[index] >> 0x00) & 0xF;
			output[33] = (data[index] >> 0x04) & 0xF;
			output[34] = (data[index] >> 0x08) & 0xF;
			output[35] = (data[index] >> 0x0C) & 0xF;
			output[36] = (data[index] >> 0x10) & 0xF;
			output[37] = (data[index] >> 0x14) & 0xF;
			output[38] = (data[index] >> 0x18) & 0xF;
			output[39] = (data[index] >> 0x1C) & 0xF;
			output[40] = (data[index] >> 0x20) & 0xF;
			output[41] = (data[index] >> 0x24) & 0xF;
			output[42] = (data[index] >> 0x28) & 0xF;
			output[43] = (data[index] >> 0x2C) & 0xF;
			output[44] = (data[index] >> 0x30) & 0xF;
			output[45] = (data[index] >> 0x34) & 0xF;
			output[46] = (data[index] >> 0x38) & 0xF;
			output[47] = (data[index++] >> 0x3C) & 0xF;
			output[48] = (data[index] >> 0x00) & 0xF;
			output[49] = (data[index] >> 0x04) & 0xF;
			output[50] = (data[index] >> 0x08) & 0xF;
			output[51] = (data[index] >> 0x0C) & 0xF;
			output[52] = (data[index] >> 0x10) & 0xF;
			output[53] = (data[index] >> 0x14) & 0xF;
			output[54] = (data[index] >> 0x18) & 0xF;
			output[55] = (data[index] >> 0x1C) & 0xF;
			output[56] = (data[index] >> 0x20) & 0xF;
			output[57] = (data[index] >> 0x24) & 0xF;
			output[58] = (data[index] >> 0x28) & 0xF;
			output[59] = (data[index] >> 0x2C) & 0xF;
			output[60] = (data[index] >> 0x30) & 0xF;
			output[61] = (data[index] >> 0x34) & 0xF;
			output[62] = (data[index] >> 0x38) & 0xF;
			output[63] = (data[index++] >> 0x3C) & 0xF;
			return output;
		} else if (bitsPerIndex == 5) {
			// Bit 64 ............................................................... Bit 0
			// ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌───┐┌── ─┐┌───┐┌ ───┐┌─── ┐┌───┐┌─ ──┐┌───┐ ┌───┐┌── ─┐┌───┐┌ ───┐┌─── (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			output[0] = (data[index] >> 0) & 0x1F;
			output[1] = (data[index] >> 5) & 0x1F;
			output[2] = (data[index] >> 10) & 0x1F;
			output[3] = (data[index] >> 15) & 0x1F;
			output[4] = (data[index] >> 20) & 0x1F;
			output[5] = (data[index] >> 25) & 0x1F;
			output[6] = (data[index] >> 30) & 0x1F;
			output[7] = (data[index] >> 35) & 0x1F;
			output[8] = (data[index] >> 40) & 0x1F;
			output[9] = (data[index] >> 45) & 0x1F;
			output[10] = (data[index] >> 50) & 0x1F;
			output[11] = (data[index] >> 55) & 0x1F;
			output[12] = ((data[index++] >>> 60) | (data[index] << 4)) & 0x1F;
			output[13] = (data[index] >> 1) & 0x1F;
			output[14] = (data[index] >> 6) & 0x1F;
			output[15] = (data[index] >> 11) & 0x1F;
			output[16] = (data[index] >> 16) & 0x1F;
			output[17] = (data[index] >> 21) & 0x1F;
			output[18] = (data[index] >> 26) & 0x1F;
			output[19] = (data[index] >> 31) & 0x1F;
			output[20] = (data[index] >> 36) & 0x1F;
			output[21] = (data[index] >> 41) & 0x1F;
			output[22] = (data[index] >> 46) & 0x1F;
			output[23] = (data[index] >> 51) & 0x1F;
			output[24] = (data[index] >> 56) & 0x1F;
			output[25] = ((data[index++] >>> 61) | (data[index] << 3)) & 0x1F;
			output[26] = (data[index] >> 2) & 0x1F;
			output[27] = (data[index] >> 7) & 0x1F;
			output[28] = (data[index] >> 12) & 0x1F;
			output[29] = (data[index] >> 17) & 0x1F;
			output[30] = (data[index] >> 22) & 0x1F;
			output[31] = (data[index] >> 27) & 0x1F;
			output[32] = (data[index] >> 32) & 0x1F;
			output[33] = (data[index] >> 37) & 0x1F;
			output[34] = (data[index] >> 42) & 0x1F;
			output[35] = (data[index] >> 47) & 0x1F;
			output[36] = (data[index] >> 52) & 0x1F;
			output[37] = (data[index] >> 57) & 0x1F;
			output[38] = ((data[index++] >>> 62) | (data[index] << 2)) & 0x1F;
			output[39] = (data[index] >> 3) & 0x1F;
			output[40] = (data[index] >> 8) & 0x1F;
			output[41] = (data[index] >> 13) & 0x1F;
			output[42] = (data[index] >> 18) & 0x1F;
			output[43] = (data[index] >> 23) & 0x1F;
			output[44] = (data[index] >> 28) & 0x1F;
			output[45] = (data[index] >> 33) & 0x1F;
			output[46] = (data[index] >> 38) & 0x1F;
			output[47] = (data[index] >> 43) & 0x1F;
			output[48] = (data[index] >> 48) & 0x1F;
			output[49] = (data[index] >> 53) & 0x1F;
			output[50] = (data[index] >> 58) & 0x1F;
			output[51] = ((data[index++] >>> 63) | (data[index] << 1)) & 0x1F;
			output[52] = (data[index] >> 4) & 0x1F;
			output[53] = (data[index] >> 9) & 0x1F;
			output[54] = (data[index] >> 14) & 0x1F;
			output[55] = (data[index] >> 19) & 0x1F;
			output[56] = (data[index] >> 24) & 0x1F;
			output[57] = (data[index] >> 29) & 0x1F;
			output[58] = (data[index] >> 34) & 0x1F;
			output[59] = (data[index] >> 39) & 0x1F;
			output[60] = (data[index] >> 44) & 0x1F;
			output[61] = (data[index] >> 49) & 0x1F;
			output[62] = (data[index] >> 54) & 0x1F;
			output[63] = (data[index++] >>> 59) & 0x1F;
			return output;
		} else if (bitsPerIndex == 6) {
			// Bit 64 ............................................................... Bit 0
			// ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌───
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ (from right to left)
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			// ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌─── ─┐┌────┐ ┌────┐┌─ ───┐┌───
			// xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
			output[0] = (data[index] >> 0) & 0x3F;
			output[1] = (data[index] >> 6) & 0x3F;
			output[2] = (data[index] >> 12) & 0x3F;
			output[3] = (data[index] >> 18) & 0x3F;
			output[4] = (data[index] >> 24) & 0x3F;
			output[5] = (data[index] >> 30) & 0x3F;
			output[6] = (data[index] >> 36) & 0x3F;
			output[7] = (data[index] >> 42) & 0x3F;
			output[8] = (data[index] >> 48) & 0x3F;
			output[9] = (data[index] >> 54) & 0x3F;
			output[10] = ((data[index++] >>> 60) | (data[index] << 4)) & 0x3F;
			output[11] = (data[index] >> 0 + 2) & 0x3F;
			output[12] = (data[index] >> 6 + 2) & 0x3F;
			output[13] = (data[index] >> 12 + 2) & 0x3F;
			output[14] = (data[index] >> 18 + 2) & 0x3F;
			output[15] = (data[index] >> 24 + 2) & 0x3F;
			output[16] = (data[index] >> 30 + 2) & 0x3F;
			output[17] = (data[index] >> 36 + 2) & 0x3F;
			output[18] = (data[index] >> 42 + 2) & 0x3F;
			output[19] = (data[index] >> 48 + 2) & 0x3F;
			output[20] = (data[index] >> 54 + 2) & 0x3F;
			output[21] = ((data[index++] >>> 62) | (data[index] << 2)) & 0x3F;
			output[22] = (data[index] >> 0 + 4) & 0x3F;
			output[23] = (data[index] >> 6 + 4) & 0x3F;
			output[24] = (data[index] >> 12 + 4) & 0x3F;
			output[25] = (data[index] >> 18 + 4) & 0x3F;
			output[26] = (data[index] >> 24 + 4) & 0x3F;
			output[27] = (data[index] >> 30 + 4) & 0x3F;
			output[28] = (data[index] >> 36 + 4) & 0x3F;
			output[29] = (data[index] >> 42 + 4) & 0x3F;
			output[30] = (data[index] >> 48 + 4) & 0x3F;
			output[31] = (data[index++] >> 58) & 0x3F;
			output[32] = (data[index] >> 0) & 0x3F;
			output[33] = (data[index] >> 6) & 0x3F;
			output[34] = (data[index] >> 12) & 0x3F;
			output[35] = (data[index] >> 18) & 0x3F;
			output[36] = (data[index] >> 24) & 0x3F;
			output[37] = (data[index] >> 30) & 0x3F;
			output[38] = (data[index] >> 36) & 0x3F;
			output[39] = (data[index] >> 42) & 0x3F;
			output[40] = (data[index] >> 48) & 0x3F;
			output[41] = (data[index] >> 54) & 0x3F;
			output[42] = ((data[index++] >>> 60) | (data[index] << 4)) & 0x3F;
			output[43] = (data[index] >> 0 + 2) & 0x3F;
			output[44] = (data[index] >> 6 + 2) & 0x3F;
			output[45] = (data[index] >> 12 + 2) & 0x3F;
			output[46] = (data[index] >> 18 + 2) & 0x3F;
			output[47] = (data[index] >> 24 + 2) & 0x3F;
			output[48] = (data[index] >> 30 + 2) & 0x3F;
			output[49] = (data[index] >> 36 + 2) & 0x3F;
			output[50] = (data[index] >> 42 + 2) & 0x3F;
			output[51] = (data[index] >> 48 + 2) & 0x3F;
			output[52] = (data[index] >> 54 + 2) & 0x3F;
			output[53] = ((data[index++] >>> 62) | (data[index] << 2)) & 0x3F;
			output[54] = (data[index] >> 0 + 4) & 0x3F;
			output[55] = (data[index] >> 6 + 4) & 0x3F;
			output[56] = (data[index] >> 12 + 4) & 0x3F;
			output[57] = (data[index] >> 18 + 4) & 0x3F;
			output[58] = (data[index] >> 24 + 4) & 0x3F;
			output[59] = (data[index] >> 30 + 4) & 0x3F;
			output[60] = (data[index] >> 36 + 4) & 0x3F;
			output[61] = (data[index] >> 42 + 4) & 0x3F;
			output[62] = (data[index] >> 48 + 4) & 0x3F;
			output[63] = (data[index++] >> 58) & 0x3F;
			return output;
		} else {
			/* In natural Minecraft world data, this is essentially dead code. */

			for (int i = 0; i < 64; i++) {
				output[i] = Chunk.extractFromLong1_13(data, index * 64 / bitsPerIndex + i, bitsPerIndex);
			}
			index += bitsPerIndex;
			return output;
		}
	}
}
