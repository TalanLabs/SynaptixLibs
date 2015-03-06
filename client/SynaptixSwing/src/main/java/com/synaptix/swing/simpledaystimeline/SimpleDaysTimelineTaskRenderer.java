package com.synaptix.swing.simpledaystimeline;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;

public interface SimpleDaysTimelineTaskRenderer {

	/**
	 * Pourcentage de la hauteur prise par la tache
	 * 
	 * @return valeur entre 0 et 1
	 */
	public abstract double getSelectionHeightPourcent();

	/**
	 * Dessine la tache
	 * 
	 * @param g
	 * @param rect
	 * @param simpleDaysTimeline
	 * @param task
	 * @param isSelected
	 * @param hasFocus
	 * @param resource
	 */
	public abstract void paintTask(Graphics g, Rectangle rect,
			JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task,
			boolean isSelected, boolean hasFocus, int resource);

	/**
	 * Donner le tooltips selon la date et la position du point
	 * 
	 * @param rect
	 * @param simpleDaysTimeline
	 * @param task
	 * @param resource la ligne selon l'affichage
	 * @param dayDate le jour
	 * @param point la position exacte dans le rectangle
	 * @return
	 */
	public abstract String getToolTipText(Rectangle rect,
			JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task,
			int resource, DayDate dayDate, Point point);

}
