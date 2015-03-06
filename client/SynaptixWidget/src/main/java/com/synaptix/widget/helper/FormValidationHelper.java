package com.synaptix.widget.helper;

import java.text.ParseException;

import javax.swing.JFormattedTextField;

import com.jgoodies.validation.util.ValidationUtils;

/**
 * An helper to validate form. Helps with lost focus validation
 * 
 * @author Nicolas P
 * 
 */
public class FormValidationHelper {

	/**
	 * 
	 * @param formattedTextField
	 * @return true if valid, false otherwise
	 */
	public static boolean validateFormattedTextField(JFormattedTextField formattedTextField) {
		return validateFormattedTextField(formattedTextField, null);
	}

	/**
	 * 
	 * @param formattedTextField
	 * @param regex
	 *            Validating regex
	 * @return true if valid, false otherwise
	 */
	public static boolean validateFormattedTextField(JFormattedTextField formattedTextField, String regex) {
		boolean invalid = false;
		if (ValidationUtils.isBlank(formattedTextField.getText())) {
			invalid = true;
		} else {
			if ((formattedTextField.getText() != null) && (!formattedTextField.getText().isEmpty())) {
				try {
					formattedTextField.commitEdit();
				} catch (ParseException e) {
					invalid = true;
				}
				if ((!invalid) && (regex != null) && (!formattedTextField.getText().matches(regex))) {
					invalid = true;
				}
			}
		}
		return !invalid;
	}
}
