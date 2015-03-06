package com.synaptix.redpepper.client.controller.impl;

import com.synaptix.redpepper.client.bean.PageInfoDto;
import com.synaptix.redpepper.client.controller.entry.ICellListController;
import com.synaptix.redpepper.client.controller.entry.IDomainController;

public class PageCellListController implements ICellListController<PageInfoDto> {

	private final IDomainController controller;

	public PageCellListController(IDomainController controller) {
		this.controller = controller;
	}

	@Override
	public void onCellChange(PageInfoDto e) {
		// controller.loadPageElements();
		controller.displayPageElements(e);
	}

}
