package com.synaptix.swing;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.synaptix.swing.tree.SyTreeModel;

public class JSyTree extends JTree {

	private static final long serialVersionUID = 6204102304447148328L;

	public JSyTree(SyTreeModel model) {
		super(model);
	}

	public boolean isPathEditable(TreePath path) {
		return getSyTreeModel().isEditable(path);
	}

	public SyTreeModel getSyTreeModel() {
		return (SyTreeModel) getModel();
	}
}
