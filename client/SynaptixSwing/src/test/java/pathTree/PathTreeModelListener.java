package pathTree;

import java.util.EventListener;

public interface PathTreeModelListener extends EventListener {

	public abstract void pathTreeModelChanged(PathTreeModelEvent e);

}
