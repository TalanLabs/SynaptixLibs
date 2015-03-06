package com.synaptix.core.io;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.synaptix.client.view.io.StatsInput;
import com.synaptix.core.utils.XmlObjectUtils;

public class XmlStatsInput implements StatsInput {

	private Map<String, Object> map;

	public XmlStatsInput(Element root) {
		map = new HashMap<String, Object>();
		if (root != null) {
			NodeList nodeList = root.getElementsByTagName("Stat");
			if (nodeList != null && nodeList.getLength() > 0) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					String id = node.getAttributes().getNamedItem("id")
							.getTextContent();
					String clazz = node.getAttributes().getNamedItem("class")
							.getTextContent();
					if (clazz != null && !clazz.isEmpty()) {
						String text = node.getTextContent();
						try {
							Object res = XmlObjectUtils.stringToObject(text);
							map.put(id, res);
						} catch (Exception e) {
							map.put(id, null);
						}
					} else {
						map.put(id, null);
					}
				}
			}
		}
	}

	public Boolean readBoolean(String id) {
		return (Boolean) readObject(id);
	}

	public Byte readByte(String id) {
		return (Byte) readObject(id);
	}

	public Double readDouble(String id) {
		return (Double) readObject(id);
	}

	public Float readFloat(String id) {
		return (Float) readObject(id);
	}

	public Integer readInteger(String id) {
		return (Integer) readObject(id);
	}

	public Long readLong(String id) {
		return (Long) readObject(id);
	}

	public Object readObject(String id) {
		return map.get(id);
	}

	public String readString(String id) {
		return (String) readObject(id);
	}
}
