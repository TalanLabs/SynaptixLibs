package com.synaptix.gwt.rebind.constants;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.user.rebind.SourceWriter;

public class ConstantsWithLookingGWTBundleImplCreator extends ConstantsGWTBundleImplCreator {

	public ConstantsWithLookingGWTBundleImplCreator(TreeLogger logger, GeneratorContext context, JClassType constantsType, boolean use18nFilter) {
		super(logger, context, constantsType, use18nFilter);
	}

	protected void generateCodeProxyMethod(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsWithLookingGWTBundleMethod cbm = ConstantsWithLookingGWTBundleMethod.which(method);
		if (cbm != null) {
			switch (cbm) {
			case GET_STRING:
				generateGetString(w, method);
				break;
			case GET_STRING_ARGS:
				generateGetStringArgs(w, method);
				break;
			case GET_BOOLEAN:
				generateGetBoolean(w, method);
				break;
			case GET_DOUBLE:
				generateGetDouble(w, method);
				break;
			case GET_FLOAT:
				generateGetFloat(w, method);
				break;
			case GET_INT:
				generateGetInt(w, method);
				break;
			case GET_STRING_ARRAY:
				generateGetStringArray(w, method);
				break;
			case GET_MAP:
				generateGetMap(w, method);
				break;
			case OTHER:
				super.generateCodeProxyMethod(w, method);
				break;
			}
		} else {
			super.generateCodeProxyMethod(w, method);
		}
	}

	protected void generateGetString(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getStringValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetStringArgs(SourceWriter w, JMethod method) throws UnableToCompleteException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		JParameter[] asyncParams = method.getParameters();
		for (int i = 1; i < asyncParams.length; ++i) {
			JParameter param = asyncParams[i];
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			String paramName = param.getName();
			sb.append(paramName);
		}
		w.println("return _getStringArgsValue_(%s,%s);", method.getParameters()[0].getName(), sb.toString());
	}

	protected void generateGetBoolean(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getBooleanValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetDouble(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getDoubleValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetFloat(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getFloatValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetInt(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getIntegerValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetStringArray(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getStringArrayValue_(%s);", method.getParameters()[0].getName());
	}

	protected void generateGetMap(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getStringMapValue_(%s);", method.getParameters()[0].getName());
	}
}
