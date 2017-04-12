package com.synaptix.pmgr.trigger.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.pmgr.trigger.listener.IMessageListener;

public abstract class AbstractFlux implements Serializable, IFlux {

	private static final long serialVersionUID = 548892600102887849L;

	protected static final Log LOG = LogFactory.getLog(AbstractFlux.class);

	private String filename;

	private Map<String, Object> extras;

	private List<IMessageListener> messageListenerList;

	private int retry;

	public AbstractFlux() {
		super();

		this.retry = 0;
	}

	/**
	 * Called by the RetryAgent
	 * 
	 * @return
	 */
	public boolean retry() {
		retry++;
		return retry < 6;
	}

	/**
	 * Get the number of retries by the RetryAgent
	 * 
	 * @return
	 */
	public int getRetryNumber() {
		return retry;
	}

	/**
	 * There is no need to set filename when using setFile from import flux
	 *
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	public Map<String, Object> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}

	@Override
	public void addMessageListener(IMessageListener messageListener) {
		addMessageListener(-1, messageListener);
	}

	@Override
	public void addMessageListener(int index, IMessageListener messageListener) {
		if (retry > 0) {
			return;
		}
		if (messageListenerList == null) {
			messageListenerList = new ArrayList<IMessageListener>();
		}
		if (index >= 0) {
			messageListenerList.add(index > messageListenerList.size() ? messageListenerList.size() : index, messageListener);
		} else {
			messageListenerList.add(messageListener);
		}
	}

	@Override
	public void fireMessageListener(IMessageListener.Status status) {
		if ((messageListenerList != null) && (!messageListenerList.isEmpty())) {
			for (IMessageListener messageListener : messageListenerList) {
				messageListener.messageTreated(status);
			}
		}
	}
}
