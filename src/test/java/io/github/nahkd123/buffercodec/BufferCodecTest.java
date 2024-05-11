package io.github.nahkd123.buffercodec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class BufferCodecTest {
	@Test
	void test() {
		BufferCodec<UUID> uuidCodec = BufferCodec
			.tuple(BufferCodec.LONG, BufferCodec.LONG)
			.map(
				t -> new UUID(t.v1, t.v2),
				uuid -> new Tuples.T2<>(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

		ByteArrayWritableBuffer out = new ByteArrayWritableBuffer();
		UUID randomUUID = UUID.randomUUID();
		uuidCodec.encodeTo(out, randomUUID);

		ByteArrayReadableBuffer in = new ByteArrayReadableBuffer(out);
		UUID decodedUUID = uuidCodec.decodeFrom(in);
		assertEquals(randomUUID, decodedUUID);

		Map<UUID, String> referenceMap = new HashMap<>();
		referenceMap.put(UUID.randomUUID(), "nahkd123");
		referenceMap.put(UUID.randomUUID(), "nahkd546");

		BufferCodec<Map<UUID, String>> mapCodec = BufferCodec.keyValueMap(uuidCodec, BufferCodec.UTF8);
		out = new ByteArrayWritableBuffer();
		mapCodec.encodeTo(out, referenceMap);
		Map<UUID, String> decodedMap = mapCodec.decodeFrom(new ByteArrayReadableBuffer(out));
		assertEquals(referenceMap, decodedMap);
	}
}
