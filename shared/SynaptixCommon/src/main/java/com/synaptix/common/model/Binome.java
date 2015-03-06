package com.synaptix.common.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Binome<E, F> implements Serializable {

	private static final long serialVersionUID = -6591841776507864496L;

	private E value1;

	private F value2;

	public Binome() {
		this(null, null);
	}

	public Binome(E value1, F value2) {
		super();
		this.value1 = value1;
		this.value2 = value2;
	}

	public E getValue1() {
		return value1;
	}

	public void setValue1(E value1) {
		this.value1 = value1;
	}

	public F getValue2() {
		return value2;
	}

	public void setValue2(F value2) {
		this.value2 = value2;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
