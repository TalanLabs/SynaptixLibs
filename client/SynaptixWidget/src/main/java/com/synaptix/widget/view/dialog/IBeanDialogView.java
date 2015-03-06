package com.synaptix.widget.view.dialog;

import java.util.Map;

import com.synaptix.client.view.IView;

public interface IBeanDialogView<E> extends IBeanDialogView0<E> {

	public static final int ACCEPT_AND_REOPEN_OPTION = 2;

	/**
	 * Show dialog
	 * 
	 * @param bean
	 * @param valueMap
	 * @param readOnly
	 * @param creation
	 * @return ACCEPT_OPTION or CANCEL_OPTION
	 */
	public int showDialog(IView parent, String title, String subtitle, E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation);

	/**
	 * Show dialog with a reopen action if asked
	 * 
	 * @param bean
	 * @param valueMap
	 * @param readOnly
	 * @param creation
	 * @param acceptAndReopen
	 * @return ACCEPT_OPTION, ACCEPT_AND_REOPEN_ACTION or CANCEL_OPTION
	 */
	public int showDialog(IView parent, String title, String subtitle, E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation, boolean acceptAndReopen);

}
