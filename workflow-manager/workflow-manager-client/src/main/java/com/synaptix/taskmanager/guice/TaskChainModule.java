package com.synaptix.taskmanager.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.synaptix.taskmanager.controller.TaskChainsManagementController;
import com.synaptix.taskmanager.controller.context.TaskChainSearchDialogContext;
import com.synaptix.taskmanager.controller.context.TaskChainSearchFieldWidgetContext;
import com.synaptix.taskmanager.controller.dialog.search.SearchTaskChainDialogController;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.view.swing.descriptor.TaskChainPanelDescriptor;
import com.synaptix.taskmanager.view.swing.dialog.descriptor.SearchTaskChainDialogPanelDescriptor;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;

public final class TaskChainModule extends PrivateModule {

	@Override
	protected void configure() {

		bind(TaskChainsManagementController.class).in(Singleton.class);
		expose(TaskChainsManagementController.class);

		install(new FactoryModuleBuilder().implement( //
				SearchTaskChainDialogController.class, //
				SearchTaskChainDialogController.class).build( //
				SearchTaskChainDialogController.Factory.class));
		expose(SearchTaskChainDialogController.Factory.class);

		bind(new TypeLiteral<ISearchComponentsDialogViewDescriptor<ITaskChain>>() {
		}).to(SearchTaskChainDialogPanelDescriptor.class);

		bind(new TypeLiteral<ICRUDManagementViewDescriptor<ITaskChain>>() {
		}).to(TaskChainPanelDescriptor.class);

		install(new FactoryModuleBuilder().build(TaskChainSearchFieldWidgetContext.Factory.class));
		expose(TaskChainSearchFieldWidgetContext.Factory.class);

		install(new FactoryModuleBuilder().build(TaskChainSearchDialogContext.Factory.class));
		expose(TaskChainSearchDialogContext.Factory.class);

	}

}
