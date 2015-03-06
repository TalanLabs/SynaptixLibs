package com.synaptix.gwt.rebind.constants;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.synaptix.constants.shared.ConstantsBundle;

public class ConstantsGWTBundleImplCreator {

	protected static final String PROXY_SUFFIX = "_Impl";

	protected final TreeLogger logger;

	protected final GeneratorContext context;

	protected final JClassType constantsType;

	protected final boolean useI18nFilter;

	public ConstantsGWTBundleImplCreator(TreeLogger logger, GeneratorContext context, JClassType constantsType, boolean useI18nFilter) {
		super();

		this.logger = logger;
		this.context = context;
		this.constantsType = constantsType;
		this.useI18nFilter = useI18nFilter;
	}

	public String create() throws UnableToCompleteException {
		SourceWriter srcWriter = getSourceWriter();
		if (srcWriter == null) {
			return getProxyQualifiedName();
		}

		srcWriter.println("private static String BUNDLE_NAME = \"%s\";", getBundleName());

		generateProperties(srcWriter);
		generateDefaultMethods(srcWriter);

		generateMethods(srcWriter);

		srcWriter.commit(logger);

		return getProxyQualifiedName();
	}

	protected SourceWriter getSourceWriter() {
		JPackage serviceIntfPkg = constantsType.getPackage();
		String packageName = serviceIntfPkg == null ? "" : serviceIntfPkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());
		if (printWriter == null) {
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = new String[] {};
		for (String imp : imports) {
			composerFactory.addImport(imp);
		}

		composerFactory.addImplementedInterface(constantsType.getErasedType().getQualifiedSourceName());

		return composerFactory.createSourceWriter(context, printWriter);
	}

	protected String getProxyQualifiedName() {
		String p = constantsType.getPackage() != null ? constantsType.getPackage().getName() : null;
		return p == null || p.length() == 0 ? getProxySimpleName() : p + "." + getProxySimpleName();
	}

	protected String getProxySimpleName() {
		return constantsType.getName() + PROXY_SUFFIX;
	}

	protected void generateProperties(SourceWriter w) throws UnableToCompleteException {
		w.println("protected static java.util.Map<String,String> properties = new java.util.HashMap<String,String>();");

		w.println("static {");
		w.indent();
		for (JMethod method : constantsType.getOverridableMethods()) {
			generateDefaultValueProxyMethod(w, method);
		}

		w.outdent();
		w.println("}");
	}

	protected void generateDefaultValueProxyMethod(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsGWTBundleMethod cbm = ConstantsGWTBundleMethod.which(method);
		if (cbm != null) {
			switch (cbm) {
			case CALL_STRING:
				generateDefaultValueString(w, method);
				break;
			case CALL_STRING_ARGS:
				generateDefaultValueString(w, method);
				break;
			case CALL_BOOLEAN:
				generateDefaultValueBoolean(w, method);
				break;
			case CALL_DOUBLE:
				generateDefaultValueDouble(w, method);
				break;
			case CALL_FLOAT:
				generateDefaultValueFloat(w, method);
				break;
			case CALL_INT:
				generateDefaultValueInt(w, method);
				break;
			case CALL_MAP:
				generateDefaultValueMap(w, method);
				break;
			case CALL_STRINGARRAY:
				generateDefaultValueStringArray(w, method);
				break;
			case OTHER:
				break;
			}
		}

	}

	protected void generateDefaultValueString(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultStringValue d = getMethodAnnotation(method, ConstantsBundle.DefaultStringValue.class);
		if (d != null && d.value() != null) {
			printAddDefaultValueMap(w, getKey(method), d.value());
		}
	}

	protected void generateDefaultValueBoolean(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultBooleanValue d = getMethodAnnotation(method, ConstantsBundle.DefaultBooleanValue.class);
		if (d != null) {
			printAddDefaultValueMap(w, getKey(method), String.valueOf(d.value()));
		}
	}

	protected void generateDefaultValueDouble(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultDoubleValue d = getMethodAnnotation(method, ConstantsBundle.DefaultDoubleValue.class);
		if (d != null) {
			printAddDefaultValueMap(w, getKey(method), String.valueOf(d.value()));
		}
	}

