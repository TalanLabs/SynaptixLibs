import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.synaptix.swing.docking.SyDockableContainerFactory;
import com.vlsolutions.swing.docking.DockGroup;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockingDesktop;

public class MainDock extends JFrame {

	DockGroup documentsGroup = new DockGroup("documents");

	DockGroup helperGroup = new DockGroup("helper");

	MyTextEditor editorPanel = new MyTextEditor();
	MyTree treePanel = new MyTree();
	MyGridOfButtons buttonGrid = new MyGridOfButtons();
	MyJTable tablePanel = new MyJTable();

	DockingDesktop desk = new DockingDesktop();

	public MainDock() {
		super();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().add(desk, BorderLayout.CENTER);

		desk.registerDockable(editorPanel);
		desk.registerDockable(treePanel);
		desk.registerDockable(buttonGrid);
		desk.registerDockable(tablePanel);

		((DockingDesktop) desk.getContext().getDesktopList().get(0)).addDockable(editorPanel);
		((DockingDesktop) desk.getContext().getDesktopList().get(0)).addDockable(treePanel);
		((DockingDesktop) desk.getContext().getDesktopList().get(0)).addDockable(buttonGrid);
		((DockingDesktop) desk.getContext().getDesktopList().get(0)).addDockable(tablePanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DockableContainerFactory.setFactory(new SyDockableContainerFactory());

				MainDock frame = new MainDock();
				frame.setSize(800, 600);
				frame.validate();
				frame.setVisible(true);
			}
		});
	}

	class MyTextEditor extends JPanel implements Dockable {

		DockKey dockKey = new DockKey("text");

		JTextArea textArea = new JTextArea("A Text Area");

		public MyTextEditor() {
			setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane(textArea);
			jsp.setPreferredSize(new Dimension(300, 400));
			add(jsp, BorderLayout.CENTER);

			dockKey.setAutoHideEnabled(false);
			dockKey.setDockGroup(helperGroup);
			dockKey.setFloatEnabled(true);
		}

		public Component getComponent() {
			return this;
		}

		public DockKey getDockKey() {
			return dockKey;
		}
	}

	class MyTree extends JPanel implements Dockable {

		DockKey dockKey = new DockKey("tree");

		JTree tree = new JTree();

		public MyTree() {
			setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane(tree);
			jsp.setPreferredSize(new Dimension(200, 200));
			add(jsp, BorderLayout.CENTER);

			dockKey.setDockGroup(helperGroup);
			dockKey.setFloatEnabled(true);
		}

		public Component getComponent() {
			return this;
		}

		public DockKey getDockKey() {
			return dockKey;
		}
	}

	class MyGridOfButtons extends JPanel implements Dockable {

		DockKey dockKey = new DockKey("grid");

		public MyGridOfButtons() {
			setLayout(new FlowLayout(FlowLayout.TRAILING, 3, 3));
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					add(new JButton("btn" + (i * 3 + j)));
				}
			}
			setPreferredSize(new Dimension(200, 300));

			dockKey.setDockGroup(documentsGroup);
			dockKey.setFloatEnabled(true);
		}

		public Component getComponent() {
			return this;
		}

		public DockKey getDockKey() {
			return dockKey;
		}
	}

	class MyJTable extends JPanel implements Dockable {

		DockKey dockKey = new DockKey("table");

		JTable table = new JTable();

		public MyJTable() {
			setLayout(new BorderLayout());
			table.setModel(new DefaultTableModel(5, 5));
			JScrollPane jsp = new JScrollPane(table);
			jsp.setPreferredSize(new Dimension(200, 200));
			add(jsp, BorderLayout.CENTER);

			dockKey.setDockGroup(documentsGroup);
			dockKey.setFloatEnabled(true);
		}

		public Component getComponent() {
			return this;
		}

		public DockKey getDockKey() {
			return dockKey;
		}
	}
}
