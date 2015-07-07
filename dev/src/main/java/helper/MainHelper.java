package helper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import mapper.CountryMapper;
import mapper.ZipMapper;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.factory.DefaultComputedFactory;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsLocaleSession;
import com.synaptix.constants.IConstantsLocaleSession;
import com.synaptix.entity.IdRaw;
import com.synaptix.entity.extension.BusinessComponentExtensionProcessor;
import com.synaptix.entity.extension.CacheComponentExtensionProcessor;
import com.synaptix.entity.extension.DatabaseComponentExtensionProcessor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.SynaptixMyBatisServer;
import com.synaptix.mybatis.dao.IDaoUserContext;
import com.synaptix.mybatis.guice.AbstractSynaptixMyBatisModule;
import com.synaptix.mybatis.guice.SynaptixMyBatisModule;
import com.synaptix.prefs.FakeSyPreferences;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.server.service.GuiceServerServiceFactory;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.utils.Manager;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.guice.SwingConstantsBundleManager;
import com.synaptix.widget.guice.SynaptixWidgetModule;
import com.synaptix.widget.skin.SubstanceFlatWhiteLookAndFeel;
import com.synaptix.widget.view.swing.helper.IconHelper;

public class MainHelper {

	// private static final Log LOG = LogFactory.getLog(MainHelper.class);

	private static Injector clientInjector;

	private static Injector myBatisInjector;

	private static final void initLookAndFeel() {
		try {
			// UIManager.setLookAndFeel(new GabyLookAndFeel());

			UIManager.setLookAndFeel(new SubstanceFlatWhiteLookAndFeel());
			UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE, new WidestComboPopupPrototype());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		} catch (UnsupportedLookAndFeelException e) {
			Manager.installLookAndFeelWindows();
		}

		JDialogModel.setActiveSave(true);
		JDialogModel.setMultiScreen(true);

		IconHelper.setUseFontAwesome(true);
	}

	private static final void initSyPreferences() {
		SyPreferences.setPreferences(new FakeSyPreferences());
	}

	public static final void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		clientInjector = Guice.createInjector(new MyClientModule());

		initLookAndFeel();
		initSyPreferences();
	}

	public static JComponent createEmptyComponent() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.red);
		return panel;
	}

	public static JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 800);
		// AWTUtilitiesWrapper.setWindowOpaque(frame, false);

		return frame;
	}

	public static void initMyBatis() {
		ComponentFactory.getInstance().addExtension(IDatabaseComponentExtension.class, new DatabaseComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(IBusinessComponentExtension.class, new BusinessComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(ICacheComponentExtension.class, new CacheComponentExtensionProcessor());
		ComponentFactory.getInstance().setComputedFactory(new DefaultComputedFactory());

		myBatisInjector = Guice.createInjector(new MyBootModule());

		SynaptixMyBatisServer synaptixMyBatisServer = myBatisInjector.getInstance(SynaptixMyBatisServer.class);

		synaptixMyBatisServer.start();

		ServicesManager.getInstance().addServiceFactory("test", myBatisInjector.getInstance(GuiceServerServiceFactory.class));
	}

	public static Injector getMyBatisInjector() {
		return myBatisInjector;
	}

	public static Injector getClientInjector() {
		return clientInjector;
	}

	public static JComponent createTable() {
		JTable table = new JTable(new DefaultTableModel(100, 10) {
			@Override
			public Object getValueAt(int row, int column) {
				return row + " " + column;
			}
		});
		table.setPreferredScrollableViewportSize(new Dimension(0, 200));

		FormLayout layout = new FormLayout("FILL:PREF:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append(ButtonBarFactory.buildLeftAlignedBar(new JButton(new MyAction()), new JButton(new MyAction()), new JButton(new MyAction())));
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("FILL:PREF:GROW(1.0)");
		builder.nextLine(2);
		builder.append(new JScrollPane(table));
		JButton test = new JButton("Test");
		test.setEnabled(false);
		builder.append(ButtonBarFactory.buildLeftAlignedBar(test));
		return builder.getPanel();
	}

	private static class MyClientModule extends AbstractModule {

		@Override
		protected void configure() {
			install(new SynaptixWidgetModule());

			bind(IConstantsLocaleSession.class).to(DefaultConstantsLocaleSession.class).in(Singleton.class);
			bind(ConstantsBundleManager.class).annotatedWith(SwingConstantsBundleManager.class).to(DefaultConstantsBundleManager.class).in(Singleton.class);
		}
	}

	private static class MyBootModule extends AbstractModule {

		@Override
		protected void configure() {
			install(new SynaptixMyBatisModule(MainHelper.class.getResourceAsStream("/mybatis-config.xml"), DatabaseUserSession.class));

			bind(DatabaseUserSession.class).in(Singleton.class);

			bind(GuiceServerServiceFactory.class).in(Singleton.class);
			bind(IServiceFactory.class).to(GuiceServerServiceFactory.class).in(Singleton.class);

			install(new AbstractSynaptixMyBatisModule() {
				@Override
				protected void configure() {
					addMapperClass(CountryMapper.class);
					addMapperClass(ZipMapper.class);
				}
			});
		}
	}

	public static class DatabaseUserSession implements IDaoUserContext {

		private Serializable idUser = new IdRaw("E783ACB641D54922AC6C4309CDC4D668");

		private Locale locale = Locale.ENGLISH;

		@Override
		public Serializable getCurrentIdUser() {
			return idUser;
		}

		@Override
		public String getCurrentLogin() {
			return "demo";
		}

		@Override
		public Locale getCurrentLocale() {
			return locale;
		}

		public void setIdUser(Serializable idUser) {
			this.idUser = idUser;
		}

		public void setLocale(Locale locale) {
			this.locale = locale;
		}
	}

	private static class MyAction extends AbstractAddAction {
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
}
