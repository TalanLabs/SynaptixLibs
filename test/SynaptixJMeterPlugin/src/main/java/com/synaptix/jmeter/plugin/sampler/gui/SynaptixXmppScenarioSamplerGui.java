package com.synaptix.jmeter.plugin.sampler.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import com.synaptix.jmeter.plugin.sampler.SynaptixXmppScenarioSampler;

public class SynaptixXmppScenarioSamplerGui extends AbstractSamplerGui {

	private static final long serialVersionUID = -6083310112287926855L;

	private JTextField urlServerTextField;

	private JTextField usernameTextField;

	private JPasswordField passwordTextField;

	private JTextField filePathField;

	private JButton chooseFileButton;

	public SynaptixXmppScenarioSamplerGui() {
		super();

		initComponents();
	}

	private void initComponents() {
		this.setLayout(new BorderLayout(0, 5));

		this.setBorder(makeBorder());
		this.add(makeTitlePanel(), BorderLayout.NORTH);

		urlServerTextField = new JTextField();
		usernameTextField = new JTextField();
		passwordTextField = new JPasswordField();

		filePathField = new JTextField();
		filePathField.setEnabled(false);
		chooseFileButton = new JButton("Choose...");
		chooseFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Scenario file (.sce)";
					}

					@Override
					public boolean accept(File f) {
						return !f.isFile() || f.getName().toLowerCase().endsWith(".sce");
					}
				});
				if (fileChooser.showOpenDialog(SynaptixXmppScenarioSamplerGui.this) == JFileChooser.APPROVE_OPTION) {
					File newFile = fileChooser.getSelectedFile();
					filePathField.setText(newFile.getAbsolutePath());
				}
			}
		});

		JPanel filePanel = new JPanel(new BorderLayout(5, 0));
		filePanel.add(filePathField, BorderLayout.CENTER);
		filePanel.add(chooseFileButton, BorderLayout.EAST);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(createFieldPanel("Url server : ", urlServerTextField));
		mainPanel.add(createFieldPanel("Username : ", usernameTextField));
		mainPanel.add(createFieldPanel("Password : ", passwordTextField));
		mainPanel.add(createFieldPanel("Scenario : ", filePanel));
		this.add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createFieldPanel(String title, JComponent component) {
		JPanel panel = new JPanel(new BorderLayout(5, 0));
		JLabel label = new JLabel(title); // $NON-NLS-1$
		label.setLabelFor(component);
		panel.add(label, BorderLayout.WEST);
		panel.add(component, BorderLayout.CENTER);
		return panel;
	}

	@Override
	public String getStaticLabel() {
		return "Scenario (protocole XMPP)";
	}

	@Override
	public String getLabelResource() {
		return "synaptixXmppScenario";
	}

	@Override
	public TestElement createTestElement() {
		SynaptixXmppScenarioSampler synaptixXmppScenarioSampler = new SynaptixXmppScenarioSampler();
		modifyTestElement(synaptixXmppScenarioSampler);
		return synaptixXmppScenarioSampler;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		element.clear();
		configureTestElement(element);

		SynaptixXmppScenarioSampler synaptixXmppScenarioSampler = (SynaptixXmppScenarioSampler) element;
		synaptixXmppScenarioSampler.setUrlServer(urlServerTextField.getText());
		synaptixXmppScenarioSampler.setUsername(usernameTextField.getText());
		synaptixXmppScenarioSampler.setPassword(String.valueOf(passwordTextField.getPassword()));
		synaptixXmppScenarioSampler.setFilePath(filePathField.getText());
	}

	@Override
	public void configure(TestElement element) {
		super.configure(element);

		SynaptixXmppScenarioSampler synaptixXmppScenarioSampler = (SynaptixXmppScenarioSampler) element;
		urlServerTextField.setText(synaptixXmppScenarioSampler.getUrlServer());
		usernameTextField.setText(synaptixXmppScenarioSampler.getUsername());
		passwordTextField.setText(synaptixXmppScenarioSampler.getPassword());
		filePathField.setText(synaptixXmppScenarioSampler.getFilePath());
	}

	@Override
	public void clearGui() {
		super.clearGui();

		urlServerTextField.setText(null);
		usernameTextField.setText(null);
		passwordTextField.setText(null);
		filePathField.setText(null);
	}
}
