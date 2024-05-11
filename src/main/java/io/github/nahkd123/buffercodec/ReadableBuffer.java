package io.github.nahkd123.buffercodec;

public interface ReadableBuffer {
	public byte getByte();

	default byte[] get(byte[] bytes, int offset, int length) {
		for (int i = 0; i < length; i++) bytes[offset + i] = getByte();
		return bytes;
	}

	default byte[] get(byte[] bytes) {
		return get(bytes, 0, bytes.length);
	}

	default short getShort() {
		byte[] bs = get(new byte[2]);
		return (short) (Byte.toUnsignedInt(bs[0]) << 8 | Byte.toUnsignedInt(bs[1]));
	}

	default int getInt() {
		byte[] bs = get(new byte[4]);
		return Byte.toUnsignedInt(bs[0]) << 24
			| Byte.toUnsignedInt(bs[1]) << 16
			| Byte.toUnsignedInt(bs[2]) << 8
			| Byte.toUnsignedInt(bs[3]);
	}

	default long getLong() {
		byte[] bs = get(new byte[8]);
		return Byte.toUnsignedLong(bs[0]) << 56
			| Byte.toUnsignedLong(bs[1]) << 48
			| Byte.toUnsignedLong(bs[2]) << 40
			| Byte.toUnsignedLong(bs[3]) << 32
			| Byte.toUnsignedLong(bs[4]) << 24
			| Byte.toUnsignedLong(bs[5]) << 16
			| Byte.toUnsignedLong(bs[6]) << 8
			| Byte.toUnsignedLong(bs[7]);
	}
}
