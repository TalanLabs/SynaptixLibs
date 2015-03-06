package com.synaptix.gwt.annotation.processor;

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

import com.synaptix.gwt.annotation.SynaptixGWTComponent;
import com.synaptix.gwt.annotation.helper.BuilderGenerationHelper;
import com.synaptix.gwt.annotation.helper.ComponentBeanMethod;
import com.synaptix.gwt.annotation.helper.ReflectionHelper;
import com.synaptix.gwt.shared.component.IComponentDto;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.synaptix.gwt.annotation.SynaptixGWTComponent")
public class SynaptixGWTComponentProcessor extends GenProcessor {

	private Random serialRandom = new Random();

	@Override
	public void process(Element element) {
		SynaptixGWTComponent sc = element.getAnnotation(SynaptixGWTComponent.class);
		if (sc != null && element instanceof TypeElement) {
			TypeElement te = (TypeElement) element;
			if (te.getTypeParameters() == null || te.getTypeParameters().isEmpty()) {
				if (sc.createImpl()) {
					processImpl(element);
					processCustom(element);

					if (sc.createBuilder()) {
						processBuilder(element);
					}
				}
			}
		}
		if (sc.createFields()) {
			processFields(element);
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

			}

			writer.println("import {0};", reflection.getClassRepresenter().getQualifiedName());

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.FINAL);
			writer.generateClassHeader(dtoSimpleName, null, modifiers);
			writer.println();
			writer.println();

