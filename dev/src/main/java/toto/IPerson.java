package toto;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IPerson extends IComponent {

	public String getName();

	public void setName(String name);

	public String getSurname();

	public void setSurname(String surname);

	public String getZip();

	public void setZip(String zip);

	public String getCity();

	public void setCity(String city);

	public String getCountry();

	public void setCountry(String country);

}
