package com.synaptix.taskmanager.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.auth.helper.IAuth;
import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.component.controller.dialog.AbstractSearchEntityDialogController;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.core.controller.IController;
import com.synaptix.widget.crud.controller.AbstractCRUDManagementController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;

public abstract class AbstractClientModule extends AbstractModule {

	private Multibinder<IController> controllerMultibinder;

	private MapBinder<Class<? extends IController>, IAuth> authMapBinder;

	protected final Multibinder<IController> getControllerMultibinder() {
		if (controllerMultibinder == null) {
			controllerMultibinder = Multibinder.newSetBinder(binder(), IController.class);
		}
		return controllerMultibinder;
	}

	protected final MapBinder<Class<? extends IController>, IAuth> getAuthMapBinder() {
		if (authMapBinder == null) {
			authMapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<? extends IController>>() {
			}, new TypeLiteral<IAuth>() {
			});
		}
		return authMapBinder;
	}

	/**
	 * Add CRUD Management
	 * 
	 * @param controllerClass
	 * @param viewDescriptorClass
	 */
	@Deprecated
	// TODO marche pas
	protected final <E extends IEntity, G extends IEntity> void addCRUDManagement(final Class<? extends AbstractCRUDManagementController<?, E, G>> controllerClass,
			final Class<? extends ICRUDManagementViewDescriptor<G>> viewDescriptorClass) {
		install(new PrivateModule() {
			@Override
			protected void configure() {
				bind(controllerClass).in(Singleton.class);
				expose(controllerClass);

				bind(new TypeLiteral<ICRUDManagementViewDescriptor<G>>() {
				}).to(viewDescriptorClass);
			}
		});
	}

	/**
	 * Add Search entity dialog
	 * 
	 * @param controllerClass
	 * @param viewDescriptorClass
	 */
	@Deprecated
	// TODO marche pas
	protected final <E extends IEntity, G extends IEntity> void addSearchEntityDialog(final Class<? extends AbstractSearchEntityDialogController<?, E, G>> controllerClass,
			final Class<? extends ISearchComponentsDialogViewDescriptor<G>> viewDescriptorClass) {
		install(new PrivateModule() {
			@Override
			protected void configure() {
				bind(controllerClass).in(Singleton.class);
				expose(controllerClass);

				bind(new TypeLiteral<ISearchComponentsDialogViewDescriptor<G>>() {
				}).to(viewDescriptorClass);
			}
		});
	}

	@Deprecated
	// TODO marche pas
	protected final <E extends IEntity> void addCRUDDialog(final Class<? extends AbstractCRUDDialogController<E>> controllerClass, final Key<? extends IBeanExtensionDialogView<E>>... dialogViewKeys) {
		install(new PrivateModule() {
			@Override
			protected void configure() {
				bind(controllerClass);
				expose(controllerClass);

				if (dialogViewKeys != null && dialogViewKeys.length > 0) {
					for (Key<? extends IBeanExtensionDialogView<E>> dialogViewKey : dialogViewKeys) {
						if (dialogViewKey.getAnnotation() != null) {
							bind(Key.get(new TypeLiteral<IBeanExtensionDialogView<E>>() {
							}, dialogViewKey.getAnnotation())).to(dialogViewKey.getTypeLiteral());
						} else if (dialogViewKey.getAnnotationType() != null) {
							bind(Key.get(new TypeLiteral<IBeanExtensionDialogView<E>>() {
							}, dialogViewKey.getAnnotationType())).to(dialogViewKey.getTypeLiteral());
						} else {
							bind(Key.get(new TypeLiteral<IBeanExtensionDialogView<E>>() {
							})).to(dialogViewKey.getTypeLiteral());
						}
					}
				}
			}
		});
	}
}
