package com.synaptix.redpepper.client.controller.impl;

import com.synaptix.redpepper.client.bean.ElementInfoDto;
import com.synaptix.redpepper.client.controller.entry.ICellListController;
import com.synaptix.redpepper.client.controller.entry.IDomainController;

public class ElementCellListController implements ICellListController<ElementInfoDto> {

	private final IDomainController controller;

	public ElementCellListController(IDomainController controller) {
		this.controller = controller;
	}

	@Override
	public void onCellChange(ElementInfoDto e) {
		controller.displayElementInfo(e);
	}

}
