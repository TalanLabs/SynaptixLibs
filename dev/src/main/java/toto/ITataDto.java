package toto;

import com.synaptix.gwt.annotation.SynaptixGWTComponent;
import com.synaptix.gwt.shared.component.IComponentDto;

@SynaptixGWTComponent
public interface ITataDto extends IComponentDto {

	public String getMeaning();

	public void setMeaning(String meaning);

	public ITitiDto getTitiDto();

	public void setTitiDto(ITitiDto titiDto);

}
