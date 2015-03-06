import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import path.ExamplePathModel;

import com.synaptix.swing.JChoixPanel;
import com.synaptix.swing.path.JPath;

public class MainChoix extends JChoixPanel<JPath> {

	private static final long serialVersionUID = 8773548445231104852L;

	public MainChoix() {
		super();

		this.addSelectionListener(new JChoixPanel.SelectionListener<JPath>() {
			public void selectionChanged(JPath c) {
				// System.out.println(c != null ? c.getName() : "null");
			}
		});

		this.add(createPathPanel(new String[] { "Paris", "Bruxelles", "Grand", "Bruges", "Ostende" }, new int[] { 0, 1, 4 }));
		this.add(createPathPanel(new String[] { "Paris", "Lille", "Bruxelles", "Grand", "Bruges", "Ostende" }, new int[] { 0, 2, 5 }));
		this.add(createPathPanel(new String[] { "Paris", "Bruxelles", "Anvers", "Rotterdam", "Schiphol", "Amsterdam" }, new int[] { 0, 1, 5 }));
		this.add(createPathPanel(new String[] { "Paris", "Bruxelles", "Liege", "Aix-la-Chapelle", "Cologne" }, new int[] { 0, 1, 4 }));
		this.add(createPathPanel(new String[] { "Paris", "Mons", "Charleroi", "Namur", "Liege" }, new int[] { 0, 4 }));
		this.add(createPathPanel(new String[] { "Paris", "Aéroport\nCharles De Gaulle 2 TGV", "Marne-la-Vallée\nDisneyland© Resort Paris", "Chambéry", "Albertville", "Moutiers", "Aime-la-Plagne",
				"Landy", "Bourg-St-Maurice" }, new int[] { 0, 8 }));
		this.add(createPathPanel(new String[] { "Paris", "Aéroport\nCharles De Gaulle 2 TGV", "Marne-la-Vallée\nDisneyland© Resort Paris", "Lyon Part-Dieu", "Valence", "Marseille" }, new int[] { 0,
				3, 5 }));

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("ici 3");
			}
		});
	}

	private static final JPath createPathPanel(String[] lieux, int[] importantNodes) {
		JPath path = new JPath(new ExamplePathModel(lieux, importantNodes));
		path.setName(Arrays.toString(lieux));
		path.setEnabled(false);
		return path;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JLabel("Choisir un parcours"), BorderLayout.NORTH);
		frame.getContentPane().add(new JScrollPane(new MainChoix()), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}