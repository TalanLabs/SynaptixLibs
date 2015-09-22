package workflow;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.factory.DefaultComputedFactory;
import com.synaptix.entity.extension.BusinessComponentExtensionProcessor;
import com.synaptix.entity.extension.CacheComponentExtensionProcessor;
import com.synaptix.entity.extension.DatabaseComponentExtensionProcessor;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.SynaptixMyBatisServer;
import com.synaptix.mybatis.guice.AbstractSynaptixMyBatisModule;
import com.synaptix.mybatis.guice.SynaptixMyBatisModule;
import com.synaptix.server.service.GuiceServerServiceFactory;
import com.synaptix.server.service.guice.AbstractSynaptixServerServiceModule;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.guice.ServerTaskManagerModule;
import com.synaptix.taskmanager.guice.child.AbstractTaskManagerModule;
import com.synaptix.taskmanager.service.ITodoService;

import helper.MainHelper;
import helper.MainHelper.DatabaseUserSession;
import workflow.handler.ClassTypeHandler;
import workflow.handler.URITypeHandler;
import workflow.tasks.CANTaskService;
import workflow.tasks.CLOTaskService;
import workflow.tasks.TCOTaskService;

public class MainWorkflow {

	public static void main(String[] args) {
		ComponentFactory.getInstance().addExtension(IDatabaseComponentExtension.class, new DatabaseComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(IBusinessComponentExtension.class, new BusinessComponentExtensionProcessor());
		ComponentFactory.getInstance().addExtension(ICacheComponentExtension.class, new CacheComponentExtensionProcessor());
		ComponentFactory.getInstance().setComputedFactory(new DefaultComputedFactory());

		Injector myBatisInjector = Guice.createInjector(new MyBootModule());

		SynaptixMyBatisServer synaptixMyBatisServer = myBatisInjector.getInstance(SynaptixMyBatisServer.class);

		synaptixMyBatisServer.start();

		ServicesManager.getInstance().addServiceFactory("test", myBatisInjector.getInstance(GuiceServerServiceFactory.class));
	}

	private static class MyBootModule extends AbstractModule {

		@Override
		protected void configure() {
			install(new SynaptixMyBatisModule(MainHelper.class.getResourceAsStream("/mybatis-config.xml"), DatabaseUserSession.class));
			install(new ServerTaskManagerModule());

			bind(DatabaseUserSession.class).in(Singleton.class);

			bind(GuiceServerServiceFactory.class).in(Singleton.class);
			bind(IServiceFactory.class).to(GuiceServerServiceFactory.class).in(Singleton.class);

			install(new AbstractSynaptixMyBatisModule() {
				@Override
				protected void configure() {
					addTypeHandler(ClassTypeHandler.class);
					addTypeHandler(URITypeHandler.class);
				}
			});

			install(new AbstractSynaptixServerServiceModule() {

				@Override
				protected void configure() {
					bindService(TodoServerService.class).with(ITodoService.class);
				}
			});

			install(new AbstractTaskManagerModule() {

				@Override
				protected void configure() {
					bindObjectTypeTaskFactory(ICustomerOrder.class, CustomerOrderObjectTypeTaskFactory.class);

					bindTaskService(TCOTaskService.class);
					bindTaskService(CLOTaskService.class);
					bindTaskService(CANTaskService.class);
				}
			});
		}
	}
}
