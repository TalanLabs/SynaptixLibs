package workflow;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;

@SynaptixComponent
public interface IUser extends IEntity {

	public String getName();

	public void setName(String name);

}
