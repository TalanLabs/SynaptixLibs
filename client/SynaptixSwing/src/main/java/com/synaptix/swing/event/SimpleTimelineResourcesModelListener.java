package com.synaptix.swing.event;

import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

public interface SimpleTimelineResourcesModelListener extends EventListener {

    public void resourceAdded(SimpleTimelineResourcesModelEvent e);

    public void resourceRemoved(SimpleTimelineResourcesModelEvent e);

    public void resourceMoved(SimpleTimelineResourcesModelEvent e);

    public void resourceMarginChanged(ChangeEvent e);

    public void resourceSelectionChanged(ListSelectionEvent e);
}
