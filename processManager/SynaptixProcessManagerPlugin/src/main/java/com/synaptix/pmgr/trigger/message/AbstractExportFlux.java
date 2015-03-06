package com.synaptix.pmgr.trigger.message;

import java.io.File;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.pmgr.model.ISendInformation;
import com.synaptix.pmgr.model.annotation.Flux;

public abstract class AbstractExportFlux<E extends IEntity> extends AbstractFlux implements IExportFlux {

	private static final long serialVersionUID = 1715319449934467194L;

	private File file;

	private String extension;

	private E entity;

	private ISendInformation sendInformation;

	public AbstractExportFlux() {
		super();

		Flux fluxAnnotation = this.getClass().getAnnotation(Flux.class);
		if (fluxAnnotation != null) {
			String ext = fluxAnnotation.extension();
			if ((ext != null) && (!ext.isEmpty())) {
				if (ext.charAt(0) != '.') {
					ext = "." + ext;
				}
				setExtension(ext);
			}
		}
	}

	public final void setFile(File file) {
		setFilename(file.getName());
		this.file = file;
	}

	public final File getFile() {
		return file;
	}

	public String getExtension() {
		return extension;
	}

	public final void setExtension(String extension) {
		this.extension = extension;
	}

	public final E getEntity() {
		return entity;
	}

	public final void setEntity(E entity) {
		this.entity = entity;
	}

	public final ISendInformation getSendInformation() {
		if (this.sendInformation == null) {
			this.sendInformation = ComponentFactory.getInstance().createInstance(ISendInformation.class);
		}
		return this.sendInformation;
	}
}
