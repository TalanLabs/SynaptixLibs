package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;

@SynaptixComponent
@Entity
@Table(name = "T_ZIP")
@Cache(readOnly = true)
public interface IZip extends IEntity {

	@Column(name = "ID_COUNTRY")
	@Collection(sqlTableName = "T_COUNTRY", idSource = "ID_COUNTRY", alias = "co_", componentClass = ICountry.class)
	public ICountry getCountry();

	public void setCountry(ICountry country);

	@Column(name = "COUNTRY")
	public String getCountryName();

	public void setCountryName(String countryName);

	@Column(name = "ZIP")
	public String getZip();

	public void setZip(String zip);

	@Column(name = "CITY")
	public String getCity();

	public void setCity(String city);

	@Column(name = "STATE")
	public String getState();

	public void setState(String state);

	@Column(name = "ID_STATE")
	public IId getIdState();

	public void setIdState(IId idState);

	@Column(name = "TIME_ZONE")
	public String getTimeZone();

	public void setTimeZone(String timeZone);

	@Column(name = "GEOPOINT_LONGITUDE")
	public Double getGeopointLongitude();

	public void setGeopointLongitude(Double geopointLongitude);

	@Column(name = "GEOPOINT_LATITUDE")
	public Double getGeopointLatitude();

	public void setGeopointLatitude(Double geopointLatitude);

	@Column(name = "X")
	public Double getX();

	public void setX(Double x);

	@Column(name = "Y")
	public Double getY();

	public void setY(Double y);

}
