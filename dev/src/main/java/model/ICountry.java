package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;

/**
 * Country entity
 */
@SynaptixComponent
@Entity
@Table(name = "T_COUNTRY")
@Cache(readOnly = true)
public interface ICountry extends IEntity, INlsMessage {

	@Column(name = "COUNTRY", length = 255, nullable = false)
	@UpperOnly
	@DefaultValue("'1'")
	@BusinessKey
	public String getCountry();

	public void setCountry(String country);

	@Column(name = "ISO_COUNTRY_CODE", length = 3)
	public String getIsoCountryCode();

	public void setIsoCountryCode(String isoCountryCode);

	@Column(name = "ISO_COUNTRY_NO", length = 3)
	public String getIsoCountryNo();

	public void setIsoCountryNo(String isoCountryNo);

}
