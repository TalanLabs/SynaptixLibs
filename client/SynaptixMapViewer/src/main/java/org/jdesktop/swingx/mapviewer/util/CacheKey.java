package org.jdesktop.swingx.mapviewer.util;

import java.util.Arrays;

public class CacheKey {

	private final Class<?> cls;
	private final String key;
	private final Object[] params;
	private int hash = 0;

	public CacheKey(Class<?> cls, String key, Object... params) {
		super();

		this.cls = cls;
		this.key = key;
		this.params = params;
	}

	public CacheKey(String key, Object... params) {
		this(null, key, params);
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;

		CacheKey cacheKey = (CacheKey) o;

		if (this.cls != null ? !this.cls.equals(cacheKey.cls)
				: cacheKey.cls != null) {
			return false;
		}
		if (this.key != null ? !this.key.equals(cacheKey.key)
				: cacheKey.key != null) {
			return false;
		}
		// noinspection RedundantIfStatement
		if (!Arrays.deepEquals(this.params, cacheKey.params)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		if (this.hash == 0) {
			int result;
			result = (this.cls != null ? cls.hashCode() : 0);
			result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
			result = 31
					* result
					+ (this.params != null ? Arrays.deepHashCode(this.params)
							: 0);
			this.hash = result;
		}
		return this.hash;
	}
}
