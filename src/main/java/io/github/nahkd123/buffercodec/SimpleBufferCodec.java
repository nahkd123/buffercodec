package io.github.nahkd123.buffercodec;

public class SimpleBufferCodec<T> implements BufferCodec<T> {
	private BufferEncoder<T> encoder;
	private BufferDecoder<T> decoder;

	public SimpleBufferCodec(BufferEncoder<T> encoder, BufferDecoder<T> decoder) {
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void encodeTo(WritableBuffer buffer, T value) {
		encoder.encodeTo(buffer, value);
	}

	@Override
	public T decodeFrom(ReadableBuffer buffer) {
		return decoder.decodeFrom(buffer);
	}

}
