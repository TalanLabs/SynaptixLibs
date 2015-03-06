import it.sauronsoftware.cron4j.Scheduler;

import java.util.Date;

public class MainCron4j {

	public static void main(String[] args) {
		// Creates a Scheduler instance.
		Scheduler s = new Scheduler();
		s.start();

		// Schedule a once-a-minute task.
		s.schedule("30 23 * * *", new Runnable() {
			public void run() {
				System.out.println("Coucou...");
				System.out.println(new Date());
			}
		});
		s.schedule("*/1 * * * *", new Runnable() {
			public void run() {
				System.out.println("Rien...");
				System.out.println(new Date());
			}
		});
		// Starts the scheduler.

		// Will run for ten minutes.
		try {
			Thread.sleep(1000L * 60L * 10L);
		} catch (InterruptedException e) {
			;
		}
		// Stops the scheduler.
		s.stop();
	}

}
