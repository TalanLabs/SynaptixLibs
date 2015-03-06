package com.synaptix.classmodel.annotation.processor;

import org.jannocessor.model.JavaElement;

public interface IGenerator<E extends JavaElement> {

	public void generateCodeFrom(E javaElement, IGeneratorContext context);

}
