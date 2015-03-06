package com.synaptix.widget.path.view.swing;

import org.joda.time.Duration;
import org.joda.time.base.BaseLocal;

public abstract class AbstractBaseLocalNode<E extends BaseLocal> {

	protected String name;

	protected E baseLocal;

	protected Duration pause;

	public AbstractBaseLocalNode(String name, E baseLocal, Duration pause) {
		super();
		this.name = name;
		this.baseLocal = baseLocal;
		this.pause = pause;
	}

	public String getName() {
		return name;
	}

	public E getTime() {
		return baseLocal;
	}

	public Duration getPause() {
		return pause;
	}
}
