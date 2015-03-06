package com.synaptix.widget.joda.context;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.base.BaseLocal;

public class DefaultLocal<E extends BaseLocal> implements Serializable {

	private static final long serialVersionUID = 363585234973390413L;

	private E local;

	private boolean today;

	public DefaultLocal(E local, boolean today) {
		super();
		this.local = local;
		this.today = today;
	}

	public E getLocal() {
		return local;
	}

	public boolean isToday() {
		return today;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
