package com.synaptix.client.ref.view.swing.descriptor;

import model.CountryFields;
import model.ICountry;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.widget.component.view.swing.descriptor.DefaultComponentsManagementPanelDescriptor;

import constants.CountryTableConstantsBundle;

public class CountryPanelDescriptor extends DefaultComponentsManagementPanelDescriptor<ICountry> {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(CountryFields.country(), CountryFields.meaning());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(CountryFields.country(), CountryFields.meaning(), CountryFields.isoCountryCode(),
			CountryFields.isoCountryNo());

	public CountryPanelDescriptor(CountryTableConstantsBundle ta) {
		super(null, ta, FILTER_COLUMNS, TABLE_COLUMNS);
	}
}
