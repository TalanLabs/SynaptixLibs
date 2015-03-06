import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;
import org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel;

import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.ToolBarFactory;

public class MainToolBarFactory {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
					UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE, new WidestComboPopupPrototype());
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
					Manager.installLookAndFeelWindows();
				}
				JDialogModel.setActiveSave(true);
				JDialogModel.setMultiScreen(true);

				final JFrame frame = new JFrame("Tests");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());

				JComponent c = ToolBarFactory.buildToolBar(new JButton("Gaby"), new JToggleButton("Sandra"), new JToggleButton("Toto"));

				frame.getContentPane().add(c, BorderLayout.NORTH);

				frame.pack();
				// frame.setSize(300, 400);
				frame.setVisible(true);
			}
		});
	}
}
