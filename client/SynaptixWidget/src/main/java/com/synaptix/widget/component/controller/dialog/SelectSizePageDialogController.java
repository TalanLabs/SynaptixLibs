package com.synaptix.widget.component.controller.dialog;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.widget.view.ISelectSizePageDialogView;
import com.synaptix.widget.view.ISynaptixViewFactory;

public class SelectSizePageDialogController<F extends ISynaptixViewFactory> {

	private F viewFactory;

	private IView parent;

	private ISelectSizePageDialogView selectSizePageDialogPanel;

	private int min;
	private int max;

	public SelectSizePageDialogController(F viewFactory, IView parent, int min, int max) {
		this.viewFactory = viewFactory;

		this.parent = parent;
		this.min = min;
		this.max = max;

		initialize();
	}

	private void initialize() {
		selectSizePageDialogPanel = viewFactory.newSelectSizePageDialog(parent, min, max);
	}

	public void selectSizePage(IView parent, IResultCallback<Integer> resultCallback, int sizePage) {
		selectSizePageDialogPanel.selectSizePage(resultCallback, sizePage);
	}
}
