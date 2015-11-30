package mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import model.ICountry;

public interface CountryMapper {

	public ICountry find(@Param("code") String code);

	public List<ICountry> pagination(@Param("valueFilterMap") Map<String, Object> valueFilterMap, @Param("from") int from, @Param("to") int to);

	public List<ICountry> toto(@Param("valueFilterMap") Map<String, Object> valueFilterMap, @Param("from") int from, @Param("to") int to);

}
