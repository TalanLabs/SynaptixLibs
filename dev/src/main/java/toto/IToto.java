package toto;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;

@SynaptixComponent
public interface IToto extends IEntity {

	public String getMeaning();

	public void setMeaning(String meaning);

	public ITata getTata();

	public void setTata(ITata tata);

	public ITracable getTracable();

	public void setTracable(ITracable tracable);

}
