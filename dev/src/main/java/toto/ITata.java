package toto;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;

@SynaptixComponent
public interface ITata extends IEntity, ITracable {

	public String getMeaning();

	public void setMeaning(String meaning);

	public ITiti getTiti();

	public void setTiti(ITiti titi);

}
