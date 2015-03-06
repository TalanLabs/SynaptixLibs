package com.synaptix.gwt.server.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

import com.synaptix.gwt.shared.component.IComponentDto;

public class ComponentDtoFactory {

	private static Map<Class<? extends IComponentDto>, Class<? extends IComponentDto>> implMap = new HashMap<Class<? extends IComponentDto>, Class<? extends IComponentDto>>();

	/**
	 * Create a instance of interfaceClass with impl
	 * 
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <G extends IComponentDto> G createInstance(Class<G> interfaceClass) {
		if (interfaceClass == null) {
			throw new NullArgumentException("interfaceClass is null");
		}
		G res;
		Class<G> clazz = (Class<G>) implMap.get(interfaceClass);
		if (clazz == null) {
			String packageName = interfaceClass.getPackage() != null ? interfaceClass.getPackage().getName() : null;
			String simpleName = rename(interfaceClass.getSimpleName()) + "Impl";
			try {
				clazz = (Class<G>) interfaceClass.getClassLoader().loadClass(packageName != null ? new StringBuilder(packageName).append(".").append(simpleName).toString() : simpleName);
				implMap.put(interfaceClass, clazz);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			res = (G) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	private static final String rename(String name) {
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
