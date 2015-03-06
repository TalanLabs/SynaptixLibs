package com.synaptix.mybatis.handler;

import oracle.sql.RAW;

public class MyRAW extends RAW {

	private static final long serialVersionUID = 7930190858459584740L;

	public MyRAW(byte[] bytes) {
		super(bytes);
	}

	@Override
	public String toString() {
		return this.stringValue();
	}
}
