package toto;

import com.synaptix.gwt.shared.component.IComponentDto;

public interface ITotoDto<E extends Object> extends IComponentDto {

	public String getMeaning();

	public void setMeaning(String meaning);

	public E getTataDto();

	public void setTataDto(E tataDto);

}
