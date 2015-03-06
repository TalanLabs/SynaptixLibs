package com.synaptix.classmodel.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.jannocessor.model.JavaElement;
import org.jannocessor.model.structure.AbstractJavaStructure;

public class AbstractSuperJannocessorProcessor extends AbstractJannocessorProcessor {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Annotated {

		Class<? extends Annotation> value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Types {

		Class<? extends JavaElement> value();

	}

	private List<MyGenerator<? extends JavaElement>> myGenerators;

	private IGeneratorContext generatorContext;

	public AbstractSuperJannocessorProcessor() {
		super();

		this.myGenerators = new ArrayList<MyGenerator<? extends JavaElement>>();
		this.generatorContext = new MyGeneratorContext();
	}

	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);

		printMessage("Parcours");
		for (Method method : this.getClass().getMethods()) {
			if (IGenerator.class.isAssignableFrom(method.getReturnType()) && (method.getParameterTypes() == null || method.getParameterTypes().length == 0)) {
				Annotated annotated = method.getAnnotation(Annotated.class);
				Types types = method.getAnnotation(Types.class);
				if (annotated != null && types != null) {
					MyGenerator<? extends JavaElement> myGenerator = create(method, annotated, types);
					myGenerators.add(myGenerator);

					printMessage("Generator found " + myGenerator.getClass().getCanonicalName());
				}
			}
		}
	}

	private <E extends JavaElement> MyGenerator<E> create(Method method, Annotated annotated, Types types) {
		MyGenerator<E> myGenerator = new MyGenerator<E>();
		myGenerator.annotationClass = annotated.value();
		myGenerator.javaElementClass = (Class<E>) types.value();
		try {
			myGenerator.generator = (IGenerator<E>) method.invoke(this);
		} catch (Exception e) {
			printError(e);
			throw new RuntimeException("Error method !!");
		}
		return myGenerator;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> res = new HashSet<String>();
		for (MyGenerator<? extends JavaElement> myGenerator : myGenerators) {
			res.add(myGenerator.annotationClass.getCanonicalName());
		}
		return res;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		if (!roundEnvironment.processingOver()) {
			onProcessingStarted();
			for (MyGenerator<? extends JavaElement> myGenerator : myGenerators) {
				printMessage("Searching for " + myGenerator.annotationClass + " annotations.");
				for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(myGenerator.annotationClass)) {
					printMessage("Found " + annotatedElement.toString() + ".");
					try {
						process(myGenerator, annotatedElement);
					} catch (Throwable t) {
						printError(t);
					}
				}
			}
			onProcessingCompleted();
		}
		return true;
	}

	private <E extends JavaElement> void process(MyGenerator<E> myGenerator, Element annotatedElement) {
		E javaElement = buildElementModel(annotatedElement, myGenerator.javaElementClass);
		myGenerator.generator.generateCodeFrom(javaElement, generatorContext);
	}

	private class MyGenerator<E extends JavaElement> {

		Class<? extends Annotation> annotationClass;

		Class<E> javaElementClass;

		IGenerator<E> generator;

	}

	private class MyGeneratorContext implements IGeneratorContext {

		public void generateCode(AbstractJavaStructure model) {
			AbstractSuperJannocessorProcessor.this.generateCode(model);
		}
	}
}
