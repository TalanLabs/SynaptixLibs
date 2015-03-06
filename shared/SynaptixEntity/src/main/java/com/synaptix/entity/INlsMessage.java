package com.synaptix.entity;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

@SynaptixComponent
public interface INlsMessage extends IComponent, IDatabaseComponentExtension {

	@Column(name = "MEANING", length = 70)
	@NlsColumn
	public String getMeaning();

	public void setMeaning(String meaning);

}
