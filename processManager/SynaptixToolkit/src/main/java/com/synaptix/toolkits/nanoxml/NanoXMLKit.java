/*
 * Created on 31 dec. 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.synaptix.toolkits.nanoxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

public class NanoXMLKit {

	public static boolean fill(XMLElement elem, InputStream is, boolean close){
		try{
			InputStreamReader reader = new InputStreamReader(is);
			elem.parseFromReader(reader);
			return true;
		} catch (IOException e) {
			return false;
		} catch (XMLParseException e) {
			e.printStackTrace();
			return false;
		}finally {
			try{
				if (close)
					is.close();
			} catch (IOException e) {
			}
		}
	}
	public static XMLElement load(InputStream is, boolean nullOnError){
		XMLElement elem = new XMLElement();
		boolean r = fill(elem,is,true);
		if ((!r)&&(nullOnError)){
			return null;
		}
		return elem;
	}
	public static XMLElement load(File file, boolean nullOnError){
		try{
			FileInputStream fis = new FileInputStream(file);
			return load(fis,nullOnError);
		} catch (FileNotFoundException e) {
			if (nullOnError) return null;
			else return new XMLElement();
		}
	}
	public static boolean fill(XMLElement elem, String ressource, ClassLoader cl){
		InputStream is = cl.getResourceAsStream(ressource);
		if (is!=null){
			return fill(elem,is,true);
		} else
		return false;
	}
	
	public static XMLElement load(String ressource, Class cls, boolean nullOnError){
		XMLElement elem = new XMLElement();
		boolean r = fill(elem,ressource,cls.getClassLoader());
		if ((!r)&&(nullOnError)){
			return null;
		}
		return elem;
	}
	public static XMLElement load(String ressource, ClassLoader cl, boolean nullOnError){
		XMLElement elem = new XMLElement();
		boolean r = fill(elem,ressource,cl);
		if ((!r)&&(nullOnError)){
			return null;
		}
		return elem;
	}

	public static XMLElement newElement(String name){
		XMLElement elem = new XMLElement();
		elem.setName(name);
		return elem;
	}
	public static XMLElement newElement(String name,String cdata){
		XMLElement elem = new XMLElement();
		elem.setName(name);
		elem.setContent(cdata);
		return elem;
	}
	
	public static XMLElement newChild(XMLElement parent,String name){
		XMLElement elem = newElement(name);
		parent.addChild(elem);
		return elem;
	}
	public static XMLElement newChild(XMLElement parent,String name,String cdata){
		XMLElement elem = newElement(name,cdata);
		parent.addChild(elem);
		return elem;
	}
	public static XMLElement findChild(XMLElement parent, String name){
		for (Enumeration e = parent.enumerateChildren(); e.hasMoreElements();){
			XMLElement c = (XMLElement) e.nextElement();
			if (c.getName().equalsIgnoreCase(name)){
				return c;
			}
		}
		return null;
	}
	
	public static String findContent(XMLElement parent, String name, String defaultValue){
		XMLElement e = findChild(parent, name);
		if (e!=null){
			return e.getContent();
		} else {
			return defaultValue;
		}
	}
	
	public static List resolveMap(XMLElement parent, String[] keys, Map map){
		List missing = new ArrayList();
		for (int i=0; i<keys.length; i++){
			String v = findContent(parent, keys[i], null);
			if (v!=null){
				map.put(keys[i],v);
			} else {
				missing.add(keys[i]);
			}
		}
		return missing;
	}
	
	
	public interface XMLElementChecker{
		public boolean check(XMLElement elem);
	}
	
	
	public static void write(Writer os, XMLElement element, String indent) throws IOException{
		os.write(indent+"<"+element.getName()+" ");
		for (Enumeration e=element.enumerateAttributeNames(); e.hasMoreElements();){
			String attr = (String) e.nextElement();
			os.write(attr+"=\"");
			os.write(element.getAttribute(attr)+"\" ");
		}
		if (element.countChildren()>0){
			os.write(">\n");
			for (Enumeration e=element.enumerateChildren(); e.hasMoreElements();){
				XMLElement el = (XMLElement) e.nextElement();
				write(os,el,"  "+indent);
			}
			if ((element.getContent()!=null)&&(!"".equals(element.getContent().trim()))){
				os.write(indent+"  <![CDATA["+element.getContent()+"]]>\n");
			}
			os.write(indent+"</"+element.getName()+">\n");
		} else {
			if ((element.getContent()!=null)&&(!"".equals(element.getContent().trim()))){
				os.write(">"+element.getContent()+"</"+element.getName()+">\n");
			} else {
				os.write("/>\n");				
			}
		}
	}

	public static void writeAsProp(Writer os, XMLElement element, String parentPath) throws IOException{
		if (element.countChildren()>0){
			for (Enumeration e=element.enumerateChildren(); e.hasMoreElements();){
				XMLElement el = (XMLElement) e.nextElement();
				writeAsProp(os,el,parentPath+element.getName()+".");
			}
		} else {
			if ((element.getContent()!=null)&&(!"".equals(element.getContent().trim()))){
				os.write(parentPath+element.getName()+"="+element.getContent()+"\n");
			}
		}
	}
	
	public static void writeToProp(Properties props, XMLElement element, String parentPath, boolean ignoreRoot){
		if (element.countChildren()>0){
			for (Enumeration e=element.enumerateChildren(); e.hasMoreElements();){
				XMLElement el = (XMLElement) e.nextElement();
				writeToProp(props,el,(ignoreRoot?"":parentPath+element.getName()+"."),false);
			}
		} else {
			if ((element.getContent()!=null)&&(!"".equals(element.getContent().trim()))){
				props.put((ignoreRoot?"":parentPath)+element.getName(),element.getContent());
			}
		}
		
	}
}
