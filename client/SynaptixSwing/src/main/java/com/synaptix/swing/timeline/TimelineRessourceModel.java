package com.synaptix.swing.timeline;

import java.util.Enumeration;

import javax.swing.ListSelectionModel;

import com.synaptix.swing.event.TimelineRessourceModelListener;

public interface TimelineRessourceModel {

    public void addRessource(TimelineRessource aRessource);

    public void removeRessource(TimelineRessource ressource);
    
    public void moveRessource(int ressourceIndex, int newIndex);

    public void setRessourceMargin(int newMargin);
    
    public int getRessourceCount();
    
    public Enumeration<TimelineRessource> getRessources();

    public int getRessourceIndex(Object ressourceIdentifier);

    public TimelineRessource getRessource(int ressourceIndex);

    public int getDefaultHeight();
    
    public int getHeight(int ressourceIndex);
    
    public int getRessourceMargin();
    
    public int getRessourceIndexAtY(int yPosition);
    
    public int getTotalRessourceHeight();

    public void setRessourceSelectionAllowed(boolean flag);

    public boolean getRessourceSelectionAllowed();

    public int[] getSelectedRessources();

    public int getSelectedRessourceCount();

    public void setSelectionModel(ListSelectionModel newModel); 
    
    public ListSelectionModel getSelectionModel(); 
    
    public void addRessourceModelListener(TimelineRessourceModelListener x);

    public void removeRessourceModelListener(TimelineRessourceModelListener x);
}
