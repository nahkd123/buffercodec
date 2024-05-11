package io.github.nahkd123.buffercodec;

import java.util.function.BiConsumer;
import java.util.function.Function;

@FunctionalInterface
public interface EntryConsumer<T> {
	<A> void add(BufferCodec<A> type, Function<T, A> getter, BiConsumer<T, A> setter);
}