	protected void generateDefaultValueFloat(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultFloatValue d = getMethodAnnotation(method, ConstantsBundle.DefaultFloatValue.class);
		if (d != null) {
			printAddDefaultValueMap(w, getKey(method), String.valueOf(d.value()));
		}
	}

	protected void generateDefaultValueInt(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultIntValue d = getMethodAnnotation(method, ConstantsBundle.DefaultIntValue.class);
		if (d != null) {
			printAddDefaultValueMap(w, getKey(method), String.valueOf(d.value()));
		}
	}

	protected void generateDefaultValueMap(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultStringMapValue d = getMethodAnnotation(method, ConstantsBundle.DefaultStringMapValue.class);
		if (d != null && d.value() != null) {
			String[] keys = new String[d.value().length / 2];
			for (int i = 0; i < d.value().length; i += 2) {
				String key = d.value()[i];
				String value = d.value()[i + 1];

				printAddDefaultValueMap(w, getKey(method) + "." + key, value);

				keys[i / 2] = key;
			}
			printAddDefaultValueMap(w, getKey(method), toStringArray(keys));
			// w.println("properties.put(\"%s\",\"%s\");", getKey(method), toStringArray(keys));
		}
	}

	protected void generateDefaultValueStringArray(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsBundle.DefaultStringArrayValue d = getMethodAnnotation(method, ConstantsBundle.DefaultStringArrayValue.class);
		if (d != null && d.value() != null) {
			printAddDefaultValueMap(w, getKey(method), toStringArray(d.value()));
		}
	}

	protected String getBundleName() {
		JPackage serviceIntfPkg = constantsType.getPackage();
		String name = (serviceIntfPkg == null || serviceIntfPkg.getName().isEmpty() ? "" : serviceIntfPkg.getName() + ".") + constantsType.getName();
		return name;
	}

	protected boolean isUseI18nClass() {
		if (context instanceof StandardGeneratorContext) {
			return useI18nFilter && ((StandardGeneratorContext) context).isProdMode();
		}
		return false;
	}

	protected void printAddDefaultValueMap(SourceWriter w, String name, String value) {
		if (isUseI18nClass()) {
			String v = createI18nClass(name);
			w.println("properties.put(\"%s\",\"%s\");", name, v);
		} else {
			if (value != null) {
				w.println("properties.put(\"%s\",\"%s\");", name, value);
			}
		}
	}

	protected String createI18nClass(String name) {
		return "<i18n class=\\\"${" + getBundleName() + "#" + name + "}\\\"/>";
	}

	protected void generateDefaultMethods(SourceWriter w) throws UnableToCompleteException {
		w.println("protected static java.util.Map<String,java.util.Map<String,String>> stringMapCache = new java.util.HashMap<String,java.util.Map<String,String>>();");
		w.println("protected static java.util.Map<String,String[]> stringArrayCache = new java.util.HashMap<String,String[]>();");

		generateDefaultGetStringValueMethod(w);
		generateDefaultGetStringArgsValueMethod(w);
		generateDefaultGetBooleanValueMethod(w);
		generateDefaultGetIntegerValueMethod(w);
		generateDefaultGetDoubleValueMethod(w);
		generateDefaultGetFloatValueMethod(w);
		generateDefaultGetStringArrayValueMethod(w);
		generateDefaultGetStringMapValueMethod(w);
	}

