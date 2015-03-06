package pathTree;

public interface PathTreeModel {

	public abstract Object getRoot();

	public abstract int getNodeCount(Object parent);

	public abstract Object getNode(Object parent, int index);

	public void addPathTreeModelListener(PathTreeModelListener l);

	public void removePathTreeModelListener(PathTreeModelListener l);
}