			writer.println("\tprivate java.util.Map<String,Object> builder;");
			writer.println();
			writer.println("\tpublic {0}() {", dtoSimpleName);
			writer.println("\t\tbuilder = new java.util.HashMap<String,Object>();");
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
			writer.println("\t\t{0} component = new {1}Impl();", dtoElementSimpleName, rename(dtoElementSimpleName));
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
			writer.println("import com.synaptix.gwt.shared.field.*;");
			writer.println("import com.synaptix.gwt.shared.helper.*;");

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
								anno = te.getAnnotation(SynaptixGWTComponent.class) != null;
							}
						}
						if (anno) {
							ReflectionHelper rh = new ReflectionHelper(getEnvironment(), te);
							String sn = rename(rh.getSimpleClassName()) + "Fields.SubFields";
							String pn = rh.getPackageName();
							String cn = pn != null && !pn.isEmpty() ? pn + '.' + sn : sn;

							writer.println("\tprivate static IDotFieldDto<{1}> {0} = null;", propertyName, cn);
							writer.println("\tpublic static final IDotFieldDto<{1}> {0}() { if ({0} == null) { {0} = new DefaultDotFieldDto<{1}>(new {1}(\"{0}\"),\"{0}\"); } return {0}; }",
									propertyName, cn);
						} else {
							writer.println("\tprivate static IFieldDto {0} = null;", propertyName);
							writer.println("\tpublic static final IFieldDto {0}() { if ({0} == null) { {0} = new DefaultFieldDto(\"{0}\"); } return {0}; }", propertyName);
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
								anno = te.getAnnotation(SynaptixGWTComponent.class) != null;
							}
						}
						if (anno) {
							ReflectionHelper rh = new ReflectionHelper(getEnvironment(), te);
							String sn = rename(rh.getSimpleClassName()) + "Fields.SubFields";
							String pn = rh.getPackageName();
							String cn = pn != null ? pn + '.' + sn : sn;

							writer.println("\t\tprivate IDotFieldDto<{1}> {0} = null;", propertyName, cn);
							writer.println(
									"\t\tpublic IDotFieldDto<{1}> {0}() { if ({0} == null) { {0} = new DefaultDotFieldDto<{1}>(new {1}(ComponentDtoHelper.PropertyDotBuilder.build(parentName,\"{0}\")),ComponentDtoHelper.PropertyDotBuilder.build(parentName,\"{0}\")); } return {0}; }",
									propertyName, cn);
						} else {
							writer.println("\t\tprivate IFieldDto {0} = null;", propertyName);
							writer.println(
									"\t\tpublic IFieldDto {0}() { if ({0} == null) { {0} = new DefaultFieldDto(ComponentDtoHelper.PropertyDotBuilder.build(parentName,\"{0}\")); } return {0}; }",
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
		BuilderGenerationHelper writerImpl = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Impl";
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null && !packageName.isEmpty() ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			// ComponentImpl
			writerImpl = new BuilderGenerationHelper(getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter());
			if (packageName != null && !packageName.isEmpty()) {
				writerImpl.generatePackageDeclaration(packageName);
				writerImpl.println();
			}

			writerImpl.println("import {0};", reflection.getClassRepresenter().getQualifiedName());

			// writerImpl.generateImports("com.synaptix.gwt.shared.component.PropertyChangeEventDto",
			// "com.synaptix.gwt.shared.component.PropertyChangeListenerDto",
			// "java.util.ArrayList",
			// "java.util.Collections", "java.util.HashMap",
			// "java.util.HashSet", "java.util.List", "java.util.Map",
			// "java.util.Map.Entry", "java.util.Set");
			Set<Modifier> modifierImpls = new HashSet<Modifier>();
			modifierImpls.add(Modifier.PUBLIC);
			writerImpl.generateClassHeader(dtoSimpleName, "com.synaptix.gwt.shared.component.ComponentDtoImpl", modifierImpls, dtoElementSimpleName);
			writerImpl.println();

			Set<String> dejaVus = new HashSet<String>();

			List<ExecutableElement> seds = new ArrayList<ExecutableElement>();
			Set<String> propertyNames = new HashSet<String>();
			Map<String, TypeMirror> typeMap = new HashMap<String, TypeMirror>();
			for (ExecutableElement executableElement : reflection.getMethods()) {
				ExecutableType et = (ExecutableType) getEnvironment().getTypeUtils().asMemberOf((DeclaredType) (element.asType()), executableElement);

				String propertyName;

				ComponentBeanMethod cbm = ComponentBeanMethod.which(executableElement);
				switch (cbm) {
				case GET:
					propertyName = cbm.inferName(executableElement);
					if (!propertyNames.contains(propertyName)) {
						if (executableElement.getAnnotation(IComponentDto.EqualsKey.class) != null) {
							seds.add(executableElement);
						}
						TypeMirror type = et.getReturnType();
						writerImpl.println();
						writerImpl.println("\t@Override");
						writerImpl.println("\tpublic {0} {1}() {", type.toString(), executableElement.getSimpleName());
						writerImpl.println("\t\treturn ({0})getter(\"{1}\");", writerImpl.isPrimitive(type) ? writerImpl.determineWrapperClass(type) : type.toString(), propertyName);

						propertyNames.add(propertyName);
						typeMap.put(propertyName, type);
						writerImpl.println("\t}");
					}
					break;
				case SET:
					propertyName = cbm.inferName(executableElement);
					if (!dejaVus.contains(propertyName)) {
						dejaVus.add(propertyName);

						TypeMirror setVe = et.getParameterTypes().get(0);
						writerImpl.println();
						writerImpl.println("\t@Override");
						writerImpl.println("\tpublic void {0}({1} arg0) {", executableElement.getSimpleName(), setVe.toString());
						writerImpl.println("\t\tsetter(\"{0}\",arg0);", cbm.inferName(executableElement));
						writerImpl.println("\t}");
					}
					break;
				case COMPUTED:
					IComponentDto.Computed computed = executableElement.getAnnotation(IComponentDto.Computed.class);
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
					writerImpl.println();
					writerImpl.println("\t@Override");
					writerImpl.println("\tpublic {0} {1}({2}) {", executableElement.getReturnType().toString(), executableElement.getSimpleName(), pSb.toString());
					String className = null;
					try {
						Class<?> clazz = computed.value();
						className = clazz.getName();
					} catch (MirroredTypeException e) {
						className = e.getTypeMirror().toString();
					}
					writerImpl.println("\t\t{0} _computedInstance = ({0})getComputedInstance({0}.class);", className);
					writerImpl.println("\t\tif (_computedInstance == null) {");
					writerImpl.println("\t\t\t_computedInstance = new {0}();", className);
					writerImpl.println("\t\t\tputComputedInstance({0}.class, _computedInstance);", className);
					writerImpl.println("\t\t}");
					writerImpl.print("\t\t");
					if (!executableElement.getReturnType().toString().equals("void")) {
						writerImpl.print("return ");
					}
					writerImpl.print("_computedInstance.{0}(this", executableElement.getSimpleName());
					if (executableElement.getParameters() != null && !executableElement.getParameters().isEmpty()) {
						for (VariableElement ve : executableElement.getParameters()) {
							writerImpl.print(", ");
							writerImpl.print(ve.toString());
						}
					}
					writerImpl.println(");");
					writerImpl.println("\t}");
					break;
				}
			}

			writerImpl.println();
			writerImpl.println("\t// Use only for RPC Serialization");
			for (String propertyName : propertyNames) {
				TypeMirror type = typeMap.get(propertyName);

				writerImpl.println("\t@SuppressWarnings(\"unused\")");
				writerImpl.println("\tprivate {0} {1};", writerImpl.isPrimitive(type) ? writerImpl.determineWrapperClass(type) : type.toString(), propertyName);
			}

			writerImpl.println();
			writerImpl.println("\tprivate transient static final java.util.Set<String> propertyNames;");
			writerImpl.println("\tprivate transient static final java.util.Map<String, Class<?>> propertyClassMap;");
			writerImpl.println();
			writerImpl.println("\tstatic {");
			writerImpl.println("\t\tjava.util.Set<String> pns = new java.util.HashSet<String>();");
			writerImpl.println("\t\tjava.util.Map<String, Class<?>> pcm = new java.util.HashMap<String, Class<?>>();");
			for (String propertyName : propertyNames) {
				writerImpl.println("\t\tpns.add(\"{0}\");", propertyName);
				TypeMirror type = typeMap.get(propertyName);
				if (type instanceof DeclaredType) {
					DeclaredType dt = (DeclaredType) type;
					writerImpl.println("\t\tpcm.put(\"{0}\",{1}.class);", propertyName, dt.asElement().toString());
				} else {
					writerImpl.println("\t\tpcm.put(\"{0}\",{1}.class);", propertyName, type.toString());
				}
			}
			writerImpl.println("\t\tpropertyNames = java.util.Collections.unmodifiableSet(pns);");
			writerImpl.println("\t\tpropertyClassMap = java.util.Collections.unmodifiableMap(pcm);");
			writerImpl.println("\t}");
			writerImpl.println();
			writerImpl
					.println("\t@Override public java.util.Set<String> straightGetPropertyNames() { return propertyNames; }  @Override public Class<?> straightGetPropertyClass(String propertyName) { if (propertyName == null) { throw new IllegalArgumentException(\"propertyName is null\"); } return propertyClassMap.get(propertyName); }");
			writerImpl.println();
			writerImpl.println("\tpublic {0}() {", dtoSimpleName);
			writerImpl.println("\t\tsuper();");
			for (String propertyName : propertyNames) {
				writerImpl.println("\t\tpropertyValueMap.put(\"{0}\",{1});", propertyName, determineValue(typeMap.get(propertyName)));
			}
			writerImpl.println("\t}");
			generateHashCode(writerImpl, seds);
			generateEquals(writerImpl, dtoSimpleName, seds);
			writerImpl.println();
			// writerImpl.println("\t// IPropertyChangeCapable");
			// writerImpl
			// .println("\tprivate transient final List<PropertyChangeListenerDto> propertyChangeListenerDtos = new ArrayList<PropertyChangeListenerDto>();  private transient final Map<String, List<PropertyChangeListenerDto>> propertyChangeListenerDtoMap = new HashMap<String, List<PropertyChangeListenerDto>>();  @Override public void addPropertyChangeListenerDto(PropertyChangeListenerDto l) { propertyChangeListenerDtos.add(l); }  @Override public void removePropertyChangeListenerDto(PropertyChangeListenerDto l) { propertyChangeListenerDtos.remove(l); }  @Override public void addPropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l) { List<PropertyChangeListenerDto> ls = propertyChangeListenerDtoMap.get(propertyName); if (ls == null) { ls = new ArrayList<PropertyChangeListenerDto>(); propertyChangeListenerDtoMap.put(propertyName, ls); } ls.add(l); }  @Override public void removePropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l) { List<PropertyChangeListenerDto> ls = propertyChangeListenerDtoMap.get(propertyName); if (ls != null) { ls.remove(l); if (ls.isEmpty()) { propertyChangeListenerDtoMap.remove(propertyName); } } }  private void firePropertyChange(String propertyName, Object oldValue, Object newValue) { PropertyChangeEventDto evt = new PropertyChangeEventDto(this, propertyName, oldValue, newValue); for (PropertyChangeListenerDto l : propertyChangeListenerDtos) { l.propertyChange(evt); } List<PropertyChangeListenerDto> ls = propertyChangeListenerDtoMap.get(propertyName); if (ls != null) { for (PropertyChangeListenerDto l : ls) { l.propertyChange(evt); } } }");
			// writerImpl.println();
			// writerImpl.println("\t// IComponent");
			// writerImpl
			// .println("\tprivate transient Map<String, Object> propertyValueMap = new HashMap<String, Object>();  private transient static final Set<String> propertyNames;  private transient static final Map<String, Class<?>> propertyClassMap;  private static final void addProperty(String propertyName, Class<?> propertyClass) { propertyNames.add(propertyName); propertyClassMap.put(propertyName, propertyClass); }  @Override public Map<String, Object> straightGetProperties() { return new HashMap<String, Object>(propertyValueMap); }  @Override public Object straightGetProperty(String propertyName) { return getter(propertyName); }  @Override public void straightSetProperties(Map<String, Object> properties) { if (properties == null) { throw new IllegalArgumentException(\"properties is null\"); } for (Entry<String, Object> entry : properties.entrySet()) { setter(entry.getKey(), entry.getValue()); } }  @Override public void straightSetProperty(String propertyName, Object value) { setter(propertyName, value); }  @Override public Set<String> straightGetPropertyNames() { return Collections.unmodifiableSet(propertyNames); }  @Override public Class<?> straightGetPropertyClass(String propertyName) { if (propertyName == null) { throw new IllegalArgumentException(\"propertyName is null\"); } return propertyClassMap.get(propertyName); }");
			// writerImpl
			// .println("\tprivate Object getter(String propertyName) { if (propertyName == null) { throw new IllegalArgumentException(\"propertyName is null\"); } if (!propertyNames.contains(propertyName)) { throw new IllegalArgumentException(\"propertyName=\" + propertyName + \" is not property\"); }  return propertyValueMap.get(propertyName); }");
			// writerImpl
			// .println("\tprivate void setter(String propertyName, Object value) { if (propertyName == null) { throw new IllegalArgumentException(\"propertyName is null\"); } if (!propertyNames.contains(propertyName)) { throw new IllegalArgumentException(\"propertyName=\" + propertyName + \" is not property\"); }  Object oldValue = propertyValueMap.get(propertyName); propertyValueMap.put(propertyName, value); firePropertyChange(propertyName, oldValue, value); }");
			//
			// writerImpl.println();
			// writerImpl.println("\tpublic String toString() {");
			// writerImpl.println("\t\tStringBuilder sb = new StringBuilder();");
			// writerImpl.println("\t\tsb.append(this.getClass().getName()).append(\"@\").append(super.hashCode()).append(\"[\");",
			// dtoClassName);
			// writerImpl.println("\t\tboolean first = true;");
			// writerImpl.println("\t\tfor(java.util.Map.Entry<String,Object> entry : propertyValueMap.entrySet()) {");
			// writerImpl.println("\t\t\tif (!first) {");
			// writerImpl.println("\t\t\t\tsb.append(\", \");");
			// writerImpl.println("\t\t\t} else {");
			// writerImpl.println("\t\t\t\tfirst = false;");
			// writerImpl.println("\t\t\t}");
			// writerImpl.println("\t\t\tsb.append(entry.getKey()).append(\"=\").append(entry.getValue());");
			// writerImpl.println("\t\t}");
			// writerImpl.println("\t\tsb.append(\"]\");");
			// writerImpl.println("\t\treturn sb.toString();");
			// writerImpl.println("\t}");

			writerImpl.println("\tprivate static final long serialVersionUID = " + String.valueOf(serialRandom.nextLong()) + "L;");

			writerImpl.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writerImpl != null) {
				writerImpl.close();
			}
		}
	}

	private void processCustom(Element element) {
		BuilderGenerationHelper writerCustom = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Impl";
			String customSimpleName = dtoSimpleName + "_CustomFieldSerializer";
			String packageName = reflection.getPackageName();
			String customClassName = (packageName != null && !packageName.isEmpty() ? packageName + '.' + customSimpleName : customSimpleName);

			printMessage("Generating '" + customClassName + "' from '" + dtoElementSimpleName + "'.");

			// Custom
			writerCustom = new BuilderGenerationHelper(getEnvironment().getFiler().createSourceFile(customClassName, element).openWriter());
			if (packageName != null && !packageName.isEmpty()) {
				writerCustom.generatePackageDeclaration(packageName);
				writerCustom.println();
			}
			writerCustom.generateImports("java.util.Set", "com.google.gwt.user.client.rpc.CustomFieldSerializer", "com.google.gwt.user.client.rpc.SerializationException",
					"com.google.gwt.user.client.rpc.SerializationStreamReader", "com.google.gwt.user.client.rpc.SerializationStreamWriter");
			Set<Modifier> modifierCustoms = new HashSet<Modifier>();
			modifierCustoms.add(Modifier.PUBLIC);
			writerCustom.generateClassHeader(customSimpleName, "CustomFieldSerializer<" + dtoSimpleName + ">", modifierCustoms);
			writerCustom.println();

			writerCustom
					.println(
							"public static void deserialize(SerializationStreamReader streamReader, {0} instance) throws SerializationException { int size = streamReader.readInt(); for (int i = 0; i < size; ++i) { String propertyName = streamReader.readString(); Object value = streamReader.readObject(); instance.straightSetProperty(propertyName, value); } }  public static void serialize(SerializationStreamWriter streamWriter, {0} instance) throws SerializationException { Set<String> propertyNames = instance.straightGetPropertyNames(); streamWriter.writeInt(propertyNames.size()); for (String propertyName : instance.straightGetPropertyNames()) { Object value = instance.straightGetProperty(propertyName); streamWriter.writeString(propertyName); streamWriter.writeObject(value); } }  @Override public void deserializeInstance(SerializationStreamReader streamReader, {0} instance) throws SerializationException { deserialize(streamReader, instance); }  @Override public void serializeInstance(SerializationStreamWriter streamWriter, {0} instance) throws SerializationException { serialize(streamWriter, instance); }",
							dtoSimpleName);

			writerCustom.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writerCustom != null) {
				writerCustom.close();
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
					writer.println("\t\tif ({0}() == null || other.{0}() == null) {", fieldName);
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
			return "new Byte(0)";
		}
		if (typeName.equals("short")) {
			return "new Short(0)";
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
