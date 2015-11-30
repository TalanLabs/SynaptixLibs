import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JSearch;
import com.synaptix.swing.search.AbstractResult;
import com.synaptix.swing.search.AbstractSearchModel;
import com.synaptix.swing.search.AbstractSearchRowHighlight;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.Filter.TypeVisible;
import com.synaptix.swing.search.Result;
import com.synaptix.swing.search.filter.DefaultCheckBoxFilter;
import com.synaptix.swing.search.filter.DefaultComboBoxFilter;
import com.synaptix.swing.search.filter.DefaultDateComboBoxFilter;
import com.synaptix.swing.search.filter.DefaultDateFieldFilter;
import com.synaptix.swing.search.filter.DefaultDateHourFieldFilter;
import com.synaptix.swing.search.filter.DefaultTextFieldFilter;
import com.synaptix.swing.search.filter.Item;
import com.synaptix.swing.table.Column;
import com.synaptix.swing.table.SimpleColumn;
import com.synaptix.swing.utils.Manager;

public class TestSearch2 {

	private static JSearch search;

	private static class MySearchModel extends AbstractSearchModel {

		private static final long serialVersionUID = -8860073159579745170L;

		private List<Filter> filters;

		private List<Column> columns;

		public MySearchModel() {
			super();

			filters = new ArrayList<Filter>();
			filters.add(new DefaultTextFieldFilter("Prénom", 100, true, null));
			filters.add(new DefaultTextFieldFilter("Nom", 50));
			filters
					.add(new DefaultTextFieldFilter("Profession", 10, true,
							null));
			filters.add(new DefaultDateFieldFilter("Date de naissance", true,
					null));
			filters.add(new DefaultDateHourFieldFilter("date et heure", true,
					null));
			filters
					.add(new DefaultDateComboBoxFilter("date combo", true, null));
			filters.add(new DefaultCheckBoxFilter("Sexe M", 75, "H / F", true,
					false));
			filters.add(new DefaultTextFieldFilter("Adresse", 100, true, null));
			filters.add(new DefaultTextFieldFilter("Code postal", 50));

			DefaultTextFieldFilter d = new DefaultTextFieldFilter("Ville", 100);
			d.setTypeVisible(TypeVisible.Invisible);
			filters.add(d);
			filters.add(new DefaultComboBoxFilter("Pays", 100, new Item[] {
					new Item("FR", "France"), new Item("EN", "Anglais") },
					null, true, null));

			columns = new ArrayList<Column>();
			columns.add(new SimpleColumn("prenom", "Prénom", String.class,
					true, false, true, true, false, Integer.class));
			columns.add(new SimpleColumn("nom", "Nom", String.class));
			columns.add(new SimpleColumn("valider", "Valider", Boolean.class));
			columns.add(new SimpleColumn("jour", "Jour", Date.class));
		}

		public Filter getFilter(final int index) {
			return filters.get(index);
		}

		public int getFilterCount() {
			return filters.size();
		}

		public String getSaveName() {
			return "MySearchToto";
		}

		public Result search(Map<String, Object> filters) {
			System.out.println("Date " + filters.get("Date de naissance"));
			return new ListResult();
		}

		private final class ListResult extends AbstractResult {
			private String[] columns;

			public ListResult() {

				columns = new String[] { "commandeSillon", "prenom", "nom",
						"valider", "jour" };
			}

			public String getColumnId(int index) {
				return columns[index];
			}

			public int getColumnCount() {
				return columns.length;
			}

			public int getRowCount() {
				return 100;
			}

			public Object getValue(int rowIndex, int columnIndex) {
				Object res = null;
				switch (columnIndex) {
				case 0:
					res = "Erreur";
					break;
				case 1:
					res = "Prénom"
							+ String.valueOf(rowIndex * getColumnCount()
									+ columnIndex);
					break;
				case 2:
					res = "Nom"
							+ String.valueOf(rowIndex * getColumnCount()
									+ columnIndex);
					break;
				case 3:
					if (rowIndex % 5 == 4)
						res = true;
					else
						res = false;
					break;
				case 4:
					Calendar c = Calendar.getInstance();
					c.add(Calendar.HOUR_OF_DAY, rowIndex * 3);
					if (rowIndex % 5 == 2)
						res = null;
					else
						res = c.getTime();
					break;
				}
				return res;
			}
		}

		public int searchCount(Map<String, Object> filters) {
			return 100;
		}

		public Column getColumn(int index) {
			return columns.get(index);
		}

		public int getColumnCount() {
			return columns.size();
		}

		public boolean validate(Map<String, Object> filters) {
			// JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(),
			// "rien");
			return true;
		}
	}

	private static final class MySearchRowHighLight extends
			AbstractSearchRowHighlight {

		private static final Color c = new Color(200, 227, 255);

		@Override
		public Color getForegroundColor(JSearch search, Result result,
				boolean isSelected, boolean hasFocus, int rowModel,
				int rowView, int columnModel, int columnView) {
			if (!isSelected) {
				if (rowView % 2 == 0) {
					return Color.BLACK;
				}
				return Color.BLACK;
			}
			return null;
		}

		@Override
		public Color getBackgroundColor(JSearch search, Result result,
				boolean isSelected, boolean hasFocus, int rowModel,
				int rowView, int columnModel, int columnView) {
			if (!isSelected) {
				if (rowView % 2 == 0) {
					return Color.WHITE;
				}
				return c;
			}
			return null;
		}
	}

	private static final class MyAction extends AbstractAction {

		private static final long serialVersionUID = -8810075307425250116L;

		public MyAction(String title) {
			super(title);
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println(search.getValueFilters());
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new MyAction("menu1"));
		popupMenu.add(new MyAction("menu2"));
		popupMenu.add(new MyAction("menu3"));

		search = new JSearch(new MySearchModel());
		search.setSearchRowHighlight(new MySearchRowHighLight());
		search.setPopupMenu(popupMenu);
		search.getSearchHeader().setEnabled(true);
		search.getTable().setShowTableFooter(true);

		JToolBar toolBar = new JToolBar();
		toolBar.add(new JButton("Gaby..."));
		toolBar.setFloatable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(toolBar, BorderLayout.NORTH);
		panel.add(search, BorderLayout.CENTER);

		frame.getContentPane().add(panel, BorderLayout.CENTER);

		frame.setTitle("TestSearch");
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
