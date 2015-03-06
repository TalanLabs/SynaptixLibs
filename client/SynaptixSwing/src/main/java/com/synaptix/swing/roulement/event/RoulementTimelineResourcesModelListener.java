package com.synaptix.swing.roulement.event;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

public interface RoulementTimelineResourcesModelListener extends EventListener {

    public void resourceAdded(RoulementTimelineResourcesModelEvent e);

    public void resourceRemoved(RoulementTimelineResourcesModelEvent e);

    public void resourceMoved(RoulementTimelineResourcesModelEvent e);

    public void resourceMarginChanged(ChangeEvent e);

    public void resourceSelectionChanged(ListSelectionEvent e);
}
