package model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;
import com.synaptix.entity.extension.JdbcTypesEnum;

/**
 * State entity
 */
@SynaptixComponent
@Entity
@Table(name = "T_STATE")
@Cache(readOnly = true)
public interface IState extends IEntity {

	@Column(name = "STATE", length = 3, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getState();

	public void setState(String state);

	@Column(name = "ID_COUNTRY", length = 16)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Serializable getIdCountry();

	public void setIdCountry(Serializable idCountry);

	@Column(name = "COUNTRY", length = 3)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCountryName();

	public void setCountryName(String countryName);

	@Collection(sqlTableName = "T_COUNTRY", idSource = "ID_COUNTRY", alias = "c_", componentClass = ICountry.class)
	public ICountry getCountry();

	public void setCountry(ICountry country);
}
