package com.synaptix.pmgr.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface ISendInformation extends IComponent {

	public boolean isValid();

	public void setValid(boolean valid);

	public String getMessageContent();

	public void setMessageContent(String content);

}