	protected void generateDefaultGetStringValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final String _getStringValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return new StringBuilder().append(\"!\").append(key).append(\"!\").toString();");
		w.outdent();
		w.println("}");
		w.println("return properties.get(key);");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetStringArgsValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final String _getStringArgsValue_(String key, Object... args) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return new StringBuilder().append(\"!\").append(key).append(\"!\").toString();");
		w.outdent();
		w.println("}");
		w.println("String s = properties.get(key);");
		w.println("if (args != null) {");
		w.indent();
		w.println("for(int i = 0;i<args.length;i++) {");
		w.indent();
		w.println("String delimiter = \"{\" + i + \"}\";");
		w.println("while(s.contains(delimiter)) {");
		w.indent();
		w.println("s = s.replace( delimiter, String.valueOf( args[i] ) );");
		w.outdent();
		w.println("}");
		w.outdent();
		w.println("}");
		w.outdent();
		w.println("}");
		w.println("return s;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetBooleanValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final Boolean _getBooleanValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("String value = properties.get(key);");
		w.println("return value != null ? Boolean.parseBoolean(value) : null;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetIntegerValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final Integer _getIntegerValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("String value = properties.get(key);");
		w.println("return value != null ? Integer.parseInt(value) : null;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetDoubleValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final Double _getDoubleValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("String value = properties.get(key);");
		w.println("return value != null ? Double.parseDouble(value) : null;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetFloatValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final Float _getFloatValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("String value = properties.get(key);");
		w.println("return value != null ? Float.parseFloat(value) : null;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetStringArrayValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final String[] _getStringArrayValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("String[] res = stringArrayCache.get(key);");
		w.println("if (res == null) {");
		w.indent();
		w.println("String value = properties.get(key);");
		w.println("if (value != null) {");
		w.indent();
		w.println("String[] rs = value.split(\",\");");
		w.println("res = new String[rs.length];");
		w.println("for (int i = 0; i < rs.length; i++) {");
		w.indent();
		w.println("res[i] = rs[i].replace(\"\\\\,\", \",\");");
		w.outdent();
		w.println("}");
		w.outdent();
		w.println("}");
		w.println("stringArrayCache.put(key, res);");
		w.outdent();
		w.println("}");
		w.println("return res;");
		w.outdent();
		w.println("}");
	}

	protected void generateDefaultGetStringMapValueMethod(SourceWriter w) throws UnableToCompleteException {
		w.println();
		w.println("protected static final java.util.Map<String, String> _getStringMapValue_(String key) {");
		w.indent();
		w.println("if (properties == null || !properties.containsKey(key)) {");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		w.println("java.util.Map<String, String> res = stringMapCache.get(key);");
		w.println("if (res == null) {");
		w.indent();
		w.println("String map = properties.get(key);");
		w.println("if (map != null) {");
		w.indent();
		w.println("res = new java.util.HashMap<String, String>();");
		w.println("String[] ks = map.split(\",\");");
		w.println("if (ks != null) {");
		w.indent();
		w.println("for (String k : ks) {");
		w.indent();
		w.println("k = k.trim().replace(\"\\\\,\", \",\");");
		w.println("res.put(k, properties.get(new StringBuilder(key).append(\".\").append(k).toString()));");
		w.outdent();
		w.println("}");
		w.outdent();
		w.println("}");
		w.outdent();
		w.println("}");
		w.println("stringMapCache.put(key, res);");
		w.outdent();
		w.println("}");
		w.println("return res;");
		w.outdent();
		w.println("}");
	}

	protected void generateMethods(SourceWriter w) throws UnableToCompleteException {
		for (JMethod method : constantsType.getOverridableMethods()) {
			generateProxyMethod(w, method);
		}
	}

	protected void generateProxyMethod(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println();

		// Write the method signature
		JType returnType = method.getReturnType().getErasedType();
		w.print("public final ");
		w.print(returnType.getQualifiedSourceName());
		w.print(" ");
		w.print(method.getName() + "(");
		boolean needsComma = false;
		JParameter[] asyncParams = method.getParameters();
		for (int i = 0; i < asyncParams.length; ++i) {
			JParameter param = asyncParams[i];
			if (needsComma) {
				w.print(", ");
			} else {
				needsComma = true;
			}
			JType paramType = param.getType();
			paramType = paramType.getErasedType();

			w.print(paramType.getQualifiedSourceName());
			w.print(" ");

			String paramName = param.getName();
			w.print(paramName);
		}

		w.println(") {");
		w.indent();
		generateCodeProxyMethod(w, method);
		w.outdent();
		w.println("}");
	}

	protected void generateCodeProxyMethod(SourceWriter w, JMethod method) throws UnableToCompleteException {
		ConstantsGWTBundleMethod cbm = ConstantsGWTBundleMethod.which(method);
		if (cbm != null) {
			switch (cbm) {
			case CALL_STRING:
				generateString(w, method);
				break;
			case CALL_STRING_ARGS:
				generateStringArgs(w, method);
				break;
			case CALL_BOOLEAN:
				generateBoolean(w, method);
				break;
			case CALL_DOUBLE:
				generateDouble(w, method);
				break;
			case CALL_FLOAT:
				generateFloat(w, method);
				break;
			case CALL_INT:
				generateInt(w, method);
				break;
			case CALL_MAP:
				generateMap(w, method);
				break;
			case CALL_STRINGARRAY:
				generateStringArray(w, method);
				break;
			case OTHER:
				throw new UnableToCompleteException();
			}
		} else {
			throw new UnableToCompleteException();
		}
	}

	protected void generateString(SourceWriter w, JMethod method) throws UnableToCompleteException {
		if (isUseI18nClass()) {
			String v = createI18nClass(getKey(method));
			w.println("return \"%s\";", v);
		} else {
			w.println("return _getStringValue_(\"%s\");", getKey(method));
		}
	}

	protected void generateStringArgs(SourceWriter w, JMethod method) throws UnableToCompleteException {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		JParameter[] asyncParams = method.getParameters();
		for (int i = 0; i < asyncParams.length; ++i) {
			JParameter param = asyncParams[i];
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			String paramName = param.getName();
			sb.append(paramName);
		}
		w.println("return _getStringArgsValue_(\"%s\",%s);", getKey(method), sb.toString());
	}

	protected void generateBoolean(SourceWriter w, JMethod method) throws UnableToCompleteException {
		if (isUseI18nClass()) {
			String v = createI18nClass(getKey(method));
			w.println("return Boolean.parseBoolean(\"%s\");", v);
		} else {
			w.println("return _getBooleanValue_(\"%s\");", getKey(method));
		}
	}

	protected void generateDouble(SourceWriter w, JMethod method) throws UnableToCompleteException {
		if (isUseI18nClass()) {
			String v = createI18nClass(getKey(method));
			w.println("return Double.parseDouble(\"%s\");", v);
		} else {
			w.println("return _getDoubleValue_(\"%s\");", getKey(method));
		}
	}

	protected void generateFloat(SourceWriter w, JMethod method) throws UnableToCompleteException {
		if (isUseI18nClass()) {
			String v = createI18nClass(getKey(method));
			w.println("return Float.parseFloat(\"%s\");", v);
		} else {
			w.println("return _getFloatValue_(\"%s\");", getKey(method));
		}
	}

	protected void generateInt(SourceWriter w, JMethod method) throws UnableToCompleteException {
		if (isUseI18nClass()) {
			String v = createI18nClass(getKey(method));
			w.println("return Integer.parseInt(\"%s\");", v);
		} else {
			w.println("return _getIntegerValue_(\"%s\");", getKey(method));
		}
	}

	protected void generateMap(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getStringMapValue_(\"%s\");", getKey(method));
	}

	protected void generateStringArray(SourceWriter w, JMethod method) throws UnableToCompleteException {
		w.println("return _getStringArrayValue_(\"%s\");", getKey(method));
	}

	protected String getKey(JMethod method) {
		String key = method.getName();
		ConstantsBundle.Key a = getMethodAnnotation(method, ConstantsBundle.Key.class);
		if (a != null && a.value() != null) {
			key = a.value();
		}
		return key;
	}

	protected String toStringArray(String[] a) {
		if (a == null)
			return null;
		StringBuilder b = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < a.length; i++) {
			if (first) {
				first = false;
			} else {
				b.append(",");
			}
			b.append(String.valueOf(a[i]));
		}
		return b.toString();
	}

	protected <T extends Annotation> T getMethodAnnotation(JMethod method, Class<T> annotationClass) {
		if (method == null) {
			return null;
		}
		return method.getAnnotation(annotationClass);
	}
}
