package com.synaptix.client.common.swing.view;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.client.common.swing.view.dialog.BeanWithValidationDialog;
import com.synaptix.client.common.swing.view.dialog.ChooseFileTranslationDialog;
import com.synaptix.client.common.swing.view.dialog.ChooseParamImportExcelDialog;
import com.synaptix.client.common.swing.view.dialog.ShowImportNlsMessageDatasDialog;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.client.common.view.dialog.IBeanWithValidationDialogView;
import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.helper.INlsMessageData;
import com.synaptix.taskmanager.model.XlsSheet;
import com.synaptix.taskmanager.view.ICommonViewFactory;
import com.synaptix.taskmanager.view.swing.ChooseFileTranslationDialogResult;
import com.synaptix.taskmanager.view.swing.ChooseParamImportExcelResult;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.SwingSynaptixViewFactory;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

public class SwingCommonViewFactory extends SwingSynaptixViewFactory implements ICommonViewFactory {

	@Override
	public ChooseFileTranslationDialogResult importFileTranslationDialog(IView view) {
		ChooseFileTranslationDialogResult res = null;
		ChooseFileTranslationDialog dialog = new ChooseFileTranslationDialog(false);
		if (dialog.showDialog(view, StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), null) == ChooseFileTranslationDialog.ACCEPT_OPTION) {
			res = new ChooseFileTranslationDialogResult(dialog.getSelectedFile(), dialog.getSelectedLocale());
		}
		return res;
	}

	@Override
	public ChooseFileTranslationDialogResult exportFileTranslationDialog(IView view) {
		ChooseFileTranslationDialogResult res = null;
		ChooseFileTranslationDialog dialog = new ChooseFileTranslationDialog(true);
		if (dialog.showDialog(view, StaticCommonHelper.getCommonConstantsBundle().exportATranslationFile(), null) == ChooseFileTranslationDialog.ACCEPT_OPTION) {
			res = new ChooseFileTranslationDialogResult(dialog.getSelectedFile(), dialog.getSelectedLocale());
		}
		return res;
	}

	@Override
	public ChooseParamImportExcelResult chooseParamImportExcel(IView view, boolean useBundleName, XlsSheet[] sheets) {
		ChooseParamImportExcelResult res = null;
		ChooseParamImportExcelDialog dialog = new ChooseParamImportExcelDialog(useBundleName, sheets);
		if (dialog.showDialog(view, StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), null) == ChooseParamImportExcelDialog.ACCEPT_OPTION) {
			res = new ChooseParamImportExcelResult(dialog.getSheet(), dialog.getColumnBundleName(), dialog.getColumnKey(), dialog.getColumnTraduction(), dialog.getRowStart());
		}
		return res;
	}

	@Override
	public boolean showImportNlsMessageDatas(IView view, List<INlsMessageData> nlsMessageDatas) {
		ShowImportNlsMessageDatasDialog dialog = new ShowImportNlsMessageDatasDialog(nlsMessageDatas);
		return dialog.showDialog(view, StaticCommonHelper.getCommonConstantsBundle().importATranslationFile(), null) == ShowImportNlsMessageDatasDialog.ACCEPT_OPTION;
	}

	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(F... beanExtensionDialogViews) {
		return newBeanWithValidationDialogView(true, beanExtensionDialogViews);
	}

	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(String validateText, F... beanExtensionDialogViews) {
		return newBeanWithValidationDialogView(true, validateText, beanExtensionDialogViews);
	}

	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(boolean hideListIfAlone, F... beanExtensionDialogViews) {
		return newBeanWithValidationDialogView(hideListIfAlone, null, beanExtensionDialogViews);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E, F extends IBeanExtensionDialogView<E>> IBeanWithValidationDialogView<E> newBeanWithValidationDialogView(boolean hideListIfAlone, String validateText, F... beanExtensionDialogViews) {
		List<AbstractBeanExtensionDialog<E>> list = new ArrayList<AbstractBeanExtensionDialog<E>>();
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogViews) {
			list.add((AbstractBeanExtensionDialog<E>) e);
		}
		return new BeanWithValidationDialog<E>(hideListIfAlone, validateText, list.toArray(new IBeanExtensionDialogView[list.size()]));
	}
}
