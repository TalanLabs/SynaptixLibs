package com.synaptix.client.common.swing.view.dialog;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.client.common.view.dialog.IBeanWithValidationDialogView;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.DefaultBeanDialog;


public class BeanWithValidationDialog<E> extends DefaultBeanDialog<E> implements IBeanWithValidationDialogView<E> {

	private static final long serialVersionUID = -117835564629313337L;

	private final Action validateAction;

	private boolean validateActive;

	private boolean validateVisible;

	public BeanWithValidationDialog(IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(true, null, beanExtensionDialogs);
	}

	public BeanWithValidationDialog(String validateText, IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(true, validateText, beanExtensionDialogs);
	}

	public BeanWithValidationDialog(boolean hideListIfAlone, String validateText, IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(hideListIfAlone, false, StaticCommonHelper.getCommonConstantsBundle().save(), null, null, validateText, beanExtensionDialogs);
	}

	public BeanWithValidationDialog(boolean hideListIfAlone, boolean acceptActionEnabled, String acceptActionLabel, String cancelActionLabel, String closeActionLabel, String validateText,
			final IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		super(hideListIfAlone, acceptActionEnabled, acceptActionLabel, cancelActionLabel, closeActionLabel, beanExtensionDialogs);

		validateAction = new AbstractAcceptAction(validateText != null ? validateText : StaticCommonHelper.getCommonConstantsBundle().validate()) {

			private static final long serialVersionUID = -1232688344965018871L;

			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = VALIDATE;

				for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
					b.commit(bean, valueMap);
				}

				closeDialog();
			}

			@Override
			public void setEnabled(boolean newValue) {
				super.setEnabled(validateActive && newValue);
			}
		};
		getAcceptAction().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("enabled".equals(evt.getPropertyName())) { //$NON-NLS-1$
					validateAction.setEnabled(validateActive && (Boolean) evt.getNewValue());
				}
			}
		});
		validateActive = true;
		validateVisible = true;
	}

	@Override
	protected Action[] getOthersActions() {
		if (validateVisible) {
			return new Action[] { validateAction };
		}
		return new Action[0];
	}

	@Override
	public void setValidateActive(boolean validateActive) {
		this.validateActive = validateActive;
		if (!validateActive) {
			validateAction.setEnabled(false);
		} else {
			validateAction.setEnabled(getAcceptAction().isEnabled());
		}
	}

	@Override
	public void setValidateVisible(boolean validateVisible) {
		this.validateVisible = validateVisible;
	}

	@Override
	public void setBean(E bean) {
		this.bean = bean;
	}
}
