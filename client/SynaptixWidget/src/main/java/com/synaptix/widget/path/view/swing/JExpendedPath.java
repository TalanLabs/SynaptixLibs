package com.synaptix.widget.path.view.swing;

import com.synaptix.swing.path.JPath;
import com.synaptix.swing.path.PathModel;
import com.synaptix.swing.path.PathSelectionModel;

public class JExpendedPath extends JPath {

	private static final long serialVersionUID = -2832664370948044217L;

	public JExpendedPath(PathModel pathModel) {
		super(pathModel);
	}

	@Override
	protected PathSelectionModel createPathSelectionModel() {
		return new ExtendedPathSelectionModel();
	}
}
