package titleheader;

import java.awt.Component;

import javax.swing.Action;

public interface CardHeaderActionRenderer {

	public Component getCardHeaderActionRendererComponent(JCardHeader cardHeader, boolean selected, Action action, boolean pressed, boolean rollover);

}
