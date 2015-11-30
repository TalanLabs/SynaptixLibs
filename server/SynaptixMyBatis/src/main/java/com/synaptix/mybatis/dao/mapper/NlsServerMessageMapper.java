package com.synaptix.mybatis.dao.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.synaptix.entity.IId;

public interface NlsServerMessageMapper {

	@Update("MERGE INTO t_nlsservermessage a USING (SELECT 1 FROM DUAL) ON (a.table_name = #{tableName} AND a.id_object = #{idObject} AND a.LANGUAGE = #{language,jdbcType=VARCHAR} AND a.COLUMN_NAME = #{columnName}) WHEN MATCHED THEN UPDATE SET a.meaning = #{meaning,jdbcType=VARCHAR} WHEN NOT MATCHED THEN INSERT (TABLE_NAME, ID_OBJECT, LANGUAGE,COLUMN_NAME, MEANING) VALUES (#{tableName}, #{idObject}, #{language,jdbcType=VARCHAR}, #{columnName}, #{meaning,jdbcType=VARCHAR})")
	public int mergeNlsServerMessage(@Param("tableName") String tableName, @Param("idObject") IId idObject, @Param("language") String language, @Param("columnName") String columnName,
									 @Param("meaning") String meaning);

	@Delete("DELETE FROM t_nlsservermessage a where a.table_name = #{tableName} AND a.id_object = #{idObject}")
	public int deleteNlsServerMessages(@Param("tableName") String tableName, @Param("idObject") IId idObject);
}
