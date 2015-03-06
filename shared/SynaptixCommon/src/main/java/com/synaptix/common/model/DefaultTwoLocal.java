package com.synaptix.common.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.base.BaseLocal;

public class DefaultTwoLocal<E extends BaseLocal> extends Binome<E, E> implements Serializable {

	private static final long serialVersionUID = 363585234973390413L;

	public DefaultTwoLocal(E minLocal, E maxLocal) {
		super(minLocal, maxLocal);
	}

	public E getMinLocal() {
		return getValue1();
	}

	public E getMaxLocal() {
		return getValue2();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
