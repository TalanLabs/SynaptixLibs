package com.synaptix.mybatis.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

import com.synaptix.service.filter.AbstractNode;

public abstract class AbstractNodeProcess<E extends AbstractNode> {

	public AbstractNodeProcess() {
		super();
	}

	public abstract String process(IFilterContext context, E node);

	public abstract Map<String, Object> buildValueFilterMap(IFilterContext context, E node);

	public abstract Set<String> buildPropertyNames(IFilterContext context, E node);

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@interface NodeProcess {

		public Class<? extends AbstractNode> value();

	}
}
