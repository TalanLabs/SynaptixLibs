//import java.awt.BorderLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.Serializable;
//import java.util.Date;
//import java.util.Properties;
//
//import javax.swing.DefaultListModel;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//
//import com.jgoodies.forms.builder.PanelBuilder;
//import com.jgoodies.forms.layout.CellConstraints;
//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.validation.Severity;
//import com.jgoodies.validation.ValidationResult;
//import com.jgoodies.validation.ValidationResultModel;
//import com.jgoodies.validation.message.SimpleValidationMessage;
//import com.synaptix.jtattoo.plaf.gaby.GabyLookAndFeel;
//import com.synaptix.swing.search.AbstractSearchHeaderModel;
//import com.synaptix.swing.search.Filter;
//import com.synaptix.swing.search.JSearchHeader;
//import com.synaptix.swing.search.SearchHeaderModel;
//import com.synaptix.swing.search.filter.DefaultDate;
//import com.synaptix.swing.search.filter.DefaultDateFieldFilter;
//import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
//import com.synaptix.swing.search.filter.DefaultTextFieldFilter;
//import com.synaptix.swing.search.filter.DefaultTwoDatesFieldFilter;
//import com.synaptix.swing.utils.Manager;
//
//public class MainSearchHeader {
//
//	public static void main(String[] args) {
//		try {
//			Properties props = new Properties();
//			props.put("logoString", "Sncf"); //$NON-NLS-1$ //$NON-NLS-2$
//			GabyLookAndFeel.setCurrentTheme(props);
//			UIManager.setLookAndFeel(new GabyLookAndFeel());
//		} catch (UnsupportedLookAndFeelException e) {
//			Manager.installLookAndFeelWindows();
//		}
//
//		final SearchHeaderModel shm = new MySearchHeaderModel();
//		final JSearchHeader searchHeader = new JSearchHeader(shm);
//
//		final ValidationResultModel validationResultModel = searchHeader.getValidationResultModel();
//
//		searchHeader.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//				ValidationResult result = new ValidationResult();
//				result.add(new SimpleValidationMessage("Coucou", Severity.ERROR, searchHeader.getFilterComponent("filtreEntiteCom")));
//				result.add(new SimpleValidationMessage("Coucou", Severity.ERROR, searchHeader.getFilterComponent("filtreDateValidite")));
//				result.add(new SimpleValidationMessage("Coucou", Severity.ERROR, searchHeader.getFilterComponent("filtreNumeroFlux")));
//				result.add(new SimpleValidationMessage("Coucou", Severity.ERROR, searchHeader.getFilterComponent("dateDebutEtdateFinValidite")));
//				validationResultModel.setResult(result);
//			}
//		});
//
//		JButton b = new JButton("toto");
//		b.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				searchHeader.defaultFilters();
//
//				searchHeader.setFilterValue("filtreEntiteCom", "la");
//				searchHeader.setFilterValue("filtreNumeroFlux", "rien");
//			}
//		});
//
//		FormLayout layout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)", "CENTER:DEFAULT:NONE,FILL:pref"); //$NON-NLS-1$
//		PanelBuilder builder1 = new PanelBuilder(layout1);
//		CellConstraints cc1 = new CellConstraints();
//		builder1.add(searchHeader, cc1.xy(1, 2));
//
//		FormLayout layout = new FormLayout("FILL:50DLU:GROW(1.0)", //$NON-NLS-1$
//				"CENTER:d,3DLU,FILL:d:grow(1.0)"); //$NON-NLS-1$
//		PanelBuilder builder = new PanelBuilder(layout);
//		builder.setDefaultDialogBorder();
//		CellConstraints cc = new CellConstraints();
//		builder.add(builder1.getPanel(), cc.xy(1, 1));
//		builder.add(new JScrollPane(new JTable()), cc.xy(1, 3));
//
//		final JFrame frame1 = new JFrame();
//		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame1.getContentPane().setLayout(new BorderLayout());
//		frame1.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
//		frame1.getContentPane().add(b, BorderLayout.SOUTH);
//		frame1.setSize(1280, 720);
//
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				frame1.setVisible(true);
//			}
//		});
//	}
//
//	private static final class MySearchHeaderModel extends AbstractSearchHeaderModel {
//
//		private Filter[] filters;
//
//		public MySearchHeaderModel() {
//			super();
//
//			DefaultSuperComboBoxFilter<String> typePAFilter = new DefaultSuperComboBoxFilter<String>("filtreEntiteCom", //$NON-NLS-1$
//					"Entite", 150, new DefaultListModel(), new DefaultSuperComboBoxFilter.ObjectToKey<String>() {
//						public Serializable getKey(String e) {
//							return e != null ? e : null;
//						}
//					}, null);
//			DefaultTwoDatesFieldFilter dates = new DefaultTwoDatesFieldFilter("dateDebutEtdateFinValidite", "Date compris entre : ");
//			filters = new Filter[] { typePAFilter, dates,
//					new DefaultDateFieldFilter("filtreDateValidite", "Date de validité", 100, true, new DefaultDate(new Date(), true)),
//					new DefaultTextFieldFilter("filtreNomFlux", "Nom flux", 150), new DefaultTextFieldFilter("filtreNumeroFlux", "Numero flux") };
//		}
//
//		public int getFilterCount() {
//			return filters.length;
//		}
//
//		public Filter getFilter(int index) {
//			return filters[index];
//		}
//	}
//
// }
