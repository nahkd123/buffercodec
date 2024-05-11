package io.github.nahkd123.buffercodec;

public class VarintCodec implements BufferCodec<Long> {
	public static final VarintCodec INSTANCE = new VarintCodec();

	private VarintCodec() {}

	@Override
	public void encodeTo(WritableBuffer buffer, Long value) {
		long v = value;
		encodePrimitiveTo(buffer, v);
	}

	public static void encodePrimitiveTo(WritableBuffer buffer, long v) {
		do {
			long b = v & 0x7F;
			v >>= 7;
			if (v != 0) b |= 0x80;
			buffer.putByte((byte) b);
		} while (v != 0);
	}

	@Override
	public Long decodeFrom(ReadableBuffer buffer) {
		return decodePrimitiveFrom(buffer);
	}

	public static long decodePrimitiveFrom(ReadableBuffer buffer) {
		long out = 0L, shift = 0L;

		while (true) {
			long b = Byte.toUnsignedLong(buffer.getByte());
			out |= b << shift;
			if ((b & 0x80) == 0) break;
			shift += 7L;
		}

		return out;
	}
}
