package io.github.nahkd123.buffercodec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BufferCodec<T> extends BufferEncoder<T>, BufferDecoder<T> {
	// Primitives
	BufferCodec<Byte> BYTE = new SimpleBufferCodec<>(WritableBuffer::putByte, ReadableBuffer::getByte);
	BufferCodec<Short> SHORT = new SimpleBufferCodec<>(WritableBuffer::putShort, ReadableBuffer::getShort);
	BufferCodec<Integer> INT = new SimpleBufferCodec<>(WritableBuffer::putInt, ReadableBuffer::getInt);
	BufferCodec<Long> LONG = new SimpleBufferCodec<>(WritableBuffer::putLong, ReadableBuffer::getLong);

	default <R> BufferCodec<R> map(Function<T, R> forward, Function<R, T> backward) {
		return new MapperCodec<>(this, forward, backward);
	}

	// Even more primitives
	BufferCodec<Boolean> BOOL = BYTE.map(b -> b == 0, v -> (byte) (v ? 0 : 1));
	BufferCodec<Float> FLOAT = INT.map(i -> Float.intBitsToFloat(i), f -> Float.floatToIntBits(f));
	BufferCodec<Double> DOUBLE = LONG.map(l -> Double.longBitsToDouble(l), d -> Double.doubleToLongBits(d));

	// Variable-length integers
	BufferCodec<Integer> VARINT = VarintCodec.INSTANCE.map(l -> l.intValue(), i -> i.longValue());
	BufferCodec<Long> VARLONG = VarintCodec.INSTANCE;

	// Sequences
	static BufferCodec<byte[]> fixedByteSeq(int size) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			if (value.length != size)
				throw new IllegalArgumentException("Size mismatch: " + value.length + " != " + size);
			buffer.put(value);
		}, buffer -> {
			return buffer.get(new byte[size]);
		});
	}

	static BufferCodec<short[]> fixedShortSeq(int size) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			if (value.length != size)
				throw new IllegalArgumentException("Size mismatch: " + value.length + " != " + size);
			for (int i = 0; i < size; i++) buffer.putShort(value[i]);
		}, buffer -> {
			short[] out = new short[size];
			for (int i = 0; i < size; i++) out[i] = buffer.getShort();
			return out;
		});
	}

	static BufferCodec<int[]> fixedIntSeq(int size) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			if (value.length != size)
				throw new IllegalArgumentException("Size mismatch: " + value.length + " != " + size);
			for (int i = 0; i < size; i++) buffer.putInt(value[i]);
		}, buffer -> {
			int[] out = new int[size];
			for (int i = 0; i < size; i++) out[i] = buffer.getInt();
			return out;
		});
	}

	static BufferCodec<long[]> fixedLongSeq(int size) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			if (value.length != size)
				throw new IllegalArgumentException("Size mismatch: " + value.length + " != " + size);
			for (int i = 0; i < size; i++) buffer.putLong(value[i]);
		}, buffer -> {
			long[] out = new long[size];
			for (int i = 0; i < size; i++) out[i] = buffer.getLong();
			return out;
		});
	}

	default BufferCodec<List<T>> fixedSequenceOf(int size) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			if (value.size() != size)
				throw new IllegalArgumentException("Size mismatch: " + value.size() + " != " + size);
			for (int i = 0; i < size; i++) encodeTo(buffer, value.get(i));
		}, buffer -> {
			List<T> list = new ArrayList<>();
			for (int i = 0; i < size; i++) list.add(decodeFrom(buffer));
			return Collections.unmodifiableList(list);
		});
	}

	default BufferCodec<List<T>> dynamicSequence() {
		return new SimpleBufferCodec<>((buffer, value) -> {
			VarintCodec.encodePrimitiveTo(buffer, value.size());
			for (int i = 0; i < value.size(); i++) encodeTo(buffer, value.get(i));
		}, buffer -> {
			List<T> list = new ArrayList<>();
			int size = (int) VarintCodec.decodePrimitiveFrom(buffer);
			for (int i = 0; i < size; i++) list.add(decodeFrom(buffer));
			return Collections.unmodifiableList(list);
		});
	}

	BufferCodec<byte[]> DYNAMIC_BYTE_SEQ = new SimpleBufferCodec<>((buffer, value) -> {
		VarintCodec.encodePrimitiveTo(buffer, value.length);
		buffer.put(value);
	}, buffer -> {
		byte[] bs = new byte[(int) VarintCodec.decodePrimitiveFrom(buffer)];
		buffer.get(bs);
		return bs;
	});
	BufferCodec<short[]> DYNAMIC_SHORT_SEQ = new SimpleBufferCodec<>((buffer, value) -> {
		VarintCodec.encodePrimitiveTo(buffer, value.length);
		for (int i = 0; i < value.length; i++) buffer.putShort(value[i]);
	}, buffer -> {
		short[] out = new short[(int) VarintCodec.decodePrimitiveFrom(buffer)];
		for (int i = 0; i < out.length; i++) out[i] = buffer.getShort();
		return out;
	});
	BufferCodec<int[]> DYNAMIC_INT_SEQ = new SimpleBufferCodec<>((buffer, value) -> {
		VarintCodec.encodePrimitiveTo(buffer, value.length);
		for (int i = 0; i < value.length; i++) buffer.putInt(value[i]);
	}, buffer -> {
		int[] out = new int[(int) VarintCodec.decodePrimitiveFrom(buffer)];
		for (int i = 0; i < out.length; i++) out[i] = buffer.getInt();
		return out;
	});
	BufferCodec<long[]> DYNAMIC_LONG_SEQ = new SimpleBufferCodec<>((buffer, value) -> {
		VarintCodec.encodePrimitiveTo(buffer, value.length);
		for (int i = 0; i < value.length; i++) buffer.putLong(value[i]);
	}, buffer -> {
		long[] out = new long[(int) VarintCodec.decodePrimitiveFrom(buffer)];
		for (int i = 0; i < out.length; i++) out[i] = buffer.getLong();
		return out;
	});
	BufferCodec<String> UTF8 = DYNAMIC_BYTE_SEQ.map(bs -> new String(bs, StandardCharsets.UTF_8),
		s -> s.getBytes(StandardCharsets.UTF_8));

	@SuppressWarnings("unchecked")
	default <V> BufferCodec<V> dispatchPrefix(Function<T, BufferCodec<? extends V>> keyToCodec, Function<? extends V, T> objToKey) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			T key = ((Function<V, T>) objToKey).apply(value);
			encodeTo(buffer, key);
			((BufferEncoder<V>) keyToCodec.apply(key)).encodeTo(buffer, value);
		}, buffer -> {
			T key = decodeFrom(buffer);
			BufferCodec<? extends V> codec = keyToCodec.apply(key);
			return codec.decodeFrom(buffer);
		});
	}

	// Tuples
	static <V1> BufferCodec<Tuples.T1<V1>> tuple(BufferCodec<V1> v1) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			v1.encodeTo(buffer, value.v1);
		}, buffer -> {
			V1 a1 = v1.decodeFrom(buffer);
			return new Tuples.T1<>(a1);
		});
	}

	static <V1, V2> BufferCodec<Tuples.T2<V1, V2>> tuple(BufferCodec<V1> v1, BufferCodec<V2> v2) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			v1.encodeTo(buffer, value.v1);
			v2.encodeTo(buffer, value.v2);
		}, buffer -> {
			V1 a1 = v1.decodeFrom(buffer);
			V2 a2 = v2.decodeFrom(buffer);
			return new Tuples.T2<>(a1, a2);
		});
	}

	static <V1, V2, V3> BufferCodec<Tuples.T3<V1, V2, V3>> tuple(BufferCodec<V1> v1, BufferCodec<V2> v2, BufferCodec<V3> v3) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			v1.encodeTo(buffer, value.v1);
			v2.encodeTo(buffer, value.v2);
			v3.encodeTo(buffer, value.v3);
		}, buffer -> {
			V1 a1 = v1.decodeFrom(buffer);
			V2 a2 = v2.decodeFrom(buffer);
			V3 a3 = v3.decodeFrom(buffer);
			return new Tuples.T3<>(a1, a2, a3);
		});
	}

	static <V1, V2, V3, V4> BufferCodec<Tuples.T4<V1, V2, V3, V4>> tuple(BufferCodec<V1> v1, BufferCodec<V2> v2, BufferCodec<V3> v3, BufferCodec<V4> v4) {
		return new SimpleBufferCodec<>((buffer, value) -> {
			v1.encodeTo(buffer, value.v1);
			v2.encodeTo(buffer, value.v2);
			v3.encodeTo(buffer, value.v3);
			v4.encodeTo(buffer, value.v4);
		}, buffer -> {
			V1 a1 = v1.decodeFrom(buffer);
			V2 a2 = v2.decodeFrom(buffer);
			V3 a3 = v3.decodeFrom(buffer);
			V4 a4 = v4.decodeFrom(buffer);
			return new Tuples.T4<>(a1, a2, a3, a4);
		});
	}

	// Misc
	static <T> BufferCodec<T> ofMutableObject(Supplier<T> factory, Consumer<EntryConsumer<T>> builder) {
		List<BufferCodec<?>> types = new ArrayList<>();
		List<Function<T, ?>> getters = new ArrayList<>();
		List<BiConsumer<T, ?>> setters = new ArrayList<>();
		EntryConsumer<T> consumer = new EntryConsumer<T>() {
			@Override
			public <A> void add(BufferCodec<A> type, Function<T, A> getter, BiConsumer<T, A> setter) {
				types.add(type);
				getters.add(getter);
				setters.add(setter);
			}
		};
		builder.accept(consumer);

		return new BufferCodec<T>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void encodeTo(WritableBuffer buffer, T value) {
				for (int i = 0; i < types.size(); i++) {
					BufferCodec type = types.get(i);
					Function<T, ?> getter = getters.get(i);
					type.encodeTo(buffer, getter.apply(value));
				}
			}

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public T decodeFrom(ReadableBuffer buffer) {
				T out = factory.get();

				for (int i = 0; i < types.size(); i++) {
					BufferCodec type = types.get(i);
					BiConsumer setter = setters.get(i);
					setter.accept(out, type.decodeFrom(buffer));
				}

				return out;
			}
		};
	}

	static <K, V> BufferCodec<Map<K, V>> keyValueMap(BufferCodec<K> key, BufferCodec<V> value) {
		return new MapCodec<>(key, value);
	}
}
