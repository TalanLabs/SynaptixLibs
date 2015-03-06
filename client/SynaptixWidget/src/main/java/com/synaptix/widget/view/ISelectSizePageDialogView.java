package com.synaptix.widget.view;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;


public interface ISelectSizePageDialogView extends IView {

	void selectSizePage(IResultCallback<Integer> resultCallback, int sizePage);

}
