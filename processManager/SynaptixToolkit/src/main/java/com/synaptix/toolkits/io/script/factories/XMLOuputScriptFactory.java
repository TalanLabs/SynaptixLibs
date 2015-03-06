/*
 * Fichier : XMLOuputScriptFactory.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io.script.factories;

import java.util.Enumeration;

import nanoxml.XMLElement;

import com.synaptix.toolkits.io.script.AbstractOutputScript;
import com.synaptix.toolkits.io.script.BlockOutputScript;
import com.synaptix.toolkits.io.script.CloseOutputScript;
import com.synaptix.toolkits.io.script.OutOutputScript;
import com.synaptix.toolkits.io.script.OutputScript;
import com.synaptix.toolkits.io.script.OutputScriptFactory;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class XMLOuputScriptFactory implements OutputScriptFactory {
	static final byte[] NL = new byte[]{'\n'};
	static final byte[] CR = new byte[]{'\r'};
	
	XMLElement element;
	/**
	 * 
	 */
	public XMLOuputScriptFactory(XMLElement element) {
		this.element= element;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.io.script.OutputScriptFactory#build()
	 */
	public OutputScript build() {
		return buildBlock(element);
	}
	
	void getDelays(AbstractOutputScript script,XMLElement elem){
		long delay_before =elem.getIntAttribute("delay_before",0);
		long delay_after = elem.getIntAttribute("delay_after",0);
		script.setDelay_after(delay_after);
		script.setDelay_before(delay_before);
	}
	
	OutputScript buildBlock(XMLElement element){
		BlockOutputScript bos = new BlockOutputScript();
		getDelays(bos,element);
		for (Enumeration e  = element.enumerateChildren(); e.hasMoreElements();){
			XMLElement child = (XMLElement) e.nextElement();
			bos.appendScript(buildElement(child));		
		}
		return bos;
		
	}
	
	OutputScript buildElement(XMLElement elem){
		if (elem.getName().equals("out")){
			return buildOut(elem);
		} else if (elem.getName().equals("nl")){
			return buildNL(elem);
		} else if (elem.getName().equals("cr")){
		return buildCR(elem);
	} else if (elem.getName().equals("close")){
	return buildClose(elem);
}
		return null;
	}
	
	OutputScript buildOut(XMLElement elem){
		OutOutputScript oos = new OutOutputScript(elem.getContent().getBytes());
		getDelays(oos,elem);
		return oos;
	}
	OutputScript buildNL(XMLElement elem){
		OutOutputScript oos = new OutOutputScript(NL);
		getDelays(oos,elem);
		return oos;
	}
	OutputScript buildCR(XMLElement elem){
		OutOutputScript oos = new OutOutputScript(CR);
		getDelays(oos,elem);
		return oos;
	}
	OutputScript buildClose(XMLElement elem){
		CloseOutputScript oos = new CloseOutputScript();
		getDelays(oos,elem);
		return oos;
	}
	
	
}
