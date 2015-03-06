package com.synaptix.swing.utils.formatter;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Permet d'avoir un choix mutliple pour le formattage auto des dates
 * 
 * @author PGAE02801
 * 
 */
public class MultiDateFormat extends DateFormat {

	private static final long serialVersionUID = 4304931477234986013L;

	private final DateFormat[] dfs;

	/**
	 * La premiere dete sera toujours utilisée pour le format
	 * 
	 * @param dfs
	 */
	public MultiDateFormat(DateFormat... dfs) {
		super();

		this.dfs = dfs;
	}

	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition fieldPosition) {
		return dfs[0].format(date, toAppendTo, fieldPosition);
	}

	public Date parse(String source, ParsePosition pos) {
		Date res = null;
		int i = 0;
		while (i < dfs.length && res == null) {
			res = dfs[i].parse(source, pos);
			i++;
		}
		return res;
	}
}