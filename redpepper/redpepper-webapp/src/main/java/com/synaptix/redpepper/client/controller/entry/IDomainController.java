package com.synaptix.redpepper.client.controller.entry;

import com.synaptix.redpepper.client.bean.ElementInfoDto;
import com.synaptix.redpepper.client.bean.PageInfoDto;
import com.synaptix.redpepper.client.view.impl.DomainTabViewImpl;

public interface IDomainController {

	public void loadAllPages(DomainTabViewImpl view);

	public void setView(DomainTabViewImpl domainTabViewImpl);

	public void displayElementInfo(PageInfoDto e);

	public void displayPageElements(PageInfoDto e);

	public void displayElementInfo(ElementInfoDto e);

	public void onSaveElement(ElementInfoDto elementInfo);

}
