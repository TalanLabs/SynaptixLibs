package com.synaptix.widget.filefilter.view;

import java.io.File;
import java.io.FileFilter;

public interface IFileFilter extends FileFilter {

	public abstract String getDescription();

	public File format(File f);

}
