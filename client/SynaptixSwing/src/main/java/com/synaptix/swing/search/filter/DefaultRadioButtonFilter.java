package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import com.synaptix.swing.search.AbstractFilter;

public class DefaultRadioButtonFilter extends AbstractFilter {

	private String id;

	private String name;

	private JRadioButton radioButton;

	private JRadioButton defaultRadioButton;

	/**
	 * 
	 * constructeur avec un seul argument
	 * 
	 * 
	 * 
	 * @param name
	 */

	public DefaultRadioButtonFilter(String name) {

		this(name, name);

	}

	/**
	 * 
	 * constructeur ayant 2 arguments : name, width
	 * 
	 * 
	 * 
	 * @param name
	 * 
	 * @param width
	 */

	public DefaultRadioButtonFilter(String name, int width) {

		this(name, name, width);

	}

	/**
	 * 
	 * constructeur sans l'argument id et titre, utilisant useDefault
	 * 
	 * 
	 * 
	 * @param name
	 * 
	 * @param width
	 * 
	 * @param useDefault
	 * 
	 * @param defaultValue
	 */

	public DefaultRadioButtonFilter(String name, int width, boolean useDefault,

	boolean defaultValue) {

		this(name, name, width, useDefault, defaultValue);

	}

	/**
	 * 
	 * constructeur ayant 2 arguments : id, name, width mis par défaut à 75
	 * 
	 * 
	 * 
	 * @param id
	 * 
	 * @param name
	 */

	public DefaultRadioButtonFilter(String id, String name) {

		this(id, name, 75);

	}

	/**
	 * 
	 * constructeur sans l'argument titre ,utilisant useDefault et width mis par
	 * 
	 * défaut à 75
	 * 
	 * 
	 * 
	 * @param id
	 * 
	 * @param name
	 * 
	 * @param useDefault
	 * 
	 * @param defaultValue
	 */

	public DefaultRadioButtonFilter(String id, String name, boolean useDefault,

	Boolean defaultValue) {

		this(id, name, 75, useDefault, defaultValue);

	}

	/**
	 * 
	 * constructeur dont le titre comporte "Coché"
	 * 
	 * 
	 * 
	 * @param id
	 * 
	 * @param name
	 * 
	 * @param width
	 */

	public DefaultRadioButtonFilter(String id, String name, int width) {

		this(id, name, width, true, null);

	}

	/**
	 * 
	 * constructeur sans l'argument titre
	 * 
	 * 
	 * 
	 * @param id
	 * 
	 * @param name
	 * 
	 * @param width
	 * 
	 * @param useDefault
	 * 
	 * @param defaultValue
	 */

	public DefaultRadioButtonFilter(String id, String name, int width,

	boolean useDefault, Boolean defaultValue) {

		this(id, name, width, null, useDefault, defaultValue);

	}

	/**
	 * 
	 * constructeur sans l'argument id
	 * 
	 * 
	 * 
	 * @param name
	 * 
	 * @param width
	 * 
	 * @param title
	 * 
	 * @param useDefault
	 * 
	 * @param defaultValue
	 */

	public DefaultRadioButtonFilter(String name, int width, String title,

	boolean useDefault, boolean defaultValue) {

		this(name, name, width, title, useDefault, defaultValue);

	}

	public DefaultRadioButtonFilter(String id, String name, int width,

	String title, boolean useDefault, Boolean defaultValue) {

		super();

		this.id = id;

		this.name = name;

		radioButton = new JRadioButton(title == null ? "" : title);

		radioButton.setPreferredSize(new Dimension(width, radioButton

		.getPreferredSize().height));

		if (useDefault) {

			defaultRadioButton = new JRadioButton(title == null ? "Coche"

			: title);

			defaultRadioButton.setPreferredSize(new Dimension(width,

			defaultRadioButton.getPreferredSize().height));

			setDefaultValue(defaultValue);

		}

		initialize();

	}

	@Override
	public JComponent getComponent() {

		return radioButton;

	}

	@Override
	public String getId() {

		return id;

	}

	@Override
	public String getName() {

		return name;

	}

	@Override
	public Serializable getValue() {

		return radioButton.isSelected();

	}

	@Override
	public void setValue(Object o) {

		if (o != null) {

			radioButton.setSelected((Boolean) o);

		} else {

			radioButton.setSelected(false);

		}

	}

	@Override
	public Serializable getDefaultValue() {

		if (radioButton != null) {

			return radioButton.isSelected();

		}

		return null;

	}

	@Override
	public JComponent getDefaultComponent() {

		return defaultRadioButton;

	}

	@Override
	public void setDefaultValue(Object o) {

		if (defaultRadioButton != null) {

			if (o != null) {

				defaultRadioButton.setSelected((Boolean) o);

			} else {

				defaultRadioButton.setSelected(false);

			}

		}

	}

	@Override
	public void copyDefaultValue() {

		if (defaultRadioButton != null) {

			radioButton.setSelected(defaultRadioButton.isSelected());

		}

	}

	/*
	 * 
	 * le ButtonGroup n'étant pas géré par le JSearchHeader, on n'a pas besoin
	 * 
	 * de créer une classe DefaultRadioButtonGroupFilter
	 * 
	 * 
	 * 
	 * C'est le composant lui-méme qui s'enregistre au groupe par le biais de
	 * 
	 * cette méthode; il suffit donc de créer le groupe et de le passer au
	 * 
	 * composant.
	 * 
	 * 
	 * 
	 * Exemple d'utilisation :
	 * 
	 * private DefaultRadioButtonFilter shortMissionFilter;
	 * 
	 * private DefaultRadioButtonFilter shortDepartDesserteFilter;
	 * 
	 * private ButtonGroup group;
	 * 
	 * 
	 * 
	 * shortMissionFilter = new DefaultRadioButtonFilter("triMission",
	 * 
	 * "Tri croissant des missions");
	 * 
	 * shortMissionFilter.setDefaultValue(true);
	 * 
	 * 
	 * 
	 * shortDepartDesserteFilter = new DefaultRadioButtonFilter( "triDepart",
	 * 
	 * "Tri croissant par départ desserte");
	 * 
	 * shortDepartDesserteFilter.setDefaultValue(false);
	 * 
	 * 
	 * 
	 * group = new ButtonGroup();
	 * 
	 * shortMissionFilter.radioButtonsJoinedInAGroup(group);
	 * 
	 * shortDepartDesserteFilter.radioButtonsJoinedInAGroup(group);
	 */

	/**
	 * 
	 * C'est le composant lui-méme qui s'enregistre au groupe par le biais de
	 * 
	 * cette méthode; il suffit de créer le groupe et de le passer au
	 * 
	 * composant.
	 * 
	 * 
	 * 
	 * @param buttonGroup
	 */

	public void radioButtonsJoinedInAGroup(ButtonGroup buttonGroup) {

		if (radioButton != null) {

			buttonGroup.add(radioButton);

		} else {

			buttonGroup.add(defaultRadioButton);

		}

	}
}
