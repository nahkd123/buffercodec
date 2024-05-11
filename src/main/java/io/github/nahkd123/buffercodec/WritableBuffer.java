package io.github.nahkd123.buffercodec;

public interface WritableBuffer {
	public void putByte(byte value);

	default void put(byte[] bytes, int offset, int length) {
		for (int i = 0; i < length; i++) putByte(bytes[offset + i]);
	}

	default void put(byte[] bytes) {
		put(bytes, 0, bytes.length);
	}

	default void putShort(short value) {
		put(new byte[] { (byte) ((value & 0xFF00) >> 8), (byte) (value & 0x00FF) });
	}

	default void putInt(int value) {
		put(new byte[] {
			(byte) ((value & 0xFF000000) >> 24),
			(byte) ((value & 0x00FF0000) >> 16),
			(byte) ((value & 0x0000FF00) >> 8),
			(byte) (value & 0x000000FF)
		});
	}

	default void putLong(long value) {
		put(new byte[] {
			(byte) ((value & 0xFF000000_00000000L) >> 56L),
			(byte) ((value & 0x00FF0000_00000000L) >> 48L),
			(byte) ((value & 0x0000FF00_00000000L) >> 40L),
			(byte) ((value & 0x000000FF_00000000L) >> 32L),
			(byte) ((value & 0x00000000_FF000000L) >> 24L),
			(byte) ((value & 0x00000000_00FF0000L) >> 16L),
			(byte) ((value & 0x00000000_0000FF00L) >> 8L),
			(byte) (value & 0x00000000_000000FFL)
		});
	}
}
