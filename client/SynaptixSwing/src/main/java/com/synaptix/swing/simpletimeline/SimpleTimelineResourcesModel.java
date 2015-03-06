package com.synaptix.swing.simpletimeline;

import java.util.Enumeration;

import javax.swing.ListSelectionModel;

import com.synaptix.swing.event.SimpleTimelineResourcesModelListener;

public interface SimpleTimelineResourcesModel {

    public void addResource(SimpleTimelineResource aResource);

    public void removeResource(SimpleTimelineResource resource);
    
    public void moveResource(int resourceIndex, int newIndex);

    public void setResourceMargin(int newMargin);
    
    public int getResourceCount();
    
    public Enumeration<SimpleTimelineResource> getResources();

    public int getResourceIndex(Object resourceIdentifier);

    public SimpleTimelineResource getResource(int resourceIndex);

    public int getDefaultHeight();
    
    public int getHeight(int resourceIndex);
    
    public int getResourceMargin();
    
    public int getResourceIndexAtY(int yPosition);
    
    public int getTotalResourceHeight();

    public void setResourceSelectionAllowed(boolean flag);

    public boolean getResourceSelectionAllowed();

    public int[] getSelectedResources();

    public int getSelectedResourceCount();

    public void setSelectionModel(ListSelectionModel newModel); 
    
    public ListSelectionModel getSelectionModel(); 
    
    public void addResourcesModelListener(SimpleTimelineResourcesModelListener x);

    public void removeResourcesModelListener(SimpleTimelineResourcesModelListener x);
}
