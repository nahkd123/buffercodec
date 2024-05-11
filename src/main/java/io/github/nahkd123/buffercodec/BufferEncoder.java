package io.github.nahkd123.buffercodec;

@FunctionalInterface
public interface BufferEncoder<T> {
	public void encodeTo(WritableBuffer buffer, T value);
}
