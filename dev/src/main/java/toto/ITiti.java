package toto;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface ITiti extends IComponent {

	public String getMeaning();

	public void setMeaning(String meaning);

}
