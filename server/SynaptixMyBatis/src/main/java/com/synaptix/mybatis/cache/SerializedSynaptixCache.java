package com.synaptix.mybatis.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.io.Resources;

class SerializedSynaptixCache implements ISynaptixCache {

	private ISynaptixCache delegate;

	public SerializedSynaptixCache(ISynaptixCache delegate) {
		this.delegate = delegate;
	}

	public String getId() {
		return delegate.getId();
	}

	public int getSize() {
		return delegate.getSize();
	}

	public void putObject(Object key, Object object) {
		if (object == null || object instanceof Serializable) {
			delegate.putObject(key, serialize((Serializable) object));
		} else {
			throw new CacheException("SharedCache failed to make a copy of a non-serializable object: " + object);
		}
	}

	public Object getObject(Object key) {
		Object object = delegate.getObject(key);
		return object == null ? null : deserialize((byte[]) object);
	}

	public Object removeObject(Object key) {
		return delegate.removeObject(key);
	}

	public void clear(boolean fire) {
		delegate.clear(fire);
	}

	public ReadWriteLock getReadWriteLock() {
		return delegate.getReadWriteLock();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	private byte[] serialize(Serializable value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			oos.flush();
			oos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			throw new CacheException("Error serializing object.  Cause: " + e, e);
		}
	}

	private Serializable deserialize(byte[] value) {
		Serializable result;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(value);
			ObjectInputStream ois = new CustomObjectInputStream(bis);
			result = (Serializable) ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new CacheException("Error deserializing object.  Cause: " + e, e);
		}
		return result;
	}

	public static class CustomObjectInputStream extends ObjectInputStream {

		public CustomObjectInputStream(InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return Resources.classForName(desc.getName());
		}

	}
}
