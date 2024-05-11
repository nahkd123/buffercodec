package io.github.nahkd123.buffercodec;

import java.util.function.Function;

public class MapperCodec<F, T> implements BufferCodec<T> {
	private BufferCodec<F> base;
	private Function<F, T> forward;
	private Function<T, F> backward;

	public MapperCodec(BufferCodec<F> base, Function<F, T> forward, Function<T, F> backward) {
		this.base = base;
		this.forward = forward;
		this.backward = backward;
	}

	@Override
	public void encodeTo(WritableBuffer buffer, T value) {
		base.encodeTo(buffer, backward.apply(value));
	}

	@Override
	public T decodeFrom(ReadableBuffer buffer) {
		return forward.apply(base.decodeFrom(buffer));
	}
}
