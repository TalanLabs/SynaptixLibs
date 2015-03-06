package com.synaptix.sender;

import java.io.File;

public class Attachment {

	private String fileName;

	private String type;

	private File file;

	public Attachment(String fileName, String type, File file) {
		super();
		this.fileName = fileName;
		this.type = type;
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getType() {
		return type;
	}

	public File getFile() {
		return file;
	}
}
