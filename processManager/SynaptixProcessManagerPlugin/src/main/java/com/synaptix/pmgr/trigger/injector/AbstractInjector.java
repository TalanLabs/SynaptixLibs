package com.synaptix.pmgr.trigger.injector;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.pmgr.helper.IncrementHelper;
import com.synaptix.pmgr.model.annotation.Flux;
import com.synaptix.pmgr.trigger.message.AbstractImportFlux;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask.FileTriggerEvent;

/**
 * An abstract injector used to import flux<br>
 * Use annotation ImportFlux to define a root work directory
 * 
 * @param M
 *            Message class
 * @author Nicolas P
 * 
 */
public abstract class AbstractInjector<M extends AbstractImportFlux> extends AbstractMsgInjector implements IInjector<M> {

	private static final Log LOG = LogFactory.getLog(AbstractInjector.class);

	private final Class<M> fluxClass;

	private final String retryPath;

	private final String rejectedPath;

	public AbstractInjector(Class<M> fluxClass, String pluginWorkdir) {
		super();

		this.fluxClass = fluxClass;

		this.retryPath = new StringBuilder(pluginWorkdir).append(getName()).append(File.separator).append("retry").append(File.separator).toString();
		this.rejectedPath = new StringBuilder(pluginWorkdir).append(getName()).append(File.separator).append("rejected").append(File.separator).toString();
		setWorkDir(new File(new StringBuilder(pluginWorkdir).append(getName()).toString()));
	}

	/**
	 * Name of the directory, also used for receiving messages using t_jms_binding
	 * 
	 * @return directory name
	 */
	@Override
	public final String getName() {
		Flux fluxAnnotation = fluxClass.getAnnotation(Flux.class);
		if (fluxAnnotation != null) {
			return fluxAnnotation.fluxCode();
		}
		return null;
	}

	/**
	 * Called when a file is injected
	 */
	@Override
	public final Object inject(FileTriggerEvent evt) {
		File file = (File) evt.getAttachment();
		if ((evt instanceof FolderEventTriggerTask.NewFileTriggerEvent) && (file.getParentFile().equals(getWorkDir()))) {
			try {
				injectMessage(null, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public final void injectMessage(String content, File file) {
		M flux = createFlux();
		flux.setContent(content);
		flux.setFile(file);
		handleFlux(flux);
	}

	protected abstract void handleFlux(M flux);

	protected final void rejectFlux(M flux, Exception e) {
		if (flux.getFile() == null) {
			String content = null;
			try {
				content = flux.getContent();
			} catch (Exception ex) {
				// do nothing
			}
			if (content != null) { // if content is null, it may come from recycling
				File file = new File(rejectedPath, getName() + "_" + IncrementHelper.getInstance().getUniqueDate() + ".xml");
				FileOutputStream ou = null;
				try {
					ou = new FileOutputStream(file, false);
					try {
						ou.write(content.getBytes("UTF-8"));
					} catch (Exception e1) {
						LOG.error("EXCEPTION WRITE", e);
					} finally {
						ou.close();
					}
				} catch (Exception e3) {
					LOG.error("EXCEPTION FILE", e);
				}
				flux.setFile(file);
			} else {
				LOG.error("Null content");
			}
		}
		if (flux.getFile() != null) {
			retryFile(flux.getFile(), flux);
		}
		LOG.error("EXCEPTION - Flux not played", e);
	}

	protected final void retryFile(File file, M message) {
		File destination = new File(new StringBuilder(retryPath).append(File.separator).append(file.getName()).toString());
		boolean success = file.renameTo(destination);
		if (success) {
			message.setFile(destination);
		}
	}

	@Override
	public long getDelay() {
		return 20000;
	}

	public abstract M createFlux();

}
