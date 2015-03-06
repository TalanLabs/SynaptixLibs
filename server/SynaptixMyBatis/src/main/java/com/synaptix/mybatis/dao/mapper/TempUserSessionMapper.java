package com.synaptix.mybatis.dao.mapper;

import java.io.Serializable;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TempUserSessionMapper {

	@Select("call dbms_session.set_identifier(#{login,jdbcType=VARCHAR})")
	public void insertUserSession(String login);

	@Insert("insert into T_TEMP_USER_SESSION (ID_USER,LOGIN, COUNTRY,LANGUAGE) VALUES (#{idUser},#{login,jdbcType=VARCHAR},#{country,jdbcType=VARCHAR},#{language,jdbcType=VARCHAR})")
	public void insertTempUserSession(@Param("idUser") Serializable idUser, @Param("login") String login, @Param("country") String country, @Param("language") String language);

}
