package service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.synaptix.auth.AuthsBundle;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ServiceAuth {

	public @interface Auth {

		public Class<? extends AuthsBundle> bundleClass();

		public String object();

		public String action();

	}

	public Auth[] value();

}
