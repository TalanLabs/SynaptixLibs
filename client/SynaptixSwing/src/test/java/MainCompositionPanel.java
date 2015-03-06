import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JCompositionPanel;

public class MainCompositionPanel extends JCompositionPanel {

	private static final long serialVersionUID = 7388584552867300961L;

	private JLabel hautLabel;

	private JLabel basLabel;

	private JLabel flecheLabel;

	public MainCompositionPanel() {
		super();

		hautLabel = new JLabel("4567", JLabel.LEFT);
		hautLabel.setOpaque(false);
		hautLabel.setFont(new Font("arial", Font.BOLD, 15));

		basLabel = new JLabel("Toto est là", JLabel.LEFT);
		basLabel.setOpaque(false);
		basLabel.setFont(new Font("arial", Font.ITALIC, 13));

		flecheLabel = new JLabel(">", JLabel.RIGHT);
		flecheLabel.setOpaque(false);
		flecheLabel.setVisible(false);

		this.add(buildContents(), new Integer(1));
		this.add(flecheLabel, new Integer(2));
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:PREF:GROW(1.0)", //$NON-NLS-1$
				"FILL:25PX,FILL:25PX"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(hautLabel, cc.xy(1, 1));
		builder.add(basLabel, cc.xy(1, 2));
		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new MainCompositionPanel(), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}