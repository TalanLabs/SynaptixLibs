package com.synaptix.swing.search.filter;

import java.io.Serializable;
import java.util.Date;

public class DefaultDate implements Serializable {
	
	private static final long serialVersionUID = -6279691912169321931L;

	private Date date;

	private boolean today;

	public DefaultDate(Date date, boolean today) {
		super();
		this.date = date;
		this.today = today;
	}

	public Date getDate() {
		return date;
	}

	public boolean isToday() {
		return today;
	}
}
