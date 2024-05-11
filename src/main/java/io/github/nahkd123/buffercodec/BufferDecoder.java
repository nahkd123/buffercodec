package io.github.nahkd123.buffercodec;

@FunctionalInterface
public interface BufferDecoder<T> {
	public T decodeFrom(ReadableBuffer buffer);
}
