package com.synaptix.auth.annotation.processor;

import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.annotation.helper.AuthBeanMethod;
import com.synaptix.auth.annotation.helper.BuilderGenerationHelper;
import com.synaptix.auth.annotation.helper.ReflectionHelper;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.synaptix.auth.annotation.SynaptixAuth")
public class SynaptixAuthProcessor extends GenProcessor {

	@Override
	public void process(Element element) {
		processFields(element);
	}

	private void processFields(Element element) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String dtoElementSimpleName = reflection.getSimpleClassName();
			String dtoSimpleName = rename(dtoElementSimpleName) + "Methods";
			String packageName = reflection.getPackageName();
			String dtoClassName = packageName != null && !packageName.isEmpty() ? packageName + '.' + dtoSimpleName : dtoSimpleName;

			printMessage("Generating '" + dtoClassName + "' from '" + dtoElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(dtoClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			if (packageName != null && !packageName.isEmpty()) {
				writer.generatePackageDeclaration(packageName);
				writer.println();
			}
			writer.println("import com.synaptix.auth.method.*;");

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.FINAL);
			writer.generateClassHeader(dtoSimpleName, null, modifiers);
			writer.println();
			writer.println();

			for (ExecutableElement executableElement : reflection.getMethods()) {
				AuthBeanMethod cbm = AuthBeanMethod.which(executableElement);
				switch (cbm) {
				case CALL_BOOLEAN:
					String methodName = executableElement.getSimpleName().toString();
					AuthsBundle.Key key = executableElement.getAnnotation(AuthsBundle.Key.class);
					writer.println("\tprivate static IAuthMethod {0} = null;", methodName);
					writer.println("\tpublic static final IAuthMethod {0}() { if ({0} == null) { {0} = new DefaultAuthMethod({1}.class, \"{2}\", \"{3}\"); } return {0}; }", methodName,
							dtoElementSimpleName, key.object(), key.action());
					String uri = new StringBuilder().append(packageName != null && !packageName.isEmpty() ? packageName + '.' + dtoElementSimpleName : dtoElementSimpleName).append("@")
							.append("object=").append(URLEncoder.encode(key.object(), "UTF-8")).append("&action=").append(URLEncoder.encode(key.action(), "UTF-8")).toString();
					writer.println("\tpublic static final String {0}String = \"{1}\";", methodName, uri);

					writer.println();
					break;
				default:
					break;
				}
			}

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
}
