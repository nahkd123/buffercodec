package io.github.nahkd123.buffercodec;

import java.io.ByteArrayInputStream;

public class ByteArrayReadableBuffer implements ReadableBuffer {
	private ByteArrayInputStream stream;

	public ByteArrayReadableBuffer(byte[] bytes) {
		stream = new ByteArrayInputStream(bytes);
	}

	public ByteArrayReadableBuffer(ByteArrayWritableBuffer from) {
		this(from.getBytes());
	}

	@Override
	public byte getByte() {
		int v = stream.read();
		if (v == -1) throw new IllegalStateException("End of stream");
		return (byte) v;
	}

	@Override
	public byte[] get(byte[] bytes, int offset, int length) {
		stream.read(bytes, offset, length);
		return bytes;
	}
}
