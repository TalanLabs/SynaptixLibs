import java.awt.BorderLayout;

import javax.swing.JFrame;

import net.sf.jeppers.grid.JGrid;
import net.sf.jeppers.grid.JScrollGrid;

/**
 * @author Cameron Zemek
 */
public class MainComponent extends JFrame {
	/** Creates new TestComponent */
	public MainComponent() {
		super("Grid Test");

		// Quit this app when the main window closes
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JGrid grid = new JGrid(10, 10);
		grid.setShowGrid(false);
		JScrollGrid scrollGrid = new JScrollGrid(grid);

		getContentPane().add(scrollGrid, BorderLayout.CENTER);
		pack();
		setSize(640, 480);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		MainComponent test = new MainComponent();
		test.show();
	}
}
