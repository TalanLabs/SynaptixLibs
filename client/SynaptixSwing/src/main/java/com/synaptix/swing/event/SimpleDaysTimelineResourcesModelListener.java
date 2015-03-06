package com.synaptix.swing.event;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

public interface SimpleDaysTimelineResourcesModelListener extends EventListener {

    public void resourceAdded(SimpleDaysTimelineResourcesModelEvent e);

    public void resourceRemoved(SimpleDaysTimelineResourcesModelEvent e);

    public void resourceMoved(SimpleDaysTimelineResourcesModelEvent e);

    public void resourceMarginChanged(ChangeEvent e);

    public void resourceSelectionChanged(ListSelectionEvent e);
}
