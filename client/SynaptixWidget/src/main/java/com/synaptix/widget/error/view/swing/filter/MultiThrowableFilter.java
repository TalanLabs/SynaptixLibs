package com.synaptix.widget.error.view.swing.filter;

import com.google.common.base.Preconditions;
import com.synaptix.widget.error.view.swing.IThrowableFilter;

public class MultiThrowableFilter implements IThrowableFilter {

	private IThrowableFilter[] filters;

	public MultiThrowableFilter(IThrowableFilter... filters) {
		super();
		Preconditions.checkNotNull(filters);
		Preconditions.checkArgument(filters.length > 0);

		this.filters = filters;
	}

	@Override
	public Throwable filter(Throwable t) {
		Throwable prec = t;
		int i = 0;
		while (i < filters.length) {
			IThrowableFilter filter = filters[i];
			Throwable n = filter.filter(prec);
			if (n == prec) {
				i++;
			} else {
				i = 0;
			}
			prec = n;
		}
		return prec;
	}

}
