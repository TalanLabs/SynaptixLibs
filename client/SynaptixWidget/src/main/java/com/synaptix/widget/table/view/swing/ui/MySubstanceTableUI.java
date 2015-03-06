package com.synaptix.widget.table.view.swing.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceRenderer;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.synaptix.swing.utils.FontAwesomeHelper;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class MySubstanceTableUI extends SubstanceTableUI {

	private static boolean useBooleanRenderer = true;

	public static boolean isUseBooleanRenderer() {
		return useBooleanRenderer;
	}

	public static void setUseBooleanRenderer(boolean useBooleanRenderer) {
		MySubstanceTableUI.useBooleanRenderer = useBooleanRenderer;
	}

	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new MySubstanceTableUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		if (isUseBooleanRenderer()) {
			MyBooleanRenderer g = new MyBooleanRenderer();
			table.setDefaultRenderer(Boolean.class, g);
			table.setDefaultRenderer(boolean.class, g);
		}

		JodaSwingUtils.decorateTable(table);
	}

	@SubstanceRenderer
	private static final class MyBooleanRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3794751543929368341L;

		private static final Font font = FontAwesomeHelper.deriveFont(Font.PLAIN, 12);

		private static final String faCheck = FontAwesomeHelper.getCharacter("fa-check").toString();

		public MyBooleanRenderer() {
			super();

			this.setHorizontalAlignment(JLabel.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setFont(font);
			return c;
		}

		@Override
		protected void setValue(Object value) {
			String text = null;
			if (value instanceof Boolean && ((Boolean) value).booleanValue()) {
				text = faCheck;
			}
			setText(text);
		}
	}
}
