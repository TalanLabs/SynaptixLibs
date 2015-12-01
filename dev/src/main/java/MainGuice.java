import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;

import helper.MainHelper;

public class MainGuice {

	public static void main(String[] args) throws Exception {
		MainHelper.init();

		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Parent.class).in(Singleton.class);
			}
		});

		Injector child1 = injector.createChildInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Fils.class).in(Singleton.class);
			}
		});

		Injector child2 = injector.createChildInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Fils.class).in(Singleton.class);
			}
		});

		Fils fils1 = child1.getInstance(Fils.class);
		fils1.bonjour();

		Fils fils2 = child2.getInstance(Fils.class);
		fils2.bonjour();
	}

	private static class MyModule0 extends AbstractModule {

		@Override
		protected void configure() {

			bind(Fils.class).in(Singleton.class);
		}
	}

	private static class MyModule1 extends PrivateModule {

		@Override
		protected void configure() {
			install(new MyModule0());
			bind(Fils.class).annotatedWith(My1.class).to(Fils.class).in(Singleton.class);
			// expose(Fils.class).annotatedWith(My1.class);
			// expose(Fils.class);
		}
	}

	private static class MyModule2 extends PrivateModule {

		@Override
		protected void configure() {
			install(Modules.override(new MyModule0()).with(new AbstractModule() {
				@Override
				protected void configure() {
					bind(Key.get(Fils.class, My2.class)).in(Singleton.class);
				}
			}));
			// install(new MyModule0());
			expose(Fils.class).annotatedWith(My2.class);
		}
	}

	private static class Parent {

		@Inject
		private Injector injector;

		public void hello() {
			System.out.println("Hello " + hashCode() + " injector " + injector.hashCode());
		}
	}

	private static class Fils {

		@Inject
		private Parent parent;

		@Inject
		private Injector injector;

		public void bonjour() {
			System.out.println("Bonjour " + hashCode() + " injector " + injector.hashCode());
			parent.hello();
		}
	}

	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	public @interface My1 {

	}

	@BindingAnnotation
	@Retention(RetentionPolicy.RUNTIME)
	public @interface My2 {

	}
}
