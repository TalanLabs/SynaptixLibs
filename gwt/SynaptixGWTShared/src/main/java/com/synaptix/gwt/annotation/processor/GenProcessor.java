package com.synaptix.gwt.annotation.processor;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

public abstract class GenProcessor extends AbstractProcessor {

	private ProcessingEnvironment environment;

	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);
		this.environment = environment;
	}

	/**
	 * Returns the current processing environment.
	 * 
	 * @return the processing environment.
	 */
	public ProcessingEnvironment getEnvironment() {
		return environment;
	}

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
							this.process(annotatedElement);
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
	 * Prints a message.
	 * 
	 * @param message
	 *            the message
	 */
	public void printMessage(String message) {
		getEnvironment().getMessager().printMessage(Kind.NOTE, message);
	}

	/**
	 * Prints an error.
	 * 
	 * @param message
	 *            the error message
	 */
	public void printError(String message) {
		getEnvironment().getMessager().printMessage(Kind.ERROR, message);
	}

	/**
	 * Override this function to receive elements which you've declared in supported annotations.
	 * 
	 * @param annotatedElement
	 *            the annotated element.
	 */
	public abstract void process(Element annotatedElement);

	/**
	 * Utility method called after processing has started.
	 */
	public void onProcessingStarted() {
		printMessage(getClass().getName() + " started.");
	}

	/**
	 * Utility method called after the processing is finished.
	 */
	public void onProcessingCompleted() {
		printMessage(getClass().getName() + " finished.");
	}
}
