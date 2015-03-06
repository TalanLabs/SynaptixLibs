package com.synaptix.swing.tree;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public interface SyTreeModel extends TreeModel {

	public abstract boolean isEditable(TreePath path);
	
}
