/*
 * Fichier : DynamicStringRepository.java
 * Projet  : stxToolkit
 * Date    : 2 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.text;

import java.util.HashMap;
import java.util.Map;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class DynamicStringRepository {
	private Map strings;
	
	public DynamicStringRepository(Map map){
		strings = map;
	}
	public DynamicStringRepository(){
		strings = new HashMap();
	}
	
	public DynamicString getDynamicString(String id){
		DynamicString ds = (DynamicString)strings.get(id);
		if(ds==null){
			ds = lookupDynamicString(id);
		}
		return ds;
	}
	
	DynamicString lookupDynamicString(String id){
		return null;
	}
	
	public String computeString(String id, Map parameters, String def){
		DynamicString ds = getDynamicString(id);
		if (ds==null){
			return def;
		} else {
			return ds.toString(parameters);
		}
	}
}
