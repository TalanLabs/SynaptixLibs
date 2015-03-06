import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JArrowScrollPane;
import com.synaptix.swing.JChoixPanel;
import com.synaptix.swing.path.JPath;
import com.synaptix.swing.wizard.AbstractValidationWizardPage;
import com.synaptix.swing.wizard.JDefaultWizardDialog;
import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;
import com.synaptix.swing.wizard.action.FinishWizardAction;
import com.synaptix.swing.wizard.action.ForwardWizardAction;
import com.synaptix.swing.wizard.action.WizardAction;

public class MainWizard {

	private static class GeneralWizardPage extends AbstractValidationWizardPage<Map<String, Object>> {

		private static final long serialVersionUID = -5435089404172746586L;

		private WizardAction<Map<String, Object>> nextWizardAction;

		private WizardAction<Map<String, Object>> previousWizardAction;

		private JTextField textField;

		private String id;

		private String title;

		public GeneralWizardPage(String id, String title, String previous, String next) {
			super();

			this.id = id;
			this.title = title;
			if (next != null) {
				nextWizardAction = new ForwardWizardAction<Map<String, Object>>(next);
			}

			if (previous != null) {
				previousWizardAction = new ForwardWizardAction<Map<String, Object>>(previous);
			}
		}

		protected void initialize() {
			textField = new JTextField();
		}

		protected JComponent buildEditorPanel() {
			return textField;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return "Description de la page " + title;
		}

		public void load(Map<String, Object> e) {
			textField.setText((String) e.get(getId()));
		}

		public void commit(Map<String, Object> e) {
			e.put(getId(), textField.getText());
		}

		public WizardAction<Map<String, Object>> getPreviousAction() {
			return previousWizardAction;
		}

		public WizardAction<Map<String, Object>> getNextAction() {
			return nextWizardAction;
		}

		public WizardAction<Map<String, Object>> getFinishAction() {
			return null;
		}
	}

	private static class ChoixWizardPage extends JPanel implements WizardPage<Map<String, Object>> {

		private static final long serialVersionUID = 806098851823207488L;

		private MainChoix choix;

		private WizardAction<Map<String, Object>> nextWizardAction;

		private WizardAction<Map<String, Object>> previousWizardAction;

		public ChoixWizardPage(final Wizard<Map<String, Object>> wizard, String previous, String next) {
			super(new BorderLayout());

			nextWizardAction = new ForwardWizardAction<Map<String, Object>>(next);
			nextWizardAction.setEnabled(false);
			previousWizardAction = new ForwardWizardAction<Map<String, Object>>(previous);

			choix = new MainChoix();
			choix.addSelectionListener(new JChoixPanel.SelectionListener<JPath>() {
				public void selectionChanged(JPath c) {
					nextWizardAction.setEnabled(c != null);

					wizard.nextWizardPage();
				}
			});

			this.add(new JArrowScrollPane(choix), BorderLayout.CENTER);
		}

		public String getId() {
			return "choix";
		}

		public String getTitle() {
			return "Choix parcours";
		}

		public String getDescription() {
			return "Permet de choisir en parcours";
		}

		public Icon getIcon() {
			return null;
		}

		public Component getView() {
			return this;
		}

		public void load(Map<String, Object> e) {

		}

		public void commit(Map<String, Object> e) {
			e.put("choix_path", choix.getSelectedComponent());
		}

		public WizardAction<Map<String, Object>> getNextAction() {
			return nextWizardAction;
		}

		public WizardAction<Map<String, Object>> getPreviousAction() {
			return previousWizardAction;
		}

		public WizardAction<Map<String, Object>> getHelpAction() {
			return null;
		}

		public WizardAction<Map<String, Object>> getFinishAction() {
			return null;
		}
	}

	private static class LastWizardPage extends AbstractValidationWizardPage<Map<String, Object>> {

		private static final long serialVersionUID = -5435089404172746586L;

		private JCheckBox box;

		private FinishWizardAction<Map<String, Object>> finishWizardAction;

		protected void initialize() {
			finishWizardAction = new FinishWizardAction<Map<String, Object>>();
			finishWizardAction.setEnabled(false);

			box = new JCheckBox("Etes-vous d'accord pour me fournir toutes vos informations ?");
			box.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					finishWizardAction.setEnabled(box.isSelected());
				}
			});
		}

		protected JComponent buildEditorPanel() {
			return box;
		}

		public String getId() {
			return "lastPage";
		}

		public String getTitle() {
			return "Dernière page";
		}

		public String getDescription() {
			return "Une description de la dernière page";
		}

		public void load(Map<String, Object> e) {
		}

		public void commit(Map<String, Object> e) {
		}

		public WizardAction<Map<String, Object>> getPreviousAction() {
			return new ForwardWizardAction<Map<String, Object>>("choix");
		}

		public WizardAction<Map<String, Object>> getNextAction() {
			return null;
		}

		public WizardAction<Map<String, Object>> getFinishAction() {
			return finishWizardAction;
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JDefaultWizardDialog<Map<String, Object>> wizard = new JDefaultWizardDialog<Map<String, Object>>("Test");

		wizard.addWizardPage(new GeneralWizardPage("general", "Général", null, "page2"));
		wizard.addWizardPage(new GeneralWizardPage("page2", "Page 2", "general", "page3"));
		int i = 3;
		for (i = 3; i < 12; i++) {
			wizard.addWizardPage(new GeneralWizardPage("page" + i, "Page " + i, "page" + String.valueOf(i - 1), "page" + String.valueOf(i + 1)));
		}
		wizard.addWizardPage(new GeneralWizardPage("page" + i, "Page " + i, "page" + String.valueOf(i - 1), "choix"));
		wizard.addWizardPage(new ChoixWizardPage(wizard, "page" + i, "lastPage"));
		wizard.addWizardPage(new LastWizardPage());

		JButton button = new JButton("Lancer...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (wizard.showWizard(map) == JDefaultWizardDialog.FINISH_OPTION) {
					System.out.println(wizard.getBean());
				}
			}
		});

		frame.getContentPane().add(button, BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}