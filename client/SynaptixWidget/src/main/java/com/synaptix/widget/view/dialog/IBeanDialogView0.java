package com.synaptix.widget.view.dialog;

import java.util.Map;

import com.synaptix.client.view.IView;

public interface IBeanDialogView0<E> extends IView {

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	/**
	 * Change title for dialog
	 * 
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * Change subtitle for dialog
	 * 
	 * @param subtitle
	 */
	public void setSubtitle(String subtitle);

	public void closeDialog();

	/**
	 * Simulates a click on "accept" button
	 */
	public void accept();

	/**
	 * Return a bean
	 * 
	 * @return
	 */
	public E getBean();

	/**
	 * Return a value map
	 * 
	 * @return
	 */
	public Map<String, Object> getValueMap();
}
