package com.synaptix.mybatis.hack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.NullCacheKey;

public class SynaptixCacheKey extends CacheKey {

	private static final long serialVersionUID = -6427282518473082077L;

	public static final CacheKey NULL_CACHE_KEY = new NullCacheKey();

	private static final int DEFAULT_MULTIPLYER = 37;
	private static final int DEFAULT_HASHCODE = 17;

	private int multiplier;
	private int hashcode;
	private long checksum;
	private int count;
	private List<Object> updateList;

	public SynaptixCacheKey() {
		this.hashcode = DEFAULT_HASHCODE;
		this.multiplier = DEFAULT_MULTIPLYER;
		this.count = 0;
		this.updateList = new ArrayList<Object>();
	}

	public SynaptixCacheKey(Object[] objects) {
		this();
		updateAll(objects);
	}

	public int getUpdateCount() {
		return updateList.size();
	}

	public void update(Object object) {
		int baseHashCode;
		if (object == null) {
			baseHashCode = 1;
		} else if (object.getClass().isArray()) {
			baseHashCode = arrayHashCode(object.getClass(), object);
		} else {
			baseHashCode = object.hashCode();
		}

		count++;
		checksum += baseHashCode;
		baseHashCode *= count;

		hashcode = multiplier * hashcode + baseHashCode;

		updateList.add(object);
	}

	public void updateAll(Object[] objects) {
		for (Object o : objects) {
			update(o);
		}
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (!(object instanceof SynaptixCacheKey))
			return false;

		final SynaptixCacheKey cacheKey = (SynaptixCacheKey) object;

		if (hashcode != cacheKey.hashcode)
			return false;
		if (checksum != cacheKey.checksum)
			return false;
		if (count != cacheKey.count)
			return false;

		for (int i = 0; i < updateList.size(); i++) {
			Object thisObject = updateList.get(i);
			Object thatObject = cacheKey.updateList.get(i);
			if (thisObject == null) {
				if (thatObject != null)
					return false;
			} else {
				if (thisObject.getClass().isArray() && thatObject.getClass().isArray() && thisObject.getClass().equals(thatObject.getClass())) {
					if (!arrayEquals(thisObject.getClass(), thisObject, thatObject)) {
						return false;
					}
				} else if (!thisObject.equals(thatObject)) {
					return false;
				}
			}
		}
		return true;
	}

	public int hashCode() {
		return hashcode;
	}

	public String toString() {
		StringBuilder returnValue = new StringBuilder().append(hashcode).append(':').append(checksum);
		for (int i = 0; i < updateList.size(); i++) {
			returnValue.append(':');
			Object object = updateList.get(i);
			if (object != null && object.getClass().isArray()) {
				returnValue.append(arrayString(object.getClass(), object));
			} else {
				returnValue.append(object);
			}
		}

		return returnValue.toString();
	}

	@Override
	public CacheKey clone() throws CloneNotSupportedException {
		SynaptixCacheKey clonedCacheKey = (SynaptixCacheKey) super.clone();
		clonedCacheKey.updateList = new ArrayList<Object>(updateList);
		return clonedCacheKey;
	}

	private static final int arrayHashCode(Class<?> type, Object value) {
		Class<?> clazz = type.getComponentType();
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Arrays.hashCode((int[]) value);
			} else if (clazz == byte.class) {
				return Arrays.hashCode((byte[]) value);
			} else if (clazz == short.class) {
				return Arrays.hashCode((short[]) value);
			} else if (clazz == long.class) {
				return Arrays.hashCode((long[]) value);
			} else if (clazz == float.class) {
				return Arrays.hashCode((float[]) value);
			} else if (clazz == double.class) {
				return Arrays.hashCode((double[]) value);
			} else if (clazz == char.class) {
				return Arrays.hashCode((char[]) value);
			} else if (clazz == boolean.class) {
				return Arrays.hashCode((boolean[]) value);
			} else {
				return 1;
			}
		} else {
			return Arrays.deepHashCode((Object[]) value);
		}
	}

	private static final boolean arrayEquals(Class<?> type, Object value1, Object value2) {
		Class<?> clazz = type.getComponentType();
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Arrays.equals((int[]) value1, (int[]) value2);
			} else if (clazz == byte.class) {
				return Arrays.equals((byte[]) value1, (byte[]) value2);
			} else if (clazz == short.class) {
				return Arrays.equals((short[]) value1, (short[]) value2);
			} else if (clazz == long.class) {
				return Arrays.equals((long[]) value1, (long[]) value2);
			} else if (clazz == float.class) {
				return Arrays.equals((float[]) value1, (float[]) value2);
			} else if (clazz == double.class) {
				return Arrays.equals((double[]) value1, (double[]) value2);
			} else if (clazz == char.class) {
				return Arrays.equals((char[]) value1, (char[]) value2);
			} else if (clazz == boolean.class) {
				return Arrays.equals((boolean[]) value1, (boolean[]) value2);
			} else {
				return false;
			}
		} else {
			return Arrays.deepEquals((Object[]) value1, (Object[]) value2);
		}
	}

	private static final String arrayString(Class<?> type, Object value) {
		Class<?> clazz = type.getComponentType();
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Arrays.toString((int[]) value);
			} else if (clazz == byte.class) {
				return Arrays.toString((byte[]) value);
			} else if (clazz == short.class) {
				return Arrays.toString((short[]) value);
			} else if (clazz == long.class) {
				return Arrays.toString((long[]) value);
			} else if (clazz == float.class) {
				return Arrays.toString((float[]) value);
			} else if (clazz == double.class) {
				return Arrays.toString((double[]) value);
			} else if (clazz == char.class) {
				return Arrays.toString((char[]) value);
			} else if (clazz == boolean.class) {
				return Arrays.toString((boolean[]) value);
			} else {
				return null;
			}
		} else {
			return Arrays.deepToString((Object[]) value);
		}
	}
}
