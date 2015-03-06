
public class MainToto {

	private ThreadLocal<String> toto = new ThreadLocal<String>();
	
	public void set(String value) {
		toto.set(value);
	}
	
	public String get() {
		return toto.get();
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		final MainToto mainToto = new MainToto();
		mainToto.set("gaby");
		System.out.println(mainToto.get());
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				mainToto.set("guil");
				System.out.println(mainToto.get());
			}
		}).start();
		
		Thread.sleep(10000);
		System.out.println(mainToto.get());
	}

}
