package de.piegames.nbt.regionfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
public class LongBenchmark {
	// @Param({"4","5","6","7","8","9","10","11","12","13"})
	@Param({ "4", "5", "6", "7" })
	private int		BITS;

	private long[]	data;

	@Setup
	public void setUp() {
		Random random = new Random(BITS * 500);

		// Fill a byte buffer with random data
		ByteBuffer buffer = ByteBuffer.wrap(new byte[BITS * 64 * 8]);
		buffer.order(ByteOrder.BIG_ENDIAN);
		random.nextBytes(buffer.array());

		// Convert it to a long buffer
		LongBuffer data = buffer.asLongBuffer();
		data.rewind();
		this.data = new long[BITS * 64];
		data.get(this.data);
	}

	@Benchmark
	public void dummy(Blackhole hole) {
		for (int i = 0; i < BITS * 64; i++)
			hole.consume(data[i]);
	}

	@Benchmark
	public void baseline(Blackhole hole) {
		for (int i = 0; i < 4096; i++) {
			int startByte = (BITS * i) >> 6; // >> 6 equals / 64
			int endByte = (BITS * (i + 1)) >> 6;
			// The bit within the long where our value starts. Counting from the right LSB (!).
			int startByteBit = ((BITS * i)) & 63; // % 64 equals & 63
			int endByteBit = ((BITS * (i + 1))) & 63;

			// Use bit shifting and & bit masking to extract bit sequences out of longs as numbers
			// -1L is the value with every bit set
			long blockIndex;
			if (startByte == endByte) {
				// Normal case: the bit string we need is within a single long
				blockIndex = (data[startByte] << (64 - endByteBit)) >>> (64 + startByteBit - endByteBit);
			} else if (endByteBit == 0) {
				// The bit string is exactly at the beginning of a long
				blockIndex = data[startByte] >>> startByteBit;
			} else {
				// The bit string is overlapping two longs
				blockIndex = ((data[startByte] >>> startByteBit))
						| ((data[endByte] << (64 - endByteBit)) >>> (startByteBit - endByteBit));
			}
			hole.consume(blockIndex);
		}
	}

	@Benchmark
	public void optimized(Blackhole hole) {
		for (int i = 0; i < 4096; i++) {
			int startByte = (BITS * i) >> 6;
			int startByteBit = (BITS * i) & 63;
			int endByte = ((BITS * (i + 1)) - 1) >> 6;
			long bitMask = 0xFFFFFFFFFFFFFFFFL >>> (64 - BITS);
			hole.consume(((data[startByte] >>> startByteBit) | (data[endByte] << (64 - startByteBit))) & bitMask);
		}
	}

	@Benchmark
	public void optimized_LICM(Blackhole hole) {
		long bitMask = 0xFFFFFFFFFFFFFFFFL >>> (64 - BITS);
		int bitIndex = 0;
		for (int i = 0; i < 4096; i++) {
			int startByte = bitIndex >> 6;
			int startByteBit = bitIndex & 63;
			bitIndex += BITS;
			int endByte = (bitIndex - 1) >> 6;
			hole.consume(((data[startByte] >>> startByteBit) | (data[endByte] << (64 - startByteBit))) & bitMask);
		}
	}

	@Benchmark
	public void optimized_IF(Blackhole hole) {
		for (int i = 0; i < 4096; i++) {
			int startByte = (BITS * i) >> 6;
			int startByteBit = (BITS * i) & 63;
			int endByte = ((BITS * (i + 1)) - 1) >> 6;
			long bitMask = 0xFFFFFFFFFFFFFFFFL >>> (64 - BITS);
			if (startByte == endByte)
				hole.consume((data[startByte] >>> startByteBit) & bitMask);
			else
				hole.consume(((data[startByte] >>> startByteBit) | (data[endByte] << (64
						- startByteBit))) & bitMask);
		}
	}

	@Benchmark
	public void optimized_LICM_IF(Blackhole hole) {
		long bitMask = 0xFFFFFFFFFFFFFFFFL >>> (64 - BITS);
		int bitIndex = 0;
		for (int i = 0; i < 4096; i++) {
			int startByte = bitIndex >> 6;
			int startByteBit = bitIndex & 63;
			bitIndex += BITS;
			int endByte = (bitIndex - 1) >> 6;
			if (startByte == endByte)
				hole.consume((data[startByte] >>> startByteBit) & bitMask);
			else
				hole.consume(((data[startByte] >>> startByteBit) | (data[endByte] << (64
						- startByteBit))) & bitMask);
		}
	}

	@Benchmark
	public void palette1(Blackhole hole) {
		Palette palette = new Palette(data);
		for (int i = 0; i < 4096 / 64; i++) {
			palette.next64();
			for (long l : palette.output)
				hole.consume(l);
		}
	}

	@Benchmark
	public void palette2(Blackhole hole) {
		Palette palette = new Palette(data);
		for (int i = 0; i < 4096 / 64; i++) {
			palette.next64();
			for (int j = 0; j < 64; j++)
				hole.consume(palette.output[j]);
		}
	}

	@Benchmark
	public void palette_ret1(Blackhole hole) {
		Palette palette = new Palette(data);
		for (int i = 0; i < 4096 / 64; i++) {
			for (long l : palette.next64())
				hole.consume(l);
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(LongBenchmark.class.getSimpleName())
				.build();

		new Runner(opt).run();
	}
}