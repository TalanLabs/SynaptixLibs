/*
 * Fichier : DynamicString.java
 * Projet  : stxToolkit
 * Date    : 2 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * 
 * @since 19 nov. 02 12:16:55
 * @author Synaptix 2002
 */
public class DynamicString {

	DynamicNode[] nodes;

	public DynamicString(String str, String sepleft, String sepright){
		nodes = parse(str,sepleft,sepright);
	}

	DynamicNode[] parse(String str,String sepleft, String sepright){
		ArrayList arr = new ArrayList();

		int sepleftlen = sepleft.length();
		int seprightlen = sepright.length();

		String buffer = "";
		boolean end = false;
		int idxS = 0;
		int curpos = 0;
		while(!end){
			idxS = str.indexOf(sepleft,curpos);
			if(idxS != -1){
				buffer = str.substring(curpos,idxS);
				arr.add(new StaticNode(buffer));
				curpos = idxS+seprightlen;
				int idxE = str.indexOf(sepright,curpos);
				if (idxE>-1){
					String key = str.substring(idxS+sepleftlen,idxE).trim();
					arr.add(createNode(key));
					curpos = idxE+seprightlen;
				} else 
					end = true;			
			}else{
				end = true;
			}
		}
		if (curpos<str.length())
			arr.add(new StaticNode(str.substring(curpos)));
		
		return (DynamicNode[]) arr.toArray(new DynamicNode[0]);
	}

	public String toString(Map map){
		StringBuffer sb=new StringBuffer();
		for (int i=0; i<nodes.length; i++){
			nodes[i].evaluateInStringBuffer(map,sb);
		}
		return sb.toString();
	}
	public String toString(){
		StringBuffer sb=new StringBuffer();
		Map map = new HashMap();
		for (int i=0; i<nodes.length; i++){
			nodes[i].evaluateInStringBuffer(map,sb);
		}
		return sb.toString();
	}

	interface DynamicNode {
		void evaluateInStringBuffer(Map map, StringBuffer sb);
		void evaluateInStringBuffer(StringBuffer sb);
	}
	class StaticNode implements DynamicNode{
		String string;
		StaticNode (String str){
			string = str;
		}
		/**
		 * @see stx.co.libs.sql.DynamicString.DynamicNode#evaluateInStringBuffer(Map, StringBuffer)
		 */
		public void evaluateInStringBuffer(Map map, StringBuffer sb) {
			sb.append(string);
		}
		/* (non-Javadoc)
		 * @see com.synaptix.toolkits.text.DynamicString.DynamicNode#evaluateInStringBuffer(java.lang.StringBuffer)
		 */
		public void evaluateInStringBuffer(StringBuffer sb) {
			sb.append(string);
		}

	}
	class VariableNode implements DynamicNode{
		String var;
		VariableNode (String v){
			var = v;
		}
		/**
		 * @see stx.co.libs.sql.DynamicString.DynamicNode#evaluateInStringBuffer(Map, StringBuffer)
		 */
		public void evaluateInStringBuffer(Map map, StringBuffer sb) {
			Object obj = map.get(var);
			sb.append((obj!=null)?obj.toString():"$$"+var+"$$");
		}
		public void evaluateInStringBuffer(StringBuffer sb) {
			sb.append("$$"+var+"$$");
		}
	}
	class FunctionNode implements DynamicNode{
		String var;
		StringFunction func;
		FunctionNode (String v, StringFunction f){
			var = v;
			func = f;
		}
		/**
		 * @see stx.co.libs.sql.DynamicString.DynamicNode#evaluateInStringBuffer(Map, StringBuffer)
		 */
		public void evaluateInStringBuffer(Map map, StringBuffer sb) {
			Object obj = map.get(var);
			sb.append((obj!=null)?func.apply(obj.toString()):"$$"+var+"$$");
		}
		/* (non-Javadoc)
		 * @see com.synaptix.toolkits.text.DynamicString.DynamicNode#evaluateInStringBuffer(java.lang.StringBuffer)
		 */
		public void evaluateInStringBuffer(StringBuffer sb) {
			sb.append("$$"+var+"$$");
		}

	}

	interface StringFunction{
		public String apply(String str);
	}

	class QuoteEscaper implements StringFunction{
		public String apply(String str){
			return StringEscapeUtils.escapeSql(str);
		}
	}

	DynamicNode createNode(String var){
		StringTokenizer st = new StringTokenizer(var,":");
		String str1 = st.nextToken();
		if (st.hasMoreTokens()){
			String str2 = st.nextToken();
			if (str1.equals("ESC"))
				return new FunctionNode(str2,new QuoteEscaper());
			else 
				return new VariableNode(str2);
		} else {
			return new VariableNode(str1);
		}
	}	

	public String[] getKeys(){
		ArrayList arr = new ArrayList();
		for (int i=0; i<nodes.length; i++){
			DynamicNode nd = nodes[i];
			if (nd instanceof VariableNode){
				arr.add(((VariableNode) nd).var);
			}
		}
		return (String[]) arr.toArray(new String[0]);
	}
}
