package io.github.nahkd123.buffercodec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapCodec<K, V> implements BufferCodec<Map<K, V>> {
	private BufferCodec<K> keyType;
	private BufferCodec<V> valueType;

	public MapCodec(BufferCodec<K> keyType, BufferCodec<V> valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}

	@Override
	public void encodeTo(WritableBuffer buffer, Map<K, V> value) {
		VarintCodec.encodePrimitiveTo(buffer, value.size());

		value.forEach((key, val) -> {
			keyType.encodeTo(buffer, key);
			valueType.encodeTo(buffer, val);
		});
	}

	@Override
	public Map<K, V> decodeFrom(ReadableBuffer buffer) {
		Map<K, V> map = new HashMap<>();
		int size = (int) VarintCodec.decodePrimitiveFrom(buffer);

		for (int i = 0; i < size; i++) {
			K key = keyType.decodeFrom(buffer);
			V value = valueType.decodeFrom(buffer);
			map.put(key, value);
		}

		return Collections.unmodifiableMap(map);
	}
}
