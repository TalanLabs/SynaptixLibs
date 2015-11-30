import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.auth.DefaultAuthsBundleManager;

import auth.MyAuthsBundle;
import auth.MyAuthsBundleMethods;
import service.IGabyService;
import service.impl.AuthPrecondition;
import service.impl.GabyServerService;
import service.impl.IPrecondition;
import service.impl.Precondition;

public class MainGuiceService {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new MyModule());

		DefaultAuthsBundleManager authsBundleManager = injector
				.getInstance(DefaultAuthsBundleManager.class);
		authsBundleManager.addBundle(MyAuthsBundle.class);
		authsBundleManager.putAuthValue(MyAuthsBundleMethods.hasTestRead());

		IGabyService gabyService = injector.getInstance(IGabyService.class);
		System.out.println(gabyService.whatYourName());
		System.out.println(gabyService.whatYourName());
	}

	private static class MyModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(DefaultAuthsBundleManager.class).in(Singleton.class);
			bind(AuthsBundleManager.class).to(DefaultAuthsBundleManager.class)
					.in(Singleton.class);

			bind(GabyServerService.class).in(Singleton.class);
			bind(IGabyService.class).to(GabyServerService.class).in(
					Singleton.class);

			PreconditionMethodInterceptor methodIn = new PreconditionMethodInterceptor();
			requestInjection(methodIn);
			bindInterceptor(Matchers.subclassesOf(GabyServerService.class),
					Matchers.annotatedWith(Precondition.class), methodIn);
		}

		@Provides
		@Singleton
		AuthPrecondition provideAuthPrecondition(
				AuthsBundleManager authsBundleManager) {
			return new AuthPrecondition(authsBundleManager);
		}
	}

	private static class PreconditionMethodInterceptor implements
			MethodInterceptor {

		@Inject
		private Injector injector;

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Precondition preconditionAnno = invocation.getMethod()
					.getAnnotation(Precondition.class);
			Class<? extends IPrecondition>[] pClasss = preconditionAnno.value();
			int i = 0;
			boolean ok = true;
			while (i < pClasss.length && ok) {
				Class<? extends IPrecondition> pClass = pClasss[i];
				IPrecondition p = injector.getInstance(pClass);
				ok = p.process(invocation.getMethod(),
						invocation.getArguments());
				i++;
			}
			if (!ok) {
				throw new IllegalAccessException();
			}
			return invocation.proceed();
		}
	}
}
