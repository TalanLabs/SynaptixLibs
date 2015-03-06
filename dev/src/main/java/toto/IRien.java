package toto;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IRien extends IComponent {

	public String getName();

	public void setName(String name);

	public IToto getName2();

	public void setName2(IToto name2);

}
