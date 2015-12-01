package mapper;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;

import model.IZip;

public interface ZipMapper {

	public IZip find(@Param("city") String city, @Param("zip") String zip, @Param("idCountry") IId idCountry);

}
