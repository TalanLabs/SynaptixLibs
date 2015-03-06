package com.synaptix.deployer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class SynaptixDatabase {

	private List<ISynaptixDatabaseSchema> dbs;

	public SynaptixDatabase(ISynaptixDatabaseSchema[] dbs) {
		super();

		this.dbs = asListOf(ISynaptixDatabaseSchema.class, dbs);
	}

	public final List<ISynaptixDatabaseSchema> getDbs() {
		return dbs;
	}

	public final <T> List<T> asListOf(Class<T> clazz, T... ts) {
		if (ts != null) {
			final int length = ts.length;
			final List<T> ret = new ArrayList<T>(length);
			if (length > 0) {
				for (T t : ts) {
					ret.add(t);
				}
			}
			return ret;
		}
		return null;
	}
}
