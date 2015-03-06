package com.synaptix.test.annotation.processor;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import org.jannocessor.model.JavaElement;
import org.jannocessor.model.structure.JavaClass;
import org.jannocessor.model.structure.JavaPackage;
import org.jannocessor.model.util.Classes;
import org.jannocessor.model.util.New;

import com.synaptix.classmodel.annotation.processor.GenProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.synaptix.test.annotation.SynaptixTest")
public class SynaptixTestProcessor extends GenProcessor {

	@Override
	public void process(Element annotatedElement) {
		JavaElement model = buildElementModel(annotatedElement, JavaElement.class);

		printMessage(model.getName().getText());

		JavaClass wrapper = New.classs(Classes.PUBLIC, "Coucou");

		wrapper.getFields().add(New.field(New.type("com.synaptix.rien.existepas"), "rien"));

		JavaPackage destPackage = New.packagee("com.test");
		destPackage.getClasses().add(wrapper);

		generateCode(wrapper);
	}
}
