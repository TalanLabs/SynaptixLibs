package com.synaptix.widget.view.swing.helper;

import javax.swing.Icon;

public interface IPopupMenuActionsBuilder extends IActionsBuilder<IPopupMenuActionsBuilder> {

	public IPopupMenuActionsBuilder addSubPopupMenu(Icon icon, String name);

}
