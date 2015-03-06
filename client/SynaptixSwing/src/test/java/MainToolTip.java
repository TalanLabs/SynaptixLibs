//import java.awt.BorderLayout;
//import java.awt.CardLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.Serializable;
//import java.util.Date;
//
//import javax.swing.DefaultListModel;
//import javax.swing.JButton;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//
//import org.pushingpixels.substance.api.SubstanceLookAndFeel;
//import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;
//import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;
//
//import com.jgoodies.validation.Severity;
//import com.jgoodies.validation.ValidationResult;
//import com.jgoodies.validation.message.SimpleValidationMessage;
//import com.jgoodies.validation.util.DefaultValidationResultModel;
//import com.synaptix.swing.search.AbstractSearchHeaderModel;
//import com.synaptix.swing.search.Filter;
//import com.synaptix.swing.search.JSearchHeader;
//import com.synaptix.swing.search.filter.DefaultDate;
//import com.synaptix.swing.search.filter.DefaultDateFieldFilter;
//import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
//import com.synaptix.swing.search.filter.DefaultTextFieldFilter;
//import com.synaptix.swing.search.filter.DefaultTwoDatesFieldFilter;
//import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;
//import com.synaptix.swing.tooltip.ToolTipFocusListener;
//import com.synaptix.swing.utils.Manager;
//import com.synaptix.swing.utils.ToolBarFactory;
//
//public class MainToolTip {
//
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
//					UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE, new WidestComboPopupPrototype());
//
//					JFrame.setDefaultLookAndFeelDecorated(true);
//					JDialog.setDefaultLookAndFeelDecorated(true);
//				} catch (UnsupportedLookAndFeelException e) {
//					Manager.installLookAndFeelWindows();
//				}
//
//				JFrame frame = new JFrame();
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//				final CardLayout cardLayout = new CardLayout();
//				final JPanel cardPanel = new JPanel(cardLayout);
//
//				final JSearchHeader searchHeader = new JSearchHeader(new MySearchHeaderModel());
//				cardPanel.add(searchHeader, "search");
//
//				final JTable table = new JTable(300, 11);
//				cardPanel.add(new JScrollPane(table), "table");
//
//				DefaultValidationResultModel model = new DefaultValidationResultModel();
//				new ToolTipFeedbackComponentValidationResultFocusListener(model);
//
//				ValidationResult result = new ValidationResult();
//				result.add(new SimpleValidationMessage("Coucou", Severity.WARNING, table));
//				result.add(new SimpleValidationMessage("Coucou", Severity.ERROR, table));
//				model.setResult(result);
//
//				frame.getContentPane().add(cardPanel, BorderLayout.CENTER);
//
//				JButton b1 = new JButton("Ecran1");
//				b1.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						cardPanel.removeAll();
//						cardPanel.add(searchHeader, "toto"); //$NON-NLS-1$
//						cardPanel.revalidate();
//						cardPanel.repaint();
//						cardLayout.show(cardPanel, "toto"); //$NON-NLS-1$
//					}
//				});
//				JButton b2 = new JButton("Ecran2");
//				b2.addActionListener(new ActionListener() {
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						cardPanel.removeAll();
//						cardPanel.add(new JScrollPane(table), "toto"); //$NON-NLS-1$
//						cardPanel.revalidate();
//						cardPanel.repaint();
//						cardLayout.show(cardPanel, "toto"); //$NON-NLS-1$
//					}
//				});
//
//				frame.getContentPane().add(ToolBarFactory.buildToolBar(b1, b2), BorderLayout.NORTH);
//
//				frame.setSize(800, 600);
//				frame.setLocationRelativeTo(null);
//				frame.setVisible(true);
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
//
//			DefaultTextFieldFilter numeroFluxFilter = new DefaultTextFieldFilter("filtreNumeroFlux", "Numero flux");
//			numeroFluxFilter.getComponent().addFocusListener(new ToolTipFocusListener("Test"));
//
//			DefaultTextFieldFilter numeroFluxFilter2 = new DefaultTextFieldFilter("filtreNumeroFlux", "Numero flux");
//			numeroFluxFilter2.getComponent().addFocusListener(new ToolTipFocusListener("Test"));
//
//			filters = new Filter[] { numeroFluxFilter, typePAFilter, dates, new DefaultDateFieldFilter("filtreDateValidite", "Date de validité", 100, true, new DefaultDate(new Date(), true)),
//					new DefaultTextFieldFilter("filtreNomFlux", "Nom flux", 150), new DefaultTextFieldFilter("filtreNomFlux", "Nom flux", 150), numeroFluxFilter2 };
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
