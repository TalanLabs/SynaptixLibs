package com.synaptix.constants.shared;

import java.util.Map;

public interface ConstantsWithLookingBundle extends ConstantsBundle {

	public String getString(String key);

	public String getString(String key, Object... args);

	public Integer getInt(String key);

	public Boolean getBoolean(String key);

	public Double getDouble(String key);

	public Float getFloat(String key);

	public String[] getStringArray(String key);

	public Map<String, String> getMap(String key);

}
