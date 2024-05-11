package io.github.nahkd123.buffercodec;

public final class Tuples {
	private Tuples() {}

	public static final class T1<V1> {
		public final V1 v1;

		public T1(V1 v1) {
			this.v1 = v1;
		}
	}

	public static final class T2<V1, V2> {
		public final V1 v1;
		public final V2 v2;

		public T2(V1 v1, V2 v2) {
			this.v1 = v1;
			this.v2 = v2;
		}
	}

	public static final class T3<V1, V2, V3> {
		public final V1 v1;
		public final V2 v2;
		public final V3 v3;

		public T3(V1 v1, V2 v2, V3 v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
	}

	public static final class T4<V1, V2, V3, V4> {
		public final V1 v1;
		public final V2 v2;
		public final V3 v3;
		public final V4 v4;

		public T4(V1 v1, V2 v2, V3 v3, V4 v4) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.v4 = v4;
		}
	}
}
