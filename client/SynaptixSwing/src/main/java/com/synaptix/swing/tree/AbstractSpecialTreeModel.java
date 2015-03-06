package com.synaptix.swing.tree;

import javax.swing.tree.TreePath;

public abstract class AbstractSpecialTreeModel extends AbstractTreeModel
		implements SyTreeModel {

	public boolean isEditable(TreePath path) {
		return false;
	}
}
