package toto;

import com.synaptix.gwt.annotation.SynaptixGWTComponent;
import com.synaptix.gwt.shared.component.IComponentDto;

@SynaptixGWTComponent
public interface ITitiDto extends IComponentDto {

	public String getMeaning();

	public void setMeaning(String meaning);

}
