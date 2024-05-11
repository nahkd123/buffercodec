package io.github.nahkd123.buffercodec;

import java.io.ByteArrayOutputStream;

public class ByteArrayWritableBuffer implements WritableBuffer {
	private ByteArrayOutputStream stream;

	public ByteArrayWritableBuffer() {
		stream = new ByteArrayOutputStream();
	}

	@Override
	public void putByte(byte value) {
		stream.write(value);
	}

	@Override
	public void put(byte[] bytes, int offset, int length) {
		stream.write(bytes, offset, length);
	}

	public byte[] getBytes() { return stream.toByteArray(); }
}
