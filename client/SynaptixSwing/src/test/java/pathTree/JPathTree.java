package pathTree;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.swing.plaf.PathTreeUI;

public class JPathTree extends JComponent implements PathTreeModelListener {

	private static final long serialVersionUID = -633025845919526689L;

	private static final String uiClassID = "PathTreeUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicPathTreeUI"); //$NON-NLS-1$
	}

	private PathTreeModel pathTreeModel;

	public JPathTree(PathTreeModel pathTreeModel) {
		super();

		this.pathTreeModel = pathTreeModel;
		
		updateUI();
	}

	public PathTreeModel getPathTreeModel() {
		return pathTreeModel;
	}

	public PathTreeUI getUI() {
		return (PathTreeUI) ui;
	}

	public void setUI(PathTreeUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((PathTreeUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void pathTreeModelChanged(PathTreeModelEvent e) {
		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}
}
