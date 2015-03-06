package com.synaptix.mybatis.cache;

public interface SynaptixCacheListener {

	public abstract void cleared(String idCache, boolean propagation);

}
