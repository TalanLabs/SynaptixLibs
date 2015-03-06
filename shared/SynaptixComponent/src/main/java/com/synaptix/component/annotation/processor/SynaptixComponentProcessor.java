package com.synaptix.component.annotation.processor;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.component.annotation.helper.BuilderGenerationHelper;
import com.synaptix.component.annotation.helper.ComponentBeanMethod;
import com.synaptix.component.annotation.helper.ReflectionHelper;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.synaptix.component.annotation.SynaptixComponent")
public class SynaptixComponentProcessor extends GenProcessor {

	private Random serialRandom = new Random();

	@Override
	public void process(Element element) {
		SynaptixComponent sc = element.getAnnotation(SynaptixComponent.class);
		if (sc != null && element instanceof TypeElement) {
			TypeElement te = (TypeElement) element;
			if (sc.createFields()) {
				processFields(element);
			}
			if (te.getTypeParameters() == null || te.getTypeParameters().isEmpty()) {
				if (sc.createBuilder()) {
					processBuilder(element);
				}
			}
		}
	}

	private void processBuilder(Element element) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Builder";
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null && !packageName.isEmpty() ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			if (packageName != null && !packageName.isEmpty()) {
				writer.generatePackageDeclaration(packageName);
				writer.println();
				writer.println("import {0};", reflection.getClassRepresenter().getQualifiedName());
			}

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.FINAL);
			writer.generateClassHeader(dtoSimpleName, null, modifiers);
			writer.println();
			writer.println();

			writer.println("\tprivate java.util.Map<String,Object> builder;");
			writer.println();
			writer.println("\tpublic {0}() {", dtoSimpleName);
			writer.println("\t\tbuilder = new java.util.HashMap<String,Object>();", dtoElementSimpleName);
			writer.println("\t}");
			writer.println();

			writer.println("\tpublic {0} _copy({1} component) {", dtoSimpleName, dtoElementSimpleName);
			writer.println("\t\tif (component != null) {");
			writer.println("\t\t\tbuilder.putAll(component.straightGetProperties());");
			writer.println("\t\t}");
			writer.println("\t\treturn this;");
			writer.println("\t}");
			writer.println();

			Set<String> dejaVus = new HashSet<String>();

			for (ExecutableElement executableElement : reflection.getMethods()) {
				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case SET:
					String propertyName = cbm.inferName(executableElement);
					if (!dejaVus.contains(propertyName)) {
						dejaVus.add(propertyName);

						ExecutableType et = (ExecutableType) getEnvironment().getTypeUtils().asMemberOf((DeclaredType) (element.asType()), executableElement);

						TypeMirror tm = et.getParameterTypes().get(0);

						writer.println("\tpublic {0} {1}({2} arg0) {", dtoSimpleName, propertyName, tm.toString());
						writer.println("\t\tbuilder.put(\"{0}\",arg0);", propertyName);
						writer.println("\t\treturn this;");
						writer.println("\t}");
						writer.println();
					}
					break;
				default:
					break;
				}
			}

			writer.println("\tpublic {0} build() {", dtoElementSimpleName);
			writer.println("\t\t{0} component = com.synaptix.component.factory.ComponentFactory.getInstance().createInstance({0}.class);", dtoElementSimpleName, dtoElementSimpleName);
			writer.println("\t\tcomponent.straightSetProperties(builder);");
			writer.println("\t\treturn component;");
			writer.println("\t}");

			writer.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void processFields(Element element) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Fields";
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null && !packageName.isEmpty() ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			if (packageName != null && !packageName.isEmpty()) {
				writer.generatePackageDeclaration(packageName);
				writer.println();
			}
			writer.println("import com.synaptix.component.field.*;");
			writer.println("import com.synaptix.component.helper.*;");

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.FINAL);
			writer.generateClassHeader(dtoSimpleName, null, modifiers);
			writer.println();
			writer.println();

			Set<String> dejaVus = new HashSet<String>();

			for (ExecutableElement executableElement : reflection.getMethods()) {
				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case COMPUTED:
				case GET:
					String propertyName = cbm.inferName(executableElement);
					if (!dejaVus.contains(propertyName)) {
						dejaVus.add(propertyName);

						ExecutableType et = (ExecutableType) getEnvironment().getTypeUtils().asMemberOf((DeclaredType) (element.asType()), executableElement);

						TypeElement te = null;
						boolean anno = false;
						if (et.getReturnType() instanceof DeclaredType) {
							DeclaredType dt = (DeclaredType) et.getReturnType();
							if (dt.asElement() instanceof TypeElement) {
								te = (TypeElement) dt.asElement();
								anno = te.getAnnotation(SynaptixComponent.class) != null;
							}
						}
						if (anno) {
							ReflectionHelper rh = new ReflectionHelper(getEnvironment(), te);
							String sn = rename(rh.getSimpleClassName()) + "Fields.SubFields";
							String pn = rh.getPackageName();
							String cn = pn != null && !pn.isEmpty() ? pn + "." + sn : sn;

							writer.println("\tprivate static IDotField<{1}> {0} = null;", propertyName, cn);
							writer.println("\tpublic static final IDotField<{1}> {0}() { if ({0} == null) { {0} = new DefaultDotField<{1}>(new {1}(\"{0}\"),\"{0}\"); } return {0}; }", propertyName,
									cn);
						} else {
							writer.println("\tprivate static IField {0} = null;", propertyName);
							writer.println("\tpublic static final IField {0}() { if ({0} == null) { {0} = new DefaultField(\"{0}\"); } return {0}; }", propertyName);
						}
						writer.println();
					}
					break;
				default:
					break;
				}
			}

			writer.println("\tpublic static final class SubFields {");
			writer.println();
			writer.println("\t\tprivate String parentName;");
			writer.println();
			writer.println("\t\tpublic SubFields(String parentName) { super(); this.parentName = parentName; }");
			writer.println();

			dejaVus.clear();
			for (ExecutableElement executableElement : reflection.getMethods()) {
				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case COMPUTED:
				case GET:
					String propertyName = cbm.inferName(executableElement);
					if (!dejaVus.contains(propertyName)) {
						dejaVus.add(propertyName);
						TypeElement te = null;
						boolean anno = false;
						if (executableElement.getReturnType() instanceof DeclaredType) {
							DeclaredType dt = (DeclaredType) executableElement.getReturnType();
							if (dt.asElement() instanceof TypeElement) {
								te = (TypeElement) dt.asElement();
								anno = te.getAnnotation(SynaptixComponent.class) != null;
							}
						}
						if (anno) {
							ReflectionHelper rh = new ReflectionHelper(getEnvironment(), te);
							String sn = rename(rh.getSimpleClassName()) + "Fields.SubFields";
							String pn = rh.getPackageName();
							String cn = pn != null && !pn.isEmpty() ? pn + '.' + sn : sn;

							writer.println("\t\tprivate IDotField<{1}> {0} = null;", propertyName, cn);
							writer.println(
									"\t\tpublic IDotField<{1}> {0}() { if ({0} == null) { {0} = new DefaultDotField<{1}>(new {1}(ComponentHelper.PropertyDotBuilder.build(parentName,\"{0}\")),ComponentHelper.PropertyDotBuilder.build(parentName,\"{0}\")); } return {0}; }",
									propertyName, cn);
						} else {
							writer.println("\t\tprivate IField {0} = null;", propertyName);
							writer.println("\t\tpublic IField {0}() { if ({0} == null) { {0} = new DefaultField(ComponentHelper.PropertyDotBuilder.build(parentName,\"{0}\")); } return {0}; }",
									propertyName);
						}
						writer.println();
					}
					break;
				default:
					break;
				}
			}
			writer.println("\t}");

			writer.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void processEnum(Element element) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = "IEnum" + rename(dtoElementSimpleName);
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			writer.generatePackageDeclaration(reflection.getPackageName());

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			writer.generateInterfaceHeader(dtoSimpleName, modifiers);
			writer.println();

			writer.println("\tpublic enum Fields {");

			boolean first = true;
			for (ExecutableElement executableElement : reflection.getMethods()) {
				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case GET:
					if (first) {
						first = false;
					} else {
						writer.println(",");
					}
					String propertyName = cbm.inferName(executableElement);
					writer.print("\t\t{0}", propertyName);
					break;
				}
			}
			writer.println("");
			writer.println("\t}");

			writer.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void processImpl(Element element) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Impl";
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			writer.generatePackageDeclaration(reflection.getPackageName());

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			writer.generateClassHeader(dtoSimpleName, "com.synaptix.entity.component.ComponentImpl", modifiers, dtoElementSimpleName);
			writer.println();
			List<ExecutableElement> seds = new ArrayList<ExecutableElement>();
			Set<String> propertyNames = new HashSet<String>();
			Map<String, TypeMirror> typeMap = new HashMap<String, TypeMirror>();
			for (ExecutableElement executableElement : reflection.getMethods()) {
				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case GET:
					if (executableElement.getAnnotation(IComponent.EqualsKey.class) != null) {
						seds.add(executableElement);
					}
					String propertyName = cbm.inferName(executableElement);
					TypeMirror type = executableElement.getReturnType();
					writer.println();
					writer.println("\t@Override");
					writer.println("\tpublic {0} {1}() {", executableElement.getReturnType().toString(), executableElement.getSimpleName());
					writer.println("\t\treturn ({0})getter(\"{1}\");", writer.isPrimitive(type) ? writer.determineWrapperClass(type) : type.toString(), propertyName);

					propertyNames.add(propertyName);
					typeMap.put(propertyName, executableElement.getReturnType());
					writer.println("\t}");
					break;
				case SET:
					VariableElement setVe = executableElement.getParameters().get(0);
					writer.println();
					writer.println("\t@Override");
					writer.println("\tpublic void {0}({1} {2}) {", executableElement.getSimpleName(), setVe.asType().toString(), setVe.getSimpleName().toString());
					writer.println("\t\tsetter(\"{0}\",{1});", cbm.inferName(executableElement), setVe.getSimpleName().toString());
					writer.println("\t}");
					break;
				case COMPUTED:
					IComponent.Computed computed = executableElement.getAnnotation(IComponent.Computed.class);
					StringBuilder pSb = new StringBuilder();
					if (executableElement.getParameters() != null && !executableElement.getParameters().isEmpty()) {
						boolean first = true;
						for (VariableElement ve : executableElement.getParameters()) {
							if (!first) {
								pSb.append(", ");
							} else {
								first = false;
							}
							pSb.append(ve.asType().toString()).append(" ").append(ve.toString());
						}
					}
					writer.println();
					writer.println("\t@Override");
					writer.println("\tpublic {0} {1}({2}) {", executableElement.getReturnType().toString(), executableElement.getSimpleName(), pSb.toString());
					String className = null;
					try {
						Class<?> clazz = computed.value();
						className = clazz.getName();
					} catch (MirroredTypeException e) {
						className = e.getTypeMirror().toString();
					}
					writer.println("\t\t{0} _computedInstance = getComputedInstance({0}.class);", className);
					writer.println("\t\tif (_computedInstance == null) {");
					writer.println("\t\t\t_computedInstance = createComputedInstance({0}.class);", className);
					writer.println("\t\t\tputComputedInstance({0}.class, _computedInstance);", className);
					writer.println("\t\t}");
					writer.print("\t\t");
					if (!executableElement.getReturnType().toString().equals("void")) {
						writer.print("return ");
					}
					writer.print("_computedInstance.{0}(this", executableElement.getSimpleName());
					if (executableElement.getParameters() != null && !executableElement.getParameters().isEmpty()) {
						for (VariableElement ve : executableElement.getParameters()) {
							writer.print(", ");
							writer.print(ve.toString());
						}
					}
					writer.println(");");
					writer.println("\t}");
					break;
				}
			}

			writer.println();
			writer.println("\tprivate transient static final java.util.Set<String> propertyNames;");
			writer.println("\tprivate transient static final java.util.Map<String, Class<?>> propertyClassMap;");
			writer.println();
			writer.println("\tstatic {");
			writer.println("\t\tjava.util.Set<String> pns = new java.util.HashSet<String>();");
			writer.println("\t\tjava.util.Map<String, Class<?>> pcm = new java.util.HashMap<String, Class<?>>();");
			for (String propertyName : propertyNames) {
				writer.println("\t\tpns.add(\"{0}\");", propertyName);
				TypeMirror type = typeMap.get(propertyName);
				if (type instanceof DeclaredType) {
					DeclaredType dt = (DeclaredType) type;
					writer.println("\t\tpcm.put(\"{0}\",{1}.class);", propertyName, dt.asElement().toString());
				} else {
					writer.println("\t\tpcm.put(\"{0}\",{1}.class);", propertyName, type.toString());
				}
			}
			writer.println("\t\tpropertyNames = java.util.Collections.unmodifiableSet(pns);");
			writer.println("\t\tpropertyClassMap = java.util.Collections.unmodifiableMap(pcm);");
			writer.println("\t}");
			writer.println();
			writer.println("\t@Override public java.util.Set<String> straightGetPropertyNames() { return propertyNames; }  @Override public Class<?> straightGetPropertyClass(String propertyName) { if (propertyName == null) { throw new IllegalArgumentException(\"propertyName is null\"); } return propertyClassMap.get(propertyName); }");
			writer.println();
			writer.println("\tpublic {0}() {", dtoSimpleName);
			writer.println("\t\tsuper();");
			for (String propertyName : propertyNames) {
				writer.println("\t\tpropertyValueMap.put(\"{0}\",{1});", propertyName, determineValue(typeMap.get(propertyName)));
			}
			writer.println("\t}");
			generateHashCode(writer, seds);
			generateEquals(writer, dtoSimpleName, seds);
			writer.println();
			writer.println("\tprivate static final long serialVersionUID = " + String.valueOf(serialRandom.nextLong()) + "L;");

			writer.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
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

	private void generateEquals(BuilderGenerationHelper writer, String simpleClassName, Collection<ExecutableElement> fieldElements) {
		writer.println();
		writer.println("\t@Override");
		writer.println("\tpublic boolean equals(Object obj) {");
		if (fieldElements.size() > 0) {
			writer.println("\t\tif (this == obj) {");
			writer.println("\t\t\treturn true;");
			writer.println("\t\t}");
			writer.println("\t\tif (obj == null) {");
			writer.println("\t\t\treturn false;");
			writer.println("\t\t}");
			writer.println("\t\tif (getClass() != obj.getClass()) {");
			writer.println("\t\t\treturn false;");
			writer.println("\t\t}");
			writer.println("\t\t{0} other = ({0}) obj;", simpleClassName);
			for (ExecutableElement fieldElement : fieldElements) {
				TypeMirror type = fieldElement.getReturnType();
				String fieldName = fieldElement.getSimpleName().toString();
				if (type instanceof ArrayType) {
					writer.println("\t\tif (!java.util.Arrays.equals({0}(),other.{0}())) {", fieldName);
					writer.println("\t\t\treturn false;");
					writer.println("\t\t}");
				} else if (writer.isPrimitive(type)) {
					writer.println("\t\tif ({0}() != other.{0}()) {", fieldName);
					writer.println("\t\t\treturn false;");
					writer.println("\t\t}");
				} else {
					writer.println("\t\tif ({0}() == null || other.{0}() != null) {", fieldName);
					writer.println("\t\t\treturn false;");
					writer.println("\t\t} else if (!{0}().equals(other.{0}())) {", fieldName);
					writer.println("\t\t\treturn false;");
					writer.println("\t\t}");
				}
			}
			writer.println("\t\treturn true;");
		} else {
			writer.println("\t\treturn super.equals(obj);");
		}
		writer.println("\t}");
	}

	private void generateHashCode(BuilderGenerationHelper writer, Collection<ExecutableElement> fieldElements) {
		writer.println();
		writer.println("\t@Override");
		writer.println("\tpublic int hashCode() {");
		if (fieldElements.size() > 0) {
			writer.println("\t\tint hashCode = 23;");
			for (ExecutableElement fieldElement : fieldElements) {
				TypeMirror type = fieldElement.getReturnType();
				String fieldName = fieldElement.getSimpleName().toString();
				if (type instanceof ArrayType) {
					writer.println("\t\thashCode = (hashCode * 37) + java.util.Arrays.hashCode({0}());", fieldName);
				} else if (writer.isPrimitive(type)) {
					writer.println("\t\thashCode = (hashCode * 37) + new {0}({1}()).hashCode();", writer.determineWrapperClass(type), fieldName);
				} else {
					writer.println("\t\thashCode = (hashCode * 37) + ({0}() == null ? 1 : {0}().hashCode());", fieldName);
				}
			}
			writer.println("\t\treturn hashCode;");
		} else {
			writer.println("\t\treturn super.hashCode();");
		}
		writer.println("\t}");
	}

	private String determineValue(TypeMirror type) {
		String typeName = type.toString();
		if (typeName.equals("byte")) {
			return "new Byte((byte)0)";
		}
		if (typeName.equals("short")) {
			return "new Short((short)0)";
		}
		if (typeName.equals("int")) {
			return "new Integer(0)";
		}
		if (typeName.equals("long")) {
			return "new Long(0)";
		}
		if (typeName.equals("float")) {
			return "new Float(0)";
		}
		if (typeName.equals("double")) {
			return "new Double(0)";
		}
		if (typeName.equals("char")) {
			return "new Character(' ')";
		}
		if (typeName.equals("boolean")) {
			return "new Boolean(false)";
		} else {
			return "null";
		}
	}
}
