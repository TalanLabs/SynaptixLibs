package com.synaptix.taskmanager.view;

import java.util.List;

import com.synaptix.client.common.view.dialog.IBeanWithValidationDialogView;
import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.helper.INlsMessageData;
import com.synaptix.taskmanager.model.XlsSheet;
import com.synaptix.taskmanager.view.swing.ChooseFileTranslationDialogResult;
import com.synaptix.taskmanager.view.swing.ChooseParamImportExcelResult;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;

public interface ICommonViewFactory extends ISynaptixViewFactory {

	/**
	 * Open file translation dialog
	 * 
	 * @param view
	 * @return
	 */
	public ChooseFileTranslationDialogResult importFileTranslationDialog(IView view);

	/**
	 * Save file translation dialog
	 * 
	 * @param view
	 * @return
	 */
	public ChooseFileTranslationDialogResult exportFileTranslationDialog(IView view);

	/**
	 * Choose param for import excel
	 * 
	 * @param view
	 * @param useBundleName
	 * @param sheets
	 * @return
	 */
	public ChooseParamImportExcelResult chooseParamImportExcel(IView view, boolean useBundleName, XlsSheet[] sheets);

	/**
	 * Show import nls message datas
	 * 
	 * @param view
	 * @param nlsMessageDatas
	 * @return
	 */
	public boolean showImportNlsMessageDatas(IView view, List<INlsMessageData> nlsMessageDatas);

	/**
	 * Create a bean dialog with save/validate actions
	 * 
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(F... beanExtensionDialogViews);

	/**
	 * Create a bean dialog with save/validate actions
	 * 
	 * @param validateText
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(String validateText, F... beanExtensionDialogViews);

	/**
	 * Create a bean dialog with save/validate actions
	 * 
	 * @param hideListIfAlone
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(boolean hideListIfAlone, F... beanExtensionDialogViews);

	/**
	 * Create a bean dialog with save/validate actions
	 * 
	 * @param hideListIfAlone
	 * @param validateText
	 * @param beanExtensionDialogViews
	 * @return
	 */
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(boolean hideListIfAlone, String validateText, F... beanExtensionDialogViews);

}
