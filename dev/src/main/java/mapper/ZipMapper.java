package mapper;

import java.io.Serializable;

import model.IZip;

import org.apache.ibatis.annotations.Param;

public interface ZipMapper {

	public IZip find(@Param("city") String city, @Param("zip") String zip, @Param("idCountry") Serializable idCountry);

}
