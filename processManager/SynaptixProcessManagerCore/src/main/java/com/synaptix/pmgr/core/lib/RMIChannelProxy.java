package com.synaptix.pmgr.core.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.HandleReport;
import com.synaptix.pmgr.core.apis.PluggableChannel;
import com.synaptix.pmgr.core.apis.RemoteMessageHandler;
import com.synaptix.pmgr.core.lib.handlereport.BaseDelayedHandleReport;
import com.synaptix.pmgr.core.lib.handlereport.BaseRemoteHandleReport;

public class RMIChannelProxy extends AbstractChannel implements PluggableChannel {
	RemoteMessageHandler remoteHandler;
	File filebase;
	String bindname;

	private boolean overLoaded = false;
	private boolean busy = false;

	/**
	 * @param name
	 * @param isProxy
	 */
	public RMIChannelProxy(String name, String bindname, File filebase, Engine engine) {
		super(name, engine);
		this.bindname = bindname;
		this.filebase = filebase;
		if (!filebase.exists()) {
			filebase.mkdirs();
		}
		rebind();
	}

	private void rebind() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new PESecurityManager());
		}
		try {
			remoteHandler = (RemoteMessageHandler) Naming.lookup(bindname);

			System.out.println(remoteHandler.toString());
			System.out.println("Channel proxy bound on " + bindname);
			retryFailures();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr.api.Channel#recieve(java.lang.Object)
	 */
	@Override
	public HandleReport acceptMessage(Object message, ChannelSlot slot) {
		Log logger = slot.getLogger();
		if (logger != null) {
			logger.trace("Envoi RMI du message " + message);
		}
		// System.out.println("Proxying ["+message+"]");
		try {
			if (remoteHandler != null) {
				if (overLoaded) {
					if (logger != null) {
						logger.warn("CHANNEL_OVERLOAD :" + getName() + " La channel distante est surchargée");
					} else {
						System.out.println("CHANNEL_OVERLOAD :" + getName() + " La channel distante est surchargée");
					}
				} else {
					remoteHandler.remoteHandle(getName(), message);
					return new BaseRemoteHandleReport("00000", slot, getBindName());
				}
			}
		} catch (RemoteException e) {
			if (logger != null) {
				logger.warn("RMICNX : Perte de la connexion au service distant " + bindname);
				logger.error("acceptMessage", e);
			} else {
				System.out.println("Remote handler is unreachable !");
				e.printStackTrace();
			}
		}
		if (logger != null) {
			logger.info("Le service distant " + bindname + " est injoignable.");
		} else {
			System.out.println("Le service distant " + bindname + " est injoignable.");
		}
		remoteHandler = null;
		storeFailure(message);
		rebind();
		return new BaseDelayedHandleReport("00000", slot);
	}

	// TODO : cas d'erreur possible si 2 erreurs simultanees
	public void storeFailure(Object message) {
		File stored = new File(filebase, Long.toString(System.currentTimeMillis()) + ".queued");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stored));
			try {
				oos.writeObject(message);
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void retryFailures() {
		Log logger = getProcessEngine().getLogger();
		File[] fails = filebase.listFiles();
		for (int i = 0; i < fails.length; i++) {
			if (overLoaded) {
				if (logger != null) {
					logger.warn("CHANNEL_OVERLOAD :" + getName() + " La channel distante est surchargée");
				} else {
					System.out.println("CHANNEL_OVERLOAD :" + getName() + " La channel distante est surchargée");
				}
				return;
			}
			File f = fails[i];
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				try {
					Object message = ois.readObject();
					if (remoteHandler != null) {
						remoteHandler.remoteHandle(getName(), message);
					}

				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			f.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		try {
			return remoteHandler.isAvailable(getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void setAvailable(boolean available) {
		// ?!
	}

	@Override
	public int getNbWorking() {
		return -1; // ?!
	}

	@Override
	public int getNbWaiting() {
		return -1; // ?!
	}

	public String getBindName() {
		return bindname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return false;
	}

	public void remoteIsOverLoaded() {
		overLoaded = true;
	}

	public void remoteIsUnderLoaded() {
		overLoaded = false;
		retryFailures();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.PluggableChannel#isOverloaded()
	 */
	@Override
	public boolean isOverloaded() {
		return overLoaded;
	}

	public void remoteIsIdling() {
		busy = false;
	}

	public void remoteIsBusy() {
		busy = true;
	}

	@Override
	public boolean isBusy() {
		return busy;
	}
}
