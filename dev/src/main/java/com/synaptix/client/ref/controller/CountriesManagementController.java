package com.synaptix.client.ref.controller;

import com.google.inject.Key;
import com.synaptix.client.ref.view.swing.descriptor.CountryPanelDescriptor;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.crud.controller.AbstractComponentsManagementController;
import com.synaptix.widget.guice.SwingConstantsBundleManager;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.swing.SwingSynaptixViewFactory;

import constants.CountryTableConstantsBundle;
import helper.MainHelper;
import model.ICountry;

public class CountriesManagementController extends AbstractComponentsManagementController<ISynaptixViewFactory, ICountry> {

	public CountriesManagementController() {
		super(new SwingSynaptixViewFactory(), ICountry.class);
	}

	@Override
	protected IComponentsManagementViewDescriptor<ICountry> createComponentsManagementViewDescriptor() {
		ConstantsBundleManager constantsBundleManager = MainHelper.getClientInjector().getInstance(Key.get(ConstantsBundleManager.class, SwingConstantsBundleManager.class));
		constantsBundleManager.addBundle(CountryTableConstantsBundle.class);
		CountryTableConstantsBundle ta = constantsBundleManager.getBundle(CountryTableConstantsBundle.class);
		return new CountryPanelDescriptor(ta);
	}

	@Override
	protected IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("test");
	}
}
