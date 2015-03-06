package com.synaptix.sender.fax;

import com.synaptix.sender.Attachment;

public interface IFaxFactory {

	public abstract String sendFax(String faxNumber, Attachment attachment)
			throws Exception;

}
