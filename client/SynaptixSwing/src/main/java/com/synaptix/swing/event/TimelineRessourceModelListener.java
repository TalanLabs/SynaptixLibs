package com.synaptix.swing.event;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

public interface TimelineRessourceModelListener extends EventListener {

    public void ressourceAdded(TimelineRessourceModelEvent e);

    public void ressourceRemoved(TimelineRessourceModelEvent e);

    public void ressourceMoved(TimelineRessourceModelEvent e);

    public void ressourceMarginChanged(ChangeEvent e);

    public void ressourceSelectionChanged(ListSelectionEvent e);
}
