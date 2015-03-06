package com.synaptix.gwt.rebind.constants;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public class ConstantsGWTBundleGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);
		JClassType constantsType = typeOracle.findType(typeName);
		if (constantsType == null) {
			logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + constantsType + "'", null);
			throw new UnableToCompleteException();
		}

		if (constantsType.isInterface() == null) {
			logger.log(TreeLogger.ERROR, constantsType.getQualifiedSourceName() + " is not an interface", null);
			throw new UnableToCompleteException();
		}

		boolean use18nFilter = false;
		try {
			SelectionProperty cp = context.getPropertyOracle().getSelectionProperty(logger, "com.synaptix.gwt.constants.useI18nFilter");
			use18nFilter = Boolean.parseBoolean(cp.getCurrentValue());
		} catch (BadPropertyValueException e) {
			throw new UnableToCompleteException();
		}

		JClassType withType = typeOracle.findType("com.synaptix.constants.shared.ConstantsWithLookingBundle");

		TreeLogger proxyLogger = logger.branch(TreeLogger.DEBUG, "Generating client proxy for constants GWT bundle '" + constantsType.getQualifiedSourceName() + "'", null);

		ConstantsGWTBundleImplCreator creator;
		if (withType.isAssignableFrom(constantsType)) {
			creator = new ConstantsWithLookingGWTBundleImplCreator(proxyLogger, context, constantsType, use18nFilter);
		} else {
			creator = new ConstantsGWTBundleImplCreator(proxyLogger, context, constantsType, use18nFilter);
		}

		return creator.create();
	}
}
