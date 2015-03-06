package com.synaptix.widget.filefilter.view;

import java.io.File;

public abstract class AbstractFileFilter implements IFileFilter {

	private final String extension;

	public AbstractFileFilter(String extension) {
		super();
		this.extension = extension;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || (f.isFile() && f.getName().toLowerCase().endsWith(extension));
	}

	@Override
	public File format(File f) {
		if (!f.getName().toLowerCase().endsWith(extension)) {
			return new File(new StringBuilder().append(f.getAbsolutePath()).append(extension).toString());
		}
		return f;
	}
}
