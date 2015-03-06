package com.synaptix.widget.hierarchical.view.swing.component.title;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.component.field.IField;

@SynaptixComponent
public interface IHierarchicalTitleColumnElement extends IComponent {

	@EqualsKey
	public IField getColumnElement();

	public void setColumnElement(final IField element);

	public boolean isColumnVisible();

	public void setColumnVisible(final boolean isVisible);

	public boolean isLock();

	public void setLock(final boolean isLock);
}
