package com.synaptix.widget.joda.view.swing;

import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.swing.JSyTable;
import com.synaptix.widget.joda.text.LocalDateFormat;
import com.synaptix.widget.joda.text.LocalDateTimeFormat;
import com.synaptix.widget.joda.text.LocalTimeFormat;
import com.synaptix.widget.joda.view.swing.filter.LocalDateFilterColumn;
import com.synaptix.widget.joda.view.swing.filter.LocalDateTimeFilterColumn;
import com.synaptix.widget.joda.view.swing.filter.LocalTimeFilterColumn;
import com.synaptix.widget.joda.view.swing.renderer.DurationCellRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalDateExcelColumnRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalDateTableCellRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalDateTimeExcelColumnRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalDateTimeTableCellRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalTimeExcelColumnRenderer;
import com.synaptix.widget.joda.view.swing.renderer.LocalTimeTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public final class JodaSwingUtils {

	private static final Log LOG = LogFactory.getLog(JodaSwingUtils.class);

	private JodaSwingUtils() {
	}

	/**
	 * Decorate formatted with LocalDate
	 * 
	 * @param formattedTextField
	 * @return
	 */
	public static JComponent decorateLocalDate(JFormattedTextField formattedTextField) {
		String pattern = StaticWidgetHelper.getSynaptixDateConstantsBundle().editorDateFormat();
		return decorateLocalDateWithPattern(formattedTextField, pattern);
	}

	/**
	 * Use with caution: you might want to use .with"field"(1) to make sure the date is right.<br/>
	 * 
	 * Example: for MM/yyyy, use .withDayOfMonth(1)
	 * 
	 * @param formattedTextField
	 * @param pattern
	 * @return
	 */
	public static JComponent decorateLocalDateWithPattern(JFormattedTextField formattedTextField, String pattern) {
		formattedTextField.setFormatterFactory(new DefaultFormatterFactory(new InternationalFormatter(new ExtendedLocalDateFormat(pattern))));
		installListeners(formattedTextField, pattern);
		return LocalDateTextFieldDecorator.decorate(formattedTextField);
	}

	/**
	 * Decorate formatted with LocalDateTime
	 * 
	 * @param formattedTextField
	 * @return
	 */
	public static JComponent decorateLocalDateTime(JFormattedTextField formattedTextField) {
		String pattern = StaticWidgetHelper.getSynaptixDateConstantsBundle().editorDateTimeFormat();
		formattedTextField.setFormatterFactory(new DefaultFormatterFactory(new InternationalFormatter(new ExtendedLocalDateTimeFormat(pattern))));
		installListeners(formattedTextField, pattern);
		return LocalDateTimeTextFieldDecorator.decorate(formattedTextField);
	}

	/**
	 * Decorate formatted with LocalTime
	 * 
	 * @param formattedTextField
	 * @return
	 */
	public static JComponent decorateLocalTime(JFormattedTextField formattedTextField) {
		String pattern = StaticWidgetHelper.getSynaptixDateConstantsBundle().editorTimeFormat();
		formattedTextField.setFormatterFactory(new DefaultFormatterFactory(new InternationalFormatter(new ExtendedLocalTimeFormat(pattern))));
		installListeners(formattedTextField, pattern);
		return LocalTimeTextFieldDecorator.decorate(formattedTextField);
	}

	/**
	 * Set default renderer for table, LocalDate, LocalDateTime and LocalTime
	 * 
	 * @param table
	 */
	public static JTable decorateTable(JTable table) {
		table.setDefaultRenderer(LocalDate.class, new LocalDateTableCellRenderer());
		table.setDefaultRenderer(LocalDateTime.class, new LocalDateTimeTableCellRenderer());
		table.setDefaultRenderer(LocalTime.class, new LocalTimeTableCellRenderer());
		table.setDefaultRenderer(Duration.class, new DurationCellRenderer());
		return table;
	}

	/**
	 * Set default filter, comparator for table, LocalDate, LocalDateTime and LocalTime
	 * 
	 * @param table
	 * @return
	 */
	public static JSyTable decorateTable(JSyTable table) {
		decorateTable((JTable) table);
		table.setDefaultFilterColumn(LocalDate.class, new LocalDateFilterColumn());
		table.setDefaultComparatorColumn(LocalDate.class, new LocalComparator());
		table.setDefaultExcelColumnRenderer(LocalDate.class, new LocalDateExcelColumnRenderer());
		table.setDefaultFilterColumn(LocalDateTime.class, new LocalDateTimeFilterColumn());
		table.setDefaultComparatorColumn(LocalDateTime.class, new LocalComparator());
		table.setDefaultExcelColumnRenderer(LocalDateTime.class, new LocalDateTimeExcelColumnRenderer());
		table.setDefaultFilterColumn(LocalTime.class, new LocalTimeFilterColumn());
		table.setDefaultComparatorColumn(LocalTime.class, new LocalComparator());
		table.setDefaultExcelColumnRenderer(LocalTime.class, new LocalTimeExcelColumnRenderer());
		// renderers for Duration?
		return table;
	}

	/**
	 * Install focus & document listeners
	 * 
	 * @param formattedTextField
	 * @param pattern
	 */
	private static void installListeners(JFormattedTextField formattedTextField, String pattern) {
		JodaFormattedTextFieldListener listener = new JodaFormattedTextFieldListener(formattedTextField, pattern);
		formattedTextField.addFocusListener(listener);
		formattedTextField.addMouseListener(listener);
		formattedTextField.addKeyListener(listener);
		formattedTextField.getDocument().addDocumentListener(listener);

		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("control pressed TAB")));
		formattedTextField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("control shift pressed TAB")));
		formattedTextField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
	}

	private static final class JodaFormattedTextFieldListener extends MouseAdapter implements DocumentListener, FocusListener, KeyListener {

		private JFormattedTextField timeField;
		private String pattern;

		public JodaFormattedTextFieldListener(JFormattedTextField timeField, String pattern) {
			super();

			this.timeField = timeField;
			this.pattern = pattern;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (e.getLength() == 1) {
				try {
					if ((timeField.getText() != null) && (!timeField.getText().isEmpty()) && (timeField.getText().matches("^(-|\\+).*"))) {
						if (timeField.getCaretPosition() == 0) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									try {
										timeField.setText(timeField.getText(0, timeField.getCaretPosition()));
									} catch (BadLocationException e2) {
										LOG.error("Bad Location in " + this.getClass().getName(), e2);
									}
								}
							});
						}
					} else if (!Character.isLetter(timeField.getText().charAt(timeField.getCaretPosition()))) {
						if (timeField.getCaretPosition() < pattern.length() - 1) {
							Character a = pattern.charAt(timeField.getCaretPosition() + 1);
							if (!Character.isLetterOrDigit(a)) {
								final String text = e.getDocument().getText(0, e.getDocument().getLength());
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										Pattern pattern = Pattern.compile(String.format("^.{%d}([^\\w]+?)\\w+?([^\\w]+|$)", timeField.getSelectionEnd()));
										Matcher matcher = pattern.matcher(text);
										if (matcher.find()) {
											timeField.setSelectionStart(matcher.start(1) + 1);
											timeField.setSelectionEnd(matcher.start(2));
										} else {
											pattern = Pattern.compile("([a-zA-Z0-9]+)");
											matcher = pattern.matcher(text);
											if (matcher.find(timeField.getSelectionStart())) {
												timeField.setSelectionStart(matcher.start());
												timeField.setSelectionEnd(matcher.end());
											}
										}
									}
								});
							}
						}
					}
				} catch (BadLocationException e1) {
					LOG.error("Bad Location in " + this.getClass().getName(), e1);
				}
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource().equals(timeField)) {
				if (!displayDefaultPattern()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (timeField.getSelectionStart() == timeField.getSelectionEnd()) {
								if (StringUtils.isNotBlank(timeField.getText())) {
									Pattern pattern = Pattern.compile("[a-zA-Z0-9]{2,}");
									Matcher matcher = pattern.matcher(timeField.getText());
									if (matcher.find()) {
										timeField.setSelectionStart(matcher.start());
										timeField.setSelectionEnd(matcher.end());
									}
								}
							}
						}
					});
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			DefaultFormatter formatter = (DefaultFormatter) timeField.getFormatter();
			if (formatter.getAllowsInvalid()) {
				if (ValidationUtils.isEmpty(timeField.getText())) {
					timeField.setValue(null);
				}
			}
		}

		@Override
		public void mousePressed(final MouseEvent ev) {

		}

		@Override
		public void mouseClicked(final MouseEvent ev) {
			if ((ev.getComponent() == timeField) && (StringUtils.isNotEmpty(timeField.getText()))) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						int i = 0;
						int selectionStart = timeField.getSelectionStart();
						if (ev.getClickCount() % 3 == 2) {
							timeField.setCaretPosition(selectionStart);
						} else if (ev.getClickCount() % 3 == 1) {
							int m = timeField.getText().length() + 1;
							while (i < m) {
								if (i >= selectionStart) {

									Pattern pattern = Pattern.compile("(\\w+)");
									Matcher matcher = pattern.matcher(timeField.getText());
									while (matcher.find()) {
										if ((matcher.start(1) <= i) && (matcher.end(1) >= i)) {
											timeField.setSelectionStart(matcher.start(1));
											timeField.setSelectionEnd(matcher.end(1));
											break;
										}
									}
									i = m + 1;
								}

								i++;
							}
							ev.consume();
						}
					}
				});
			}

			if ((ev.getComponent() == timeField) && (ev.getButton() == MouseEvent.BUTTON1)) {
				displayDefaultPattern();
			}
		}

		/**
		 * true if default pattern displayed, false otherwise
		 * 
		 * @return
		 */
		private boolean displayDefaultPattern() {
			if ((timeField.isEnabled()) && (timeField.isEditable())) {
				if ((timeField.getText() == null) || (timeField.getText().isEmpty())) {
					timeField.setText(pattern);
					Pattern patt = Pattern.compile("\\w+");
					Matcher matcher = patt.matcher(pattern);
					if (matcher.find()) {
						timeField.setSelectionStart(matcher.start());
						timeField.setSelectionEnd(matcher.end());
					} else {
						timeField.setSelectionStart(0);
						timeField.setSelectionEnd(2);
					}
					return true;
				}
			}
			return false;
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			int start = timeField.getSelectionStart();

			if ((e.getKeyCode() == KeyEvent.VK_SPACE) && ((!pattern.contains(" ")) || (timeField.getText().contains(" ")))) {
				e.consume();
				final String text = timeField.getText();
				Pattern patt = Pattern.compile(String.format(".{%s}(.*?)(\\s|$)", start));
				final Matcher matt = patt.matcher(text);
				if (matt.find()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							guessDate(text, matt.start(1), matt.end(1), true);
							gotoNextField(0);
						}
					});
				}
			} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
				e.consume();
				gotoNextField(e.getModifiers());
				// } else if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE) && ((start > 0) || (start != end))) {
				// if ((start != 0) || (end != timeField.getText().length())) {
				// System.out.println(timeField.getCaretPosition());
				// StringBuilder sb = new StringBuilder();
				// String pat = null;
				// if (start == end) {
				// start = start - 1;
				// }
				// pat = pattern.substring(start, end);
				// sb.append(timeField.getText().substring(0, start)).append(pat).append(timeField.getText().substring(end));
				// timeField.setText(sb.toString());
				// timeField.setSelectionStart(start);
				// timeField.setSelectionEnd(start);
				// e.consume();
				// }
			}
		}

		private void gotoNextField(int modifiers) {
			final String text = timeField.getText();

			final int start = timeField.getSelectionStart();
			final int end = timeField.getSelectionEnd();
			if (modifiers == KeyEvent.SHIFT_MASK) {
				if ((start == 0) || (text == null) || (text.isEmpty())) {
					timeField.getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentBefore(timeField.getFocusCycleRootAncestor(), timeField).requestFocus();
				} else {
					Pattern patt = Pattern.compile(String.format("([^\\w]+)\\w+?([^\\w]+)\\w*?.{%d}$", StringUtils.length(text) - start));
					Matcher matcher = patt.matcher(text);
					if (matcher.find()) {
						timeField.setSelectionStart(matcher.start(1) + 1);
						timeField.setSelectionEnd(matcher.start(2));
					} else {
						patt = Pattern.compile("([a-zA-Z0-9]+)");
						matcher = patt.matcher(text);
						if (matcher.find()) {
							timeField.setSelectionStart(matcher.start());
							timeField.setSelectionEnd(matcher.end());
						}
					}
				}
			} else if (modifiers == 0) {
				guessDate(timeField.getText(), start, end, false);
				if (((start == 0) && (pattern.equals(text))) || (text == null) || (text.isEmpty()) || (end == text.length())) {
					timeField.getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentAfter(timeField.getFocusCycleRootAncestor(), timeField).requestFocus();
				} else {
					String regex = null;
					if (end == 0) {
						regex = "^(\\w+).*?$";
					} else {
						regex = String.format(".{%d,}?(\\w+).*?$", end);
					}
					Pattern patt = Pattern.compile(regex);
					Matcher matcher = patt.matcher(text);
					if (matcher.find()) {
						timeField.setSelectionStart(matcher.start(1));
						timeField.setSelectionEnd(matcher.end(1));
					} else {
						// improve ...
						timeField.setSelectionStart(0);
						timeField.setSelectionEnd(2);
					}
				}
			}
		}

		private void guessDate(String text, int start, int end, boolean midnight) {
			try {
				String patt = text.substring(start, end);
				SimpleDateFormat sdf = new SimpleDateFormat(patt);
				Calendar cal = Calendar.getInstance();
				if (midnight) {
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
				}
				String s = sdf.format(cal.getTime());
				StringBuilder sb = new StringBuilder();
				sb.append(text.substring(0, start));
				sb.append(s);
				sb.append(text.substring(end));
				timeField.setText(sb.toString());
				timeField.setSelectionStart(start);
				timeField.setSelectionEnd(end);
			} catch (Exception ex) {
				// nothing
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	private static final class ExtendedLocalDateFormat extends LocalDateFormat {

		private static final long serialVersionUID = 1568797442736353308L;

		private String pattern;

		public ExtendedLocalDateFormat(String pattern) {
			super(pattern);

			this.pattern = pattern;
		}

		@Override
		public Object parseObject(String source) throws ParseException {
			if ((source.length() >= 1) && ((source.charAt(0) == '+') || ((source.charAt(0) == '-')))) {
				return transformText(source, pattern).toLocalDate();
			}
			if (StringUtils.isBlank(source)) {
				return null;
			}
			return super.parseObject(source);
		}
	}

	private static final class ExtendedLocalTimeFormat extends LocalTimeFormat {

		private static final long serialVersionUID = 1568797442736353308L;

		private String pattern;

		public ExtendedLocalTimeFormat(String pattern) {
			super(pattern);

			this.pattern = pattern;
		}

		@Override
		public Object parseObject(String source) throws ParseException {
			if ((source.length() >= 1) && ((source.charAt(0) == '+') || ((source.charAt(0) == '-')))) {
				return transformText(source, pattern).toLocalTime();
			}
			if (StringUtils.isBlank(source)) {
				return null;
			}
			return super.parseObject(source);
		}
	}

	private static final class ExtendedLocalDateTimeFormat extends LocalDateTimeFormat {

		private static final long serialVersionUID = 1568797442736353308L;

		private String pattern;

		public ExtendedLocalDateTimeFormat(String pattern) {
			super(pattern);

			this.pattern = pattern;
		}

		@Override
		public Object parseObject(String source) throws ParseException {
			if ((source.length() >= 1) && ((source.charAt(0) == '+') || ((source.charAt(0) == '-')))) {
				return transformText(source, pattern);
			}
			if (StringUtils.isBlank(source)) {
				return null;
			}
			return super.parseObject(source);
		}
	}

	private static LocalDateTime transformText(String source, String pattern) {
		// valid syntax (example) :
		// -2d-4M+5y+10H-2m+25s
		String regex = "(-|\\+)([0-9]+)([a-zA-Z]?)";
		Pattern patt = Pattern.compile(regex);
		Matcher mat = patt.matcher(source);
		LocalDateTime dateTime = new LocalDateTime();
		while (mat.find()) {
			String sign = mat.group(1);
			Integer nb = Integer.parseInt(mat.group(2));
			char unit;
			if (mat.group(3).isEmpty()) {
				unit = 'd';
			} else {
				unit = mat.group(3).charAt(0);
			}
			if ("-".equals(sign)) {
				nb = -nb;
			}
			switch (unit) {
			case 'd':
				dateTime = dateTime.plusDays(nb);
				break;
			case 'M':
				dateTime = dateTime.plusMonths(nb);
				break;
			case 'y':
				dateTime = dateTime.plusYears(nb);
				break;
			case 'H':
			case 'h':
				dateTime = dateTime.plusHours(nb);
				break;
			case 'm':
				dateTime = dateTime.plusMinutes(nb);
				break;
			case 's':
				dateTime = dateTime.plusSeconds(nb);
				break;
			case 'w':
				dateTime = dateTime.plusWeeks(nb);
				break;
			default:
			}
		}
		return dateTime;
	}
}
