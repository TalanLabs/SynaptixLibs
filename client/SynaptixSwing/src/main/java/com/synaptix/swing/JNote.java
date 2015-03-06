package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JNote extends JPanel {

	private static final long serialVersionUID = 7925311766256791287L;

	public enum Type {
		note, tip, warning, important
	};

	private static final Icon noteIcon = new ImageIcon(JNote.class
			.getResource("note.png"));

	private static final Icon tipIcon = new ImageIcon(JNote.class
			.getResource("tip.png"));

	private static final Icon warningIcon = new ImageIcon(JNote.class
			.getResource("warning.png"));

	private static final Icon importantIcon = new ImageIcon(JNote.class
			.getResource("important.png"));

	private Type type;

	private JLabel iconLabel;

	private JTextArea infoArea;

	public JNote() {
		this(Type.note, "");
	}

	public JNote(Type type, String text) {
		super(new BorderLayout());

		this.type = type;

		initActions();
		initComponents();

		infoArea.setText(text);

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		iconLabel = new JLabel();
		infoArea = new JTextArea();
	}

	private void initComponents() {
		createComponents();

		infoArea.setEditable(false);
		infoArea.setOpaque(false);
		infoArea.setBackground(Color.WHITE);
		infoArea.setWrapStyleWord(true);
		infoArea.setLineWrap(true);

		updateIconType();
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		JComponent e = buildEditorPane();
		e.setOpaque(false);
		panel.add(e, BorderLayout.CENTER);
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		return panel;
	}

	private JComponent buildEditorPane() {
		FormLayout layout = new FormLayout(
				"CENTER:DEFAULT:NONE,FILL:4DLU:NONE,FILL:200DLU:GROW(1.0)",
				"CENTER:MIN(48PX;PREF):GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(iconLabel, cc.xy(1, 1));
		builder.add(infoArea, cc.xy(3, 1, CellConstraints.FILL,
				CellConstraints.CENTER));
		return builder.getPanel();
	}

	public void setType(Type type) {
		Type oldValue = getType();
		this.type = type;
		updateIconType();
		firePropertyChange("type", oldValue, type);
	}

	private void updateIconType() {
		switch (type) {
		case note:
			iconLabel.setIcon(noteIcon);
			break;
		case warning:
			iconLabel.setIcon(warningIcon);
			break;
		case tip:
			iconLabel.setIcon(tipIcon);
			break;
		case important:
			iconLabel.setIcon(importantIcon);
			break;
		}
	}

	public Type getType() {
		return type;
	}

	public void setText(String text) {
		String oldValue = getText();
		infoArea.setText(text);
		firePropertyChange("text", oldValue, text);
	}

	public String getText() {
		return infoArea.getText();
	}
}
