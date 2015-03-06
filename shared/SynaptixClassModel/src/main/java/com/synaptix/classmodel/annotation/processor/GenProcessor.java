package com.synaptix.classmodel.annotation.processor;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public abstract class GenProcessor extends AbstractJannocessorProcessor {

	@Override
	@SuppressWarnings("unchecked")
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		if (!roundEnvironment.processingOver()) {
			onProcessingStarted();
			for (String supportedAnnotationName : getSupportedAnnotationTypes()) {
				printMessage("Searching for " + supportedAnnotationName + " annotations.");
				try {
					Class<?> supportedAnnotationClass = Class.forName(supportedAnnotationName);
					if (supportedAnnotationClass.isAnnotation()) {
						for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith((Class<? extends Annotation>) supportedAnnotationClass)) {
							printMessage("Found " + annotatedElement.toString() + ".");
							try {
								this.process(annotatedElement);
							} catch (Throwable t) {
								printError(t);
							}
						}
					}
				} catch (ClassNotFoundException e) {
					printError("Annotation not found: " + supportedAnnotationName);
				}
			}
			onProcessingCompleted();
		}
		return true;
	}

	/**
	 * Override this function to receive elements which you've declared in
	 * supported annotations.
	 * 
	 * @param annotatedElement
	 *            the annotated element.
	 */
	public abstract void process(Element annotatedElement);

}
