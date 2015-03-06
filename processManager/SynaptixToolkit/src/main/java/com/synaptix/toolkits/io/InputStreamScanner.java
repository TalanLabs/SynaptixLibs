/*
 * Fichier : InputStreamSupervisor.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class InputStreamScanner {
	InputStreamScanListener listener;
	boolean isActive;
	Thread watcherThread;
	/**
	 * 
	 */
	public InputStreamScanner(InputStream is,InputStreamScanListener listener,boolean ignoreEOF) {
		this.listener = listener;
		Watcher watcher= new Watcher(this,is,1024,ignoreEOF);
		watcherThread = new Thread(watcher);
	}

	public void activate(){
		isActive = true;	
		watcherThread.start();	
	}
	public void deactivate(){
		isActive=false;
		watcherThread.interrupt();
	}

	public void setListener(InputStreamScanListener listener){
		this.listener = listener;
	}
	
	public void notifyData(byte []data,int offset,int len){
		listener.data(data,offset,len);
	}
	
	public void notifyEOD(byte []data,int offset,int len){
		listener.EOD(data,offset,len);
	}
	
	public static class Watcher implements Runnable {
		InputStreamScanner supervisor;
		int buffer_len;
		InputStream is;
		boolean ignoreEOF;
		public Watcher (InputStreamScanner supervisor, InputStream is, int buffer_len, boolean ignoreEOF){
			this.supervisor = supervisor;
			this.is = is;
			this.buffer_len =  buffer_len;
			this.ignoreEOF = ignoreEOF;
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			byte[]buff = new byte[buffer_len];
			int readlen=0;
			try{
				while ((((readlen=is.read(buff,0,buffer_len))>0)||(ignoreEOF))&&(supervisor.isActive)){
					if (readlen>0){
						supervisor.notifyData(buff,0,readlen);	
					} else {
						Thread.sleep(1000);
					}
				}
				
			} catch (IOException ex){
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				// TODO: handle exception
				ex.printStackTrace();
			}finally {
				System.out.println("Remains : "+readlen);
				//if (readlen>0)
					supervisor.notifyEOD(buff,0,0);
			}
		}

	}
	
	
	public static void main(String []args){
		InputStreamScanListener isl = new InputStreamScanListener() {
			public void data(byte[] data, int offset, int len) {
				System.out.println(">DATA>"+new String(data,offset,len));
			}

			public void EOD(byte[] data, int offset, int len) {
				System.out.println(">EOD>"+new String(data,offset,len)+"<EOD<");
			}
		};
		
//		isl= new TokenInputStreamScanListener(new TokenInputStreamScanListener.TokenListener() {
//			public void token(String token) {
//				System.out.println("TOKEN["+token+"]");
//			}
//
//			public void end() {
//				System.out.println("END");
//			}
//			/* (non-Javadoc)
//			 * @see com.synaptix.toolkits.io.TokenInputStreamScanListener.TokenListener#separator(byte)
//			 */
//			public void separator(byte sep) {
//				System.out.println("SEP(0x"+Integer.toHexString(sep)+")");
//			}
//
//		},"\n\r \t");
		
		try {
			InputStreamScanner iss = new InputStreamScanner(new FileInputStream(args[0]),isl,true);
			iss.activate();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
