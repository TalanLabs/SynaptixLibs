package com.synaptix.core.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.synaptix.client.view.io.StatsOutput;
import com.synaptix.core.utils.XmlObjectUtils;

public class XmlStatsOutput implements StatsOutput {

	private Map<String, Object> map;

	public XmlStatsOutput() {
		map = new HashMap<String, Object>();
	}

	public void writeBoolean(String id, Boolean value) {
		writeObject(id, value);
	}

	public void writeByte(String id, Byte value) {
		writeObject(id, value);
	}

	public void writeDouble(String id, Double value) {
		writeObject(id, value);
	}

	public void writeFloat(String id, Float value) {
		writeObject(id, value);
	}

	public void writeInteger(String id, Integer value) {
		writeObject(id, value);
	}

	public void writeLong(String id, Long value) {
		writeObject(id, value);
	}

	public void writeObject(String id, Object value) {
		map.put(id, value);
	}

	public void writeString(String id, String value) {
		writeObject(id, value);
	}

	public String getXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<Stats>");
		for (Entry<String, Object> entry : map.entrySet()) {
			sb.append("<Stat id=\"");
			sb.append(entry.getKey());
			sb.append("\" class=\"");
			if (entry.getValue() != null) {
				sb.append(entry.getValue().getClass().getName());
			}
			sb.append("\">");
			if (entry.getValue() != null) {
				try {
					String res = XmlObjectUtils
							.objectToString(entry.getValue());
					sb.append(res);
				} catch (Exception e) {
				}
			}
			sb.append("</Stat>");
		}
		sb.append("</Stats>");
		return sb.toString();
	}
}
