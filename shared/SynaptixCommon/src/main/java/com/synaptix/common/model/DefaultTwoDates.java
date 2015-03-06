package com.synaptix.common.model;

import java.io.Serializable;
import java.util.Date;

public class DefaultTwoDates extends Binome<Date, Date> implements Serializable {

	private static final long serialVersionUID = 6112991274078588549L;

	public DefaultTwoDates(Date date1, Date date2) {
		super(date1, date2);
	}

	public Date getDate1() {
		return getValue1();
	}

	public Date getDate2() {
		return getValue2();
	}

	@Override
	public String toString() {
		return "( TwoDates -> date1 : " + getDate1() + " date2 : " + getDate2() + " )";
	}
}
