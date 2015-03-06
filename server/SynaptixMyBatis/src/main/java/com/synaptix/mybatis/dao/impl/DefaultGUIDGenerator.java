package com.synaptix.mybatis.dao.impl;

import java.util.UUID;

import com.synaptix.mybatis.dao.IGUIDGenerator;

/**
 * Generate a unique GUID
 * 
 * @author Gaby
 * 
 */
public class DefaultGUIDGenerator implements IGUIDGenerator {

	public DefaultGUIDGenerator() {
	}

	@Override
	public String newGUID() {
		UUID uuid = UUID.randomUUID();
		String rep = uuid.toString();
		return rep.replaceAll("-", "").toUpperCase();
	}
}
