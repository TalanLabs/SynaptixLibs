package com.synaptix.classmodel.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.jannocessor.JannocessorException;
import org.jannocessor.adapter.AdapterFactory;
import org.jannocessor.context.Config;
import org.jannocessor.engine.JannocessorEngine;
import org.jannocessor.engine.impl.ProcessorModule;
import org.jannocessor.inject.ImportsServiceModule;
import org.jannocessor.model.JavaElement;
import org.jannocessor.model.structure.AbstractJavaStructure;
import org.jannocessor.model.util.ValidationUtils;
import org.jannocessor.service.configuration.ConfigurationServiceModule;
import org.jannocessor.service.io.IOServiceModule;
import org.jannocessor.service.render.TemplateServiceModule;
import org.jannocessor.service.representation.RepresentationServiceModule;
import org.jannocessor.service.splitter.SplitterServiceModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class AbstractJannocessorProcessor extends AbstractSynaptixProcessor {

	private JannocessorEngine engine;

	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);

		try {
			Injector injector = Guice.createInjector(new ProcessorModule(new Config(environment.getOptions())), new ConfigurationServiceModule(), new ImportsServiceModule(), new IOServiceModule(),
					new TemplateServiceModule(), new SplitterServiceModule(), new RepresentationServiceModule());

			engine = injector.getInstance(JannocessorEngine.class);
			engine.configure(engine.getTemplatesPath(), true);
		} catch (Exception e) {
			printError(e);
			throw new RuntimeException(e);
		}
	}

	public JannocessorEngine getEngine() {
		return engine;
	}

	/**
	 * Generate java code source
	 * 
	 * @param model
	 */
	public void generateCode(AbstractJavaStructure model) {
		try {
			// check model correctness
			ValidationUtils.validate(model);

			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("self", model);

			Map<String, String> contents = engine.split(engine.renderMacro("main", attributes, new String[] {}));
			for (Entry<String, String> entry : contents.entrySet()) {
				String filename = entry.getKey();
				String content = entry.getValue();

				writeFile(StandardLocation.SOURCE_OUTPUT, filename, content);
			}
		} catch (JannocessorException e) {
			printError(e);
			throw new RuntimeException("Exception occured while rendering", e);
		}
	}

	private void writeFile(StandardLocation standardLocation, String filename, String content) {
		String info = String.format("%s/%s", "", filename);
		printMessage(String.format("- Generating file: %s", info));

		Writer writer = null;
		try {
			FileObject fileRes = getEnvironment().getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", filename);
			writer = fileRes.openWriter();
			writer.write(content);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't write to file: " + info, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException("Couldn't close file: " + info, e);
				}
			}
		}
	}

	public <T extends JavaElement> T buildElementModel(Element element, Class<T> clazz) {
		return AdapterFactory.getElementModel(element, clazz, getEnvironment().getElementUtils(), getEnvironment().getTypeUtils());
	}
}
