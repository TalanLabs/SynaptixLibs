package com.synaptix.swing.other;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.other.pong.Pong;
import com.synaptix.swing.utils.Utils;
import com.synaptix.swing.widget.AbstractCloseAction;

public class SynaptixDialog extends JPanel {

	private static final long serialVersionUID = 6420692586840157890L;

	public final static int CLOSE_OPTION = 0;

	private final static String TEXT_TITLE = SwingMessages.getString("SynaptixDialog.0"); //$NON-NLS-1$

	private final static String TEXT_SUBTITLE = SwingMessages.getString("SynaptixDialog.1"); //$NON-NLS-1$

	private JDialogModel dialog;

	private JEditorPane rapportPane;

	private static final ImageIcon ICON_SYNAPTIX;

	private static final Image ICON_SYNAPTIX_ONLY;

	private Action closeAction;

	private int returnValue;

	static {
		ImageIcon imageIcon = null;
		try {
			imageIcon = new ImageIcon(ImageIO.read(SynaptixDialog.class.getResource("/images/iconSynaptix.png"))); //$NON-NLS-1$
		} catch (IOException e) {
		}
		int width = imageIcon.getIconWidth() * 100 / imageIcon.getIconHeight();
		ICON_SYNAPTIX = new ImageIcon(imageIcon.getImage().getScaledInstance(width, 100, Image.SCALE_SMOOTH));

		try {
			imageIcon = new ImageIcon(ImageIO.read(SynaptixDialog.class.getResource("/images/iconSynaptixOnly.png"))); //$NON-NLS-1$
		} catch (IOException e) {
		}
		width = imageIcon.getIconWidth() * 40 / imageIcon.getIconHeight();
		ICON_SYNAPTIX_ONLY = imageIcon.getImage().getScaledInstance(width, 40, Image.SCALE_SMOOTH);
	}

	public SynaptixDialog() {
		super(new BorderLayout());

		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		String text = Utils.getResourceAsString("/synaptix.html"); //$NON-NLS-1$

		rapportPane = new JEditorPane("text/html", text); //$NON-NLS-1$
		rapportPane.setEditable(false);
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("fill:300dlu:grow", //$NON-NLS-1$
				"100px, 3dlu,p,3dlu,fill:150dlu:grow"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.add(new JLabel(ICON_SYNAPTIX), cc.xy(1, 1));
		builder.addSeparator(SwingMessages.getString("SynaptixDialog.8"), cc.xy(1, 3)); //$NON-NLS-1$
		builder.add(new JScrollPane(rapportPane), cc.xy(1, 5));

		return builder.getPanel();
	}

	public int showDialog(Component parent) {
		returnValue = CLOSE_OPTION;

		closeAction = new CloseAction();

		dialog = new JDialogModel(parent, TEXT_TITLE, TEXT_SUBTITLE, this, new Action[] { closeAction }, closeAction);
		dialog.getTitleComponent().setImageTitle(ICON_SYNAPTIX_ONLY);
		dialog.getTitleComponent().setColorBackgroundLow(new Color(2, 128, 64));
		dialog.getTitleComponent().setColorBackgroundHigh(new Color(124, 223, 173, 255));

		dialog.setResizable(true);

		rapportPane.setCaretPosition(0);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public void actionPerformed(ActionEvent e) {
			int m = e.getModifiers();
			if (((m & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) && ((m & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) && ((m & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)) {
				Pong pong = new Pong();
				pong.setVisible(true);
				pong.dispose();
			}

			returnValue = CLOSE_OPTION;
			dialog.closeDialog();
		}
	}
}
