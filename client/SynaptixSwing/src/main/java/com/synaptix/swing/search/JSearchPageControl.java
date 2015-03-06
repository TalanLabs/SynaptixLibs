package com.synaptix.swing.search;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JSearchPageControl extends JPanel implements ISearchPageControl {

	private static final long serialVersionUID = 175424171270253289L;

	private Action lastPageAction;

	private Action nextPageAction;

	private Action previousPageAction;

	private Action firstPageAction;

	private JLabel label;

	private int currentPage;

	private int maxPage;

	public JSearchPageControl() {
		super(new BorderLayout());

		initActions();
		initComponent();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		lastPageAction = new LastPageAction();
		lastPageAction.setEnabled(false);

		nextPageAction = new NextPageAction();
		nextPageAction.setEnabled(false);

		previousPageAction = new PreviousPageAction();
		previousPageAction.setEnabled(false);

		firstPageAction = new FirstPageAction();
		firstPageAction.setEnabled(false);
	}

	private void initComponent() {
		label = new JLabel("", JLabel.CENTER);
		label.setFont(new Font("Arial",Font.BOLD,12));
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout(
				"FILL:PREF:NONE,FILL:PREF:NONE,FILL:MAX(50DLU;PREF):NONE,FILL:PREF:NONE,FILL:PREF:NONE",
				"CENTER:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JButton(firstPageAction), cc.xy(1, 1));
		builder.add(new JButton(previousPageAction), cc.xy(2, 1));
		builder.add(label, cc.xy(3, 1));
		builder.add(new JButton(nextPageAction), cc.xy(4, 1));
		builder.add(new JButton(lastPageAction), cc.xy(5, 1));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(builder.getPanel(),BorderLayout.EAST);
		return panel;
	}

	public void setControl(int currentPage, int maxPage) {
		this.maxPage = maxPage;
		this.currentPage = currentPage;

		updateLabel();
	}

	public void clear() {
		label.setText("");

		firstPageAction.setEnabled(false);
		previousPageAction.setEnabled(false);
		nextPageAction.setEnabled(false);
		lastPageAction.setEnabled(false);
	}

	private void updateLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(currentPage);
		sb.append(" / ");
		sb.append(maxPage);
		label.setText(sb.toString());

		firstPageAction.setEnabled(currentPage > 1);
		previousPageAction.setEnabled(currentPage > 1);

		nextPageAction.setEnabled(currentPage < maxPage);
		lastPageAction.setEnabled(currentPage < maxPage);
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	private void firePageActionPerformed(String action) {
		ActionListener[] listeners = listenerList
				.getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this, 0, action);
		for (ActionListener listener : listeners) {
			listener.actionPerformed(e);
		}
	}

	public void lastPage() {
		firePageActionPerformed("last");
	}

	public void firstPage() {
		firePageActionPerformed("first");
	}

	public void previousPage() {
		firePageActionPerformed("previous");
	}

	public void nextPage() {
		firePageActionPerformed("next");
	}

	private final class LastPageAction extends AbstractAction {

		private static final long serialVersionUID = 2701092537558614207L;

		public LastPageAction() {
			super(">>");
		}

		public void actionPerformed(ActionEvent e) {
			lastPage();
		}
	}

	private final class NextPageAction extends AbstractAction {

		private static final long serialVersionUID = 2701092537558614207L;

		public NextPageAction() {
			super(">");
		}

		public void actionPerformed(ActionEvent e) {
			nextPage();
		}
	}

	private final class PreviousPageAction extends AbstractAction {

		private static final long serialVersionUID = 2701092537558614207L;

		public PreviousPageAction() {
			super("<");
		}

		public void actionPerformed(ActionEvent e) {
			previousPage();
		}
	}

	private final class FirstPageAction extends AbstractAction {

		private static final long serialVersionUID = 2701092537558614207L;

		public FirstPageAction() {
			super("<<");
		}

		public void actionPerformed(ActionEvent e) {
			firstPage();
		}
	}
}
