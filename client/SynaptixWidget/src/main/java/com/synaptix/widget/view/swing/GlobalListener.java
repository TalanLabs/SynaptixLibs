package com.synaptix.widget.view.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsLocaleSession;
import com.synaptix.widget.guice.SwingConstantsBundleManager;

public class GlobalListener {

	@Inject(optional = true)
	@SwingConstantsBundleManager
	private ConstantsBundleManager constantsBundleManager;

	@Inject(optional = true)
	@SwingConstantsBundleManager
	private DefaultConstantsLocaleSession defaultConstantsLocaleSession;

	public GlobalListener() {
		initialize();
	}

	private void initialize() {
		long eventMask = AWTEvent.MOUSE_EVENT_MASK;
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent e) {
				if (e instanceof MouseEvent) {
					MouseEvent me = (MouseEvent) e;
					if ((me.getID() == MouseEvent.MOUSE_CLICKED) && (me.getButton() == MouseEvent.BUTTON2)) {
						Component component = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
						dispatchMouseClicOnComponent(component, me.getX(), me.getY());
					}
				}
			}
		}, eventMask);
	}

	private void dispatchMouseClicOnComponent(Object object, int x, int y) {
		if ((constantsBundleManager == null) || (defaultConstantsLocaleSession == null)) {
			return;
		}
		try {
			String text = null;

			try {
				Method method = object.getClass().getMethod("getText", new Class[0]);
				if (method != null) {
					text = (String) method.invoke(object, new Object[0]);
				}
			} catch (Exception e) {
			}
			if (text == null) {
				try {
					Method method = object.getClass().getMethod("getTitle", new Class[0]);
					if (method != null) {
						text = (String) method.invoke(object, new Object[0]);
					}
				} catch (Exception e) {
				}
			}
			if (text == null) {
				try {
					Method method = object.getClass().getMethod("getHeaderValue", new Class[0]);
					if (method != null) {
						text = (String) method.invoke(object, new Object[0]);
					}
				} catch (Exception e) {
				}
			}
			if ((text == null) && (object instanceof JComponent)) {
				text = (String) ((JComponent) object).getClientProperty(JComponent.TOOL_TIP_TEXT_KEY);
			}
			if ((text == null) && (object instanceof JComboBox)) {
				try {
					JComboBox comboBox = (JComboBox) object;
					dispatchMouseClicOnComponent(comboBox.getRenderer().getListCellRendererComponent(new JList(comboBox.getSelectedObjects()), comboBox.getSelectedItem(), -1, true, false), x, y);
					return;
				} catch (Exception e) {
				}
			}
			if ((text == null) && (object instanceof JList)) {
				try {
					JList list = (JList) object;
					int index = list.locationToIndex(new Point(x, y));
					dispatchMouseClicOnComponent(list.getModel().getElementAt(index), x, y);
					return;
				} catch (Exception e) {
				}
			}
			if (StringUtils.isNotBlank(text)) {
				String pureText = text.replaceAll("<[^>]+>", "").replaceAll("(\\s)?:(\\s)?$", ""); // remove html
				for (String bundle : constantsBundleManager.getAllBundleNames()) {
					DefaultConstantsBundleManager manager = (DefaultConstantsBundleManager) constantsBundleManager;
					Properties prop = manager.getProperties(defaultConstantsLocaleSession.getLocale(), bundle);
					for (Entry<Object, Object> entry : prop.entrySet()) {
						if (entry.getValue() instanceof String) {
							String bundleValue = (String) entry.getValue();
							if (bundleValue == text) {
								System.out.println("bundle : " + bundle + " - key : " + entry.getKey() + " - default value : " + entry.getValue());
							} else /* if (bundleValue.matches(".*\\{.+\\}.*")) */{
								try {
									bundleValue = getBundleValueWithJokers(bundleValue);

									if (pureText.matches(bundleValue)) {
										System.out.println("matching bundle : " + bundle + " - key : " + entry.getKey() + " - default value : " + entry.getValue());
									}
								} catch (Exception e1) {
								}
							}
						} else {
							System.out.println(entry.getValue());
						}
					}
				}
			}
		} catch (Exception e1) {
		}
	}

	/**
	 * Transforms {x} into .*, {x,choice,0#a|1#b|1<c} into (a|b|c), {x,number,00} into [0-9]{2}
	 *
	 * @param bundleValue
	 * @return
	 */
	private String getBundleValueWithJokers(String bundleValue) {
		bundleValue = bundleValue.replaceAll("\\{[0-9]+\\}", ".+").replaceAll("<[^>]+>", ""); // remove html

		String regex = "\\{[0-9]+,(choice|number),([0-9]+(?:#|<)?([^\\\\|\\}]*)\\|?)+?(?:[^\\}]*)\\}";

		String bb = bundleValue;

		Pattern patt = Pattern.compile(regex);
		Matcher matt = patt.matcher(bb);
		StringBuilder sb = new StringBuilder();
		String last2Group = null;
		while (matt.find()) {
			if (StringUtils.isNotEmpty(matt.group(2))) {
				if ((last2Group != null) && (!last2Group.equals(matt.group(2)))) {
					sb = new StringBuilder();
				}
				last2Group = matt.group(2);
				if (sb.length() > 0) {
					sb.append("|");
				} else {
					sb.append("(");
				}
				if ("number".equals(matt.group(1))) {
					sb.append("[0-9]{").append(matt.group(2).length()).append("}");
				} else if (StringUtils.isNotEmpty(matt.group(3))) {
					sb.append(matt.group(3));
				}
			}
			bb = bundleValue.substring(0, matt.start(2)) + bundleValue.substring(matt.end(2));
			bundleValue = matt.replaceFirst(sb.toString() + ")");
			matt = patt.matcher(bundleValue);
		}
		return bundleValue;
	}
}
