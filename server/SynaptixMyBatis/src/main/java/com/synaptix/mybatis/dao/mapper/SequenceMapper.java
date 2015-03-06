package com.synaptix.mybatis.dao.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.SelectProvider;

public interface SequenceMapper {

	/**
	 * Select in a sequence
	 * 
	 * @param string
	 * @return
	 */
	@SelectProvider(type = SequenceProvider.class, method = "selectSequence")
	public BigDecimal selectSequence(String string);

}
