import helper.MainHelper;

import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import model.CountryBuilder;
import model.CountryFields;
import model.ICountry;

import com.google.inject.Key;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.service.IServiceFactory;
import com.synaptix.swing.utils.Manager;
import com.synaptix.widget.component.controller.context.AbstractSearchComponentsContext;
import com.synaptix.widget.component.view.swing.DefaultTablePageComponentsPanel;
import com.synaptix.widget.guice.SwingConstantsBundleManager;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.IViewDescriptor;
import com.synaptix.widget.view.swing.SwingSynaptixViewFactory;
import com.synaptix.widget.view.swing.tablemodel.field.CompositeField;

import constants.CountryTableConstantsBundle;

public class MainCountryTable {

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(CountryFields.isoCountryCode(),
			CompositeField.newBuilder(CountryFields.isoCountryNo(), CountryFields.isoCountryCode(), CountryFields.country()).format("%s - (%s -> %s)").build());

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Manager.setAutoSaveExportExcel(true);

				MainHelper.init();

				ConstantsBundleManager constantsBundleManager = MainHelper.getClientInjector().getInstance(Key.get(ConstantsBundleManager.class, SwingConstantsBundleManager.class));
				constantsBundleManager.addBundle(CountryTableConstantsBundle.class);
				CountryTableConstantsBundle ta = constantsBundleManager.getBundle(CountryTableConstantsBundle.class);

				Default context = new Default();

				DefaultTablePageComponentsPanel<ICountry> d = new DefaultTablePageComponentsPanel<ICountry>(context, ICountry.class, ta, TABLE_COLUMNS);

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add(d);
				frame.setVisible(true);

				System.out.println(d.getTable().getYColumnModel().getColumnCount());

				System.out.println(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().actions());

				ICountry country = new CountryBuilder().isoCountryNo("10").isoCountryCode("FRA").country("France").build();

				d.setComponents(Arrays.asList(country));
			}
		});
	}

	private static final class Default extends AbstractSearchComponentsContext<ISynaptixViewFactory, ICountry> {

		public Default() {
			super(new SwingSynaptixViewFactory(), ICountry.class);
		}

		@Override
		protected IViewDescriptor<ICountry> createViewDescriptor() {
			return null;
		}

		@Override
		protected IServiceFactory getServiceFactory() {
			return null;
		}
	}
}
