package com.synaptix.classmodel.annotation.processor;

import org.jannocessor.model.structure.AbstractJavaStructure;

public interface IGeneratorContext {

	public void generateCode(AbstractJavaStructure model);

}
