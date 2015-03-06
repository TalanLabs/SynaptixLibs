package com.synaptix.widget.view.dialog;

import java.awt.Component;
import java.util.List;

public interface IChooseListDialog<E> {

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	int showDialog(Component parent);

	List<E> getValueList();

}
