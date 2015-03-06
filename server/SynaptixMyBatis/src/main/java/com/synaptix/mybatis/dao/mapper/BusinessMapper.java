package com.synaptix.mybatis.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.synaptix.entity.IEntity;

public interface BusinessMapper {

	@SelectProvider(type = BusinessProvider.class, method = "checkUnicityConstraint")
	public <E extends IEntity> int checkUnicityConstraint(@Param("entity") E entity);

}
