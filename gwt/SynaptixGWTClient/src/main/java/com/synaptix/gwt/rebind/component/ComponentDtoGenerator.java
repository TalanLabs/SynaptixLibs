package com.synaptix.gwt.rebind.component;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public class ComponentDtoGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		TypeOracle typeOracle = context.getTypeOracle();
		String packageName = null;
		String className = null;
		try {
			JClassType classType = typeOracle.getType(typeName);
			packageName = classType.getPackage() != null ? classType.getPackage().getName() : null;
			className = rename(classType.getSimpleSourceName()) + "Impl";
		} catch (Exception e) {
			logger.log(TreeLogger.ERROR, "typeName ERROR Impl not found", e);

		}
		return packageName != null ? packageName + "." + className : className;
	}

	private String rename(String name) {
		String res = name;
		if (name.length() > 2) {
			String second = name.substring(1, 2);
			boolean startI = name.substring(0, 1).equals("I") && second.toUpperCase().equals(second);
			if (startI) {
				res = name.substring(1);
			}
		}
		return res;
	}
}
