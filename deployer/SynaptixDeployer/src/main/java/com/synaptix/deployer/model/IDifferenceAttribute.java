package com.synaptix.deployer.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IDifferenceAttribute extends IComponent {

	public String getAttribute();

	public void setAttribute(String attribute);

	public Object getValue1();

	public void setValue1(Object value1);

	public Object getValue2();

	public void setValue2(Object value2);

}
