/*
 * Copyright (c) 2003-2007 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.synaptix.swing.JDateHourTextField;
import com.synaptix.swing.JDateTextField;
import com.synaptix.swing.SpinnerBigDecimalModel;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.formatter.BigDecimalFormatter;
import com.synaptix.swing.utils.formatter.DoubleFormatter;
import com.synaptix.swing.utils.formatter.EuroFormatter;
import com.synaptix.swing.utils.formatter.HourFormatter;
import com.synaptix.swing.utils.formatter.IntegerFormatter;
import com.synaptix.swing.utils.formatter.LongFormatter;
import com.synaptix.swing.utils.formatter.RegexFormatter;

public final class SwingComponentFactory extends BasicComponentFactory {

	private SwingComponentFactory() {
	}

	/**
	 * only accept numerics values, others values are not visible,
	 * 
	 * @param replace
	 *            length by # for number or A for alphanumeric, length defines the length of the number
	 * 
	 */
	public static JFormattedTextField createRegularExpression(String mask) {
		JFormattedTextField res = null;
		try {
			res = new JFormattedTextField(new MaskFormatter(mask));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * method for regular expression, usefull for negative number, give it a pattern such as "0|-[1-9]"
	 * 
	 */
	public static JFormattedTextField getRegularExpressionJFTF(String pattern) {
		return new JFormattedTextField(new RegexFormatter(pattern));
	}

	public static JFormattedTextField createDateField(ValueModel valueModel) {
		return createDateField(valueModel, false);
	}

	public static JFormattedTextField createDateField(ValueModel valueModel, boolean enableShortcuts) {
		return createDateField(valueModel, enableShortcuts, true);
	}

	public static JFormattedTextField createDateField(ValueModel valueModel, boolean enableShortcuts, boolean commitsOnValidEdit) {
		JFormattedTextField textField = new JDateTextField(enableShortcuts, commitsOnValidEdit);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createDateField(boolean enableShortcuts, boolean commitsOnValidEdit) {
		JFormattedTextField textField = new JDateTextField(enableShortcuts, commitsOnValidEdit);
		textField.addFocusListener(new SelectAllTextFieldFocusListener());
		return textField;
	}

	public static JFormattedTextField createDateHourField(ValueModel valueModel, boolean enableShortcuts, boolean commitsOnValidEdit) {
		JFormattedTextField textField = new JDateHourTextField(enableShortcuts, commitsOnValidEdit);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createDateHourField(boolean enableShortcuts, boolean commitsOnValidEdit) {
		JFormattedTextField textField = new JDateHourTextField(enableShortcuts, commitsOnValidEdit);
		textField.addFocusListener(new SelectAllTextFieldFocusListener());
		return textField;
	}

	public static JFormattedTextField createHourField(ValueModel valueModel) {
		return createHourField(valueModel, false);
	}

	public static JFormattedTextField createHourField(ValueModel valueModel, boolean defaultNull) {
		DefaultFormatter defaultFormatter = new HourFormatter(defaultNull);
		defaultFormatter.setCommitsOnValidEdit(true);
		JFormattedTextField textField = new JFormattedTextField(defaultFormatter);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createHourField(boolean defaultNull) {
		DefaultFormatter defaultFormatter = new HourFormatter(defaultNull);
		defaultFormatter.setCommitsOnValidEdit(true);
		JFormattedTextField textField = new JFormattedTextField(defaultFormatter);
		textField.addFocusListener(new SelectAllTextFieldFocusListener());
		return textField;
	}

	public static JFormattedTextField createBigDecimalField(ValueModel valueModel) {
		JFormattedTextField.AbstractFormatter defaultFormatter = new BigDecimalFormatter();
		JFormattedTextField textField = new JFormattedTextField(defaultFormatter);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createBigDecimalField() {
		JFormattedTextField.AbstractFormatter defaultFormatter = new BigDecimalFormatter();

		JFormattedTextField textField = new JFormattedTextField(defaultFormatter);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}

	public static JSpinner createBigDecimalSpinner() {
		JSpinner spinner = new JSpinner(new SpinnerBigDecimalModel());
		return spinner;
	}

	public static JSpinner createBigDecimalSpinner(BigDecimal value, BigDecimal minimum, BigDecimal maximum, BigDecimal stepSize) {
		JSpinner spinner = new JSpinner(new SpinnerBigDecimalModel(value, minimum, maximum, stepSize));
		return spinner;
	}

	public static JFormattedTextField createDoubleField() {
		JFormattedTextField.AbstractFormatter defaultFormatter = new DoubleFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, defaultFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}

	public static JFormattedTextField createDoubleField(ValueModel valueModel) {
		JFormattedTextField.AbstractFormatter defaultFormatter = new DoubleFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, defaultFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createEuroField() {
		JFormattedTextField.AbstractFormatter defaultFormatter = new DoubleFormatter();
		JFormattedTextField.AbstractFormatter euroFormatter = new EuroFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, euroFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}

	public static JFormattedTextField createEuroField(ValueModel valueModel) {
		JFormattedTextField.AbstractFormatter defaultFormatter = new DoubleFormatter();
		JFormattedTextField.AbstractFormatter euroFormatter = new EuroFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, euroFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	public static JFormattedTextField createIntegerField() {
		JFormattedTextField.AbstractFormatter defaultFormatter = new IntegerFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, defaultFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}

	public static JFormattedTextField createLongField() {
		JFormattedTextField.AbstractFormatter defaultFormatter = new LongFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, defaultFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		return textField;
	}

	public static JFormattedTextField createIntegerField(ValueModel valueModel) {
		JFormattedTextField.AbstractFormatter defaultFormatter = new IntegerFormatter();
		DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(defaultFormatter, defaultFormatter);

		JFormattedTextField textField = new JFormattedTextField();
		textField.setFormatterFactory(formatterFactory);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		Bindings.bind(textField, valueModel);
		return textField;
	}

	private static JTextArea addEnterActionByTextArea(JTextArea textArea, boolean activeEnterKey, boolean activeCtrlEnterKey) {
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		Action enterAction = new EnterAction(textArea);

		if (activeEnterKey) {
			textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "enterAction");
		}
		if (activeCtrlEnterKey) {
			textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterAction");
		}
		if (activeEnterKey || activeCtrlEnterKey) {
			textArea.getActionMap().put("enterAction", enterAction);
		}

		return textArea;
	}

	public static JTextArea createTextArea(ValueModel valueModel) {
		JTextArea textArea = BasicComponentFactory.createTextArea(valueModel);
		return addEnterActionByTextArea(textArea, false, false);
	}

	public static JTextArea createTextArea(ValueModel valueModel, boolean activeEnterKey, boolean activeCtrlEnterKey) {
		JTextArea textArea = BasicComponentFactory.createTextArea(valueModel);
		return addEnterActionByTextArea(textArea, activeEnterKey, activeCtrlEnterKey);
	}

	public static JTextArea createTextArea(ValueModel valueModel, boolean commitOnFocusLost) {
		JTextArea textArea = BasicComponentFactory.createTextArea(valueModel, commitOnFocusLost);
		return addEnterActionByTextArea(textArea, false, false);
	}

	public static JTextArea createTextArea(ValueModel valueModel, boolean commitOnFocusLost, boolean activeEnterKey, boolean activeCtrlEnterKey) {
		JTextArea textArea = BasicComponentFactory.createTextArea(valueModel, commitOnFocusLost);
		return addEnterActionByTextArea(textArea, activeEnterKey, activeCtrlEnterKey);
	}

	public static JTextArea createTextArea() {
		return createTextArea(false, false);
	}

	public static JTextArea createTextArea(boolean activeEnterKey, boolean activeCtrlEnterKey) {
		return addEnterActionByTextArea(new JTextArea(), activeEnterKey, activeCtrlEnterKey);
	}

	public static <T> JComboBox createGenericComboBox(ValueModel valueModel, List<T> list, GenericObjectToString<T> os, boolean autoComplete) {
		return createGenericComboBox(valueModel, list, os, autoComplete, null);
	}

	public static <T> JComboBox createGenericComboBox(ValueModel valueModel, List<T> list, GenericObjectToString<T> os, boolean autoComplete, T nullObject) {
		List<T> l = list;
		if (nullObject != null) {
			l = createListWithNull(list, nullObject);
		}
		SelectionInList<T> selectionInList = new SelectionInList<T>(l, valueModel);
		JComboBox comboBox = BasicComponentFactory.createComboBox(selectionInList, new TypeGenericCellRenderer<T>(os, nullObject));

		if (autoComplete) {
			// comboBox.setEditable(true);
			AutoCompleteDecorator.decorate(comboBox, new GenericObjectToStringConverter<T>(os, nullObject));
		}

		return comboBox;
	}

	public static <T> JComboBox createGenericComboBox(List<T> list, GenericObjectToString<T> os, boolean autoComplete) {
		return createGenericComboBox(list, os, autoComplete, null);
	}

	public static <T> JComboBox createGenericComboBox(List<T> list, GenericObjectToString<T> os, boolean autoComplete, T nullObject) {
		List<T> l = list;
		if (nullObject != null) {
			l = createListWithNull(list, nullObject);
		}
		JComboBox comboBox = new JComboBox(l.toArray());
		comboBox.setRenderer(new TypeGenericCellRenderer<T>(os, nullObject));

		if (autoComplete) {
			// comboBox.setEditable(true);
			AutoCompleteDecorator.decorate(comboBox, new GenericObjectToStringConverter<T>(os, nullObject));
		}

		return comboBox;
	}

	private static <T> List<T> createListWithNull(List<T> list, T nullObject) {
		List<T> l = new ArrayList<T>();
		l.add(nullObject);
		if (list != null) {
			l.addAll(list);
		}
		return l;
	}

	private static class GenericObjectToStringConverter<T extends Object> extends ObjectToStringConverter {

		private GenericObjectToString<T> os;

		private T nullObject;

		public GenericObjectToStringConverter(GenericObjectToString<T> os, T nullObject) {
			super();
			this.os = os;
			this.nullObject = nullObject;
		}

		@SuppressWarnings("unchecked")
		@Override
		public String getPreferredStringForItem(Object value) {
			if (value != null && !value.equals(nullObject)) {
				T t = (T) value;
				return os.getString(t);
			}
			return SwingMessages.getString("SwingComponentFactory.0"); //$NON-NLS-1$
		}
	}

	private static class TypeGenericCellRenderer<T> extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 7388584552867300961L;

		private GenericObjectToString<T> os;

		private T nullObject;

		public TypeGenericCellRenderer(GenericObjectToString<T> os, T nullObject) {
			super("", JLabel.LEFT); //$NON-NLS-1$

			this.os = os;
			this.nullObject = nullObject;

			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setValue(value);

			return this;
		}

		@SuppressWarnings("unchecked")
		private void setValue(Object value) {
			if (value != null && !value.equals(nullObject)) {
				T t = (T) value;
				this.setText(os.getString(t));
			} else {
				this.setText(SwingMessages.getString("SwingComponentFactory.0")); //$NON-NLS-1$
			}
		}
	}

	private static final class EnterAction extends AbstractAction {

		private static final long serialVersionUID = 2317313251526302220L;

		private JTextArea textArea;

		public EnterAction(JTextArea textArea) {
			super("Enter"); //$NON-NLS-1$

			this.textArea = textArea;
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setText(textArea.getText() + "\n"); //$NON-NLS-1$
		}
	}

	private static final class SelectAllTextFieldFocusListener extends FocusAdapter {

		public void focusGained(FocusEvent e) {
			final JTextField field = (JTextField) e.getComponent();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					field.selectAll();
				}
			});
		}
	}
}
