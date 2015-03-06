package com.synaptix.pmgr.trigger.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.synaptix.pmgr.trigger.message.IImportFlux;

public abstract class AbstractImportFlux extends AbstractFlux implements IImportFlux {

	private static final long serialVersionUID = -8670505943582717072L;

	private File file;

	private String content;

	public AbstractImportFlux() {
		super();
	}

	public void setFile(File file) {
		if (file != null) {
			setFilename(file.getName());
		}
		this.file = file;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public final File getFile() {
		return file;
	}

	public String getContent() throws Exception {
		if (content == null) {
			content = readFile(file);
		}
		return content;
	}

	private final String readFile(File file) throws IOException {
		String result = null;
		if (file != null) {
			FileInputStream stream = new FileInputStream(file);
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				/* Instead of using default, pass in a decoder. */
				result = Charset.forName("UTF-8").decode(bb).toString();
			} finally {
				stream.close();
			}
		}
		return result;
	}
}
