package com.synaptix.entity.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.synaptix.component.IComponent;

public interface ICacheComponentExtension {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Cache {

		/**
		 * Si true alors l'objet mis en cache et récupéré seront la même
		 * instance. Plus rapide.
		 * 
		 * @return
		 */
		public boolean readOnly() default false;

		public int size() default 512;

		public long clearInterval() default 60 * 60 * 1000; // 1 hour

		/**
		 * Mettre d'autres component en lien, quand ils seont flushé, l'objet le
		 * sera aussi
		 * 
		 * @return
		 */
		public Class<? extends IComponent>[] links() default {};

	}
}
