import java.lang.management.ManagementFactory;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MXBean;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class MainMBean {

	public static void main(String[] args) throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("com.example:type=Hello");
		Hello mbean = new Hello();
		mbs.registerMBean(mbean, name);

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);
	}

	@MXBean
	public interface HelloMBean {

		public void sayHello();

		public int add(int x, int y);

		public String getName();

		public int getCacheSize();

		public void setCacheSize(int size);
	}

	public static class Hello extends NotificationBroadcasterSupport implements HelloMBean {

		private final String name = "Reginald";

		private int cacheSize = DEFAULT_CACHE_SIZE;

		private static final int DEFAULT_CACHE_SIZE = 200;

		private long sequenceNumber = 1;

		public Hello() {
			super(new MBeanNotificationInfo(new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE, "Hello" }, "toto", "Changement d'attributs"));
		}

		public void sayHello() {
			System.out.println("hello, world");

			Notification n = new Notification("Hello", this, sequenceNumber++);
			sendNotification(n);
		}

		public int add(int x, int y) {
			return x + y;
		}

		public String getName() {
			return this.name;
		}

		public int getCacheSize() {
			return this.cacheSize;
		}

		public synchronized void setCacheSize(int size) {
			int oldSize = this.cacheSize;
			this.cacheSize = size;
			System.out.println("Cache size now " + this.cacheSize);

			Notification n = new AttributeChangeNotification(this, sequenceNumber++, System.currentTimeMillis(), "CacheSize changed", "CacheSize", "int", oldSize, this.cacheSize);

			sendNotification(n);
		}
	}
}
