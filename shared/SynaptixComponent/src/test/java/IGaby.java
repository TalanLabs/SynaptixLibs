import java.util.List;

import com.synaptix.component.IComponent;

public interface IGaby extends ISandra<Integer, String>, IComponent {

	public List<List<String>> getRien();

	public void setRien(List<List<String>> rien);

}
