package com.synaptix.widget.perimeter.view.swing;

import java.util.Iterator;
import java.util.List;

public class RUSPerimetersHelper {

	public RUSPerimetersHelper() {
	}

	/**
	 * Vérifie filtre sur un String, mode contient
	 * 
	 * @param text
	 * @param textList
	 * @return
	 */
	public static final boolean verifyStrings(String text, List<String> textList) {
		boolean res = true;
		if (textList != null && !textList.isEmpty()) {
			boolean existe = false;
			if (text != null) {
				Iterator<String> it = textList.iterator();
				while (it.hasNext() && !existe) {
					String t = it.next();
					existe = text.toUpperCase().contains(t.toUpperCase());
				}
			}
			res = existe;
		}
		return res;
	}
}
