package com.synaptix.deployer.client.view.swing.deployerManagement;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.EnvironmentNode;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.swing.JSyScrollPane;

public class EnvironmentPanel extends AbstractManagementPanel<EnvironmentNode> {

	private static final long serialVersionUID = -7340367282640956401L;

	private static final ResizableIcon lookIcon = ImageWrapperResizableIcon.getIcon(EnvironmentPanel.class.getResource("/images/deployer/look.png"), new Dimension(18, 18));

	public EnvironmentPanel(EnvironmentNode node, DeployerManagementController managementController) {
		super(node, managementController);
	}

	@Override
	protected void initComponents() {

	}

	@Override
	protected JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:80DLU:NONE,3DLU,RIGHT:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,FILL:3DLU:GROW(1.0),FILL:80DLU:NONE,3DLU,RIGHT:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		List<ISynaptixEnvironment> supportedEnvironments = getManagementController().getSupportedEnvironments();
		int instanceNb = 0;
		for (final ISynaptixEnvironment supportedEnvironment : supportedEnvironments) {
			JButton button = new JButton(supportedEnvironment.getName());
			if (supportedEnvironment.isReadOnly()) {
				button.setEnabled(false);
			} else {
				button.addActionListener(new AbstractAction() {

					private static final long serialVersionUID = 311468131929017654L;

					@Override
					public void actionPerformed(ActionEvent e) {
						getManagementController().selectEnvironment(getNode(), supportedEnvironment);
					}
				});
			}
			builder.append(button, new JLabel("<html><i>(" + supportedEnvironment.getIp() + ")</i></html>"));
			builder.setColumn(builder.getColumn() - 2);
			instanceNb = Math.max(instanceNb, supportedEnvironment.getInstances().size());
			for (final IEnvironmentInstance environmentInstance : supportedEnvironment.getInstances()) {
				if (builder.getRow() >= builder.getRowCount()) {
					builder.appendRow("FILL:DEFAULT:NONE");
				}
				builder.nextRow();
				JLabel lookLabel = new JLabel(lookIcon);
				lookLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lookLabel.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						super.mouseClicked(e);

						getManagementController().watchEnvironment(supportedEnvironment, environmentInstance);
					}

				});
				builder.append(new JLabel(environmentInstance.getName()), lookLabel);
				builder.setColumn(builder.getColumn() - 4);
			}
			if (builder.getColumn() >= 7) {
				builder.appendRow("3DLU");
				builder.setRow(builder.getRow() + 2 + instanceNb - supportedEnvironment.getInstances().size());
				instanceNb = 0;
				builder.setColumn(1);
			} else {
				builder.setRow(builder.getRow() - supportedEnvironment.getInstances().size());
				builder.setColumn(7);
			}
		}

		JSyScrollPane jSyScrollPane = new JSyScrollPane(builder.getPanel());
		jSyScrollPane.setBorder(null);
		return jSyScrollPane;
	}

	@Override
	public void updateValidation() {
	}
}
