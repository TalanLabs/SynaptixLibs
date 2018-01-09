package com.synaptix.widget.perimeter.view.swing;

import javax.swing.*;

import com.synaptix.client.view.IView;
import com.synaptix.swing.WaitComponentFeedbackPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class AbstractPerimeterWidget extends WaitComponentFeedbackPanel implements IPerimeterWidget, IView {

	private static final long serialVersionUID = -1018312750879846662L;

	public JComponent getView() {
		return this;
	}

	public void addPerimetreWidgetListener(PerimeterWidgetListener l) {
		listenerList.add(PerimeterWidgetListener.class, l);
	}

	public void removePerimetreWidgetListener(PerimeterWidgetListener l) {
		listenerList.remove(PerimeterWidgetListener.class, l);
	}

	protected void fireTitleChanged() {
		PerimeterWidgetListener[] ls = listenerList.getListeners(PerimeterWidgetListener.class);
		for (PerimeterWidgetListener l : ls) {
			l.titleChanged(this);
		}
	}

	protected void fireValuesChanged() {
		PerimeterWidgetListener[] ls = listenerList.getListeners(PerimeterWidgetListener.class);
		for (PerimeterWidgetListener l : ls) {
			l.valuesChanged(this);
		}
	}

    @Override
    public void addContent(Component content) {
        if(content != null && JTextField.class.isAssignableFrom(content.getClass())){
            content.addMouseListener(new FieldMouseAdapter());
        } else if (content != null && JPanel.class.isAssignableFrom(content.getClass())){
            JPanel panel = (JPanel) content;
            for(Component component:panel.getComponents()){
                if(component != null && JTextField.class.isAssignableFrom(component.getClass())){
                    component.addMouseListener(new FieldMouseAdapter());
                }
            }
        }
        super.addContent(content);
    }

    public class FieldMouseAdapter extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            //Right Double click
            if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
                if ((e.getSource() instanceof JFormattedTextField) && (((JFormattedTextField) e.getSource()).isEnabled())) {
                    ((JFormattedTextField) e.getSource()).setValue(null);
                    fireValuesChanged();
                } else if ((e.getSource() instanceof JTextField) && (((JTextField) e.getSource()).isEnabled())) {
                    ((JTextField) e.getSource()).setText(null);
                    fireValuesChanged();
                }
            }
        }
    }

}
