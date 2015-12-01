import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import model.ICountry;
import model.IZip;

public class MainFreeMarker {

	public static void main(String[] args) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.FRENCH);

		ICountry country = ComponentFactory.getInstance().createInstance(
				ICountry.class);
		country.setCountry("France");

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("country", country);
		root.put("utils", new MTest());
		root.put("services", new ServicesModels(BeansWrapper.getDefaultInstance()));

		Template template = new Template(
				"test",
				new StringReader(
						"<#assign x= utils.stringToClass('MainFreeMarker$MTest')><#assign y=utils.get(x).toto(country)>\nWelcome ${utils.find(y.id).city} ${services['toto']}"),
				cfg);

		Writer out = new OutputStreamWriter(System.out);
		template.process(root, out);

	}

	public static class ServicesModels implements TemplateHashModel {
		
		BeansWrapper wrapper;
		
		public ServicesModels(BeansWrapper wrapper) {
	        this.wrapper = wrapper;
	    }
		
		@Override
		public TemplateModel get(String key) throws TemplateModelException {
			System.out.println(key);
			return wrapper.wrap(new MTest());
		}
		
		@Override
		public boolean isEmpty() throws TemplateModelException {
			return false;
		}
		
	}
	
	public static class MTest {

		public Class<?> stringToClass(String className) throws Exception {
			Class<?> clazz = this.getClass().getClassLoader()
					.loadClass(className);
			return clazz;
		}
		
		public <E extends IComponent> MTest get(Class<E> clazz) {
			System.out.println("get "+clazz);
			return this;
		}

		public <E extends IComponent> IZip find(IId id) {
			System.out.println(id.getClass());
			return toto(null);
		}
		
		public IZip toto(ICountry country) {
			IZip zip = ComponentFactory.getInstance()
					.createInstance(IZip.class);
			zip.setId(new IdRaw("123456789"));
			zip.setCity("Valence");
			zip.setZip("26000");
			zip.setCountry(country);
			return zip;
		}

	}

}
