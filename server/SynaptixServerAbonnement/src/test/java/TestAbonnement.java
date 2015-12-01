import org.junit.Test;

import com.synaptix.server.abonnement.AbonnementManager;
import com.synaptix.server.abonnement.NotFoundAbonnementException;

import junit.framework.TestCase;
import test.abonnement.GabyAbonnement;

public class TestAbonnement extends TestCase {

	@Test
	public void testAbonnement1() {
		try {
			AbonnementManager.getInstance();
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	@Test
	public void testServerService2() {
		try {
			AbonnementManager.getInstance().getAbonnement("notExist");
			assertTrue(false);
		} catch (Exception e) {
			assertEquals(e.getClass(), NotFoundAbonnementException.class);
		}

		try {
			assertNotNull(AbonnementManager.getInstance().getAbonnement("gaby"));
		} catch (Exception e) {
		}
	}

	@Test
	public void testServerService3() {
		try {
			assertEquals(GabyAbonnement.class, AbonnementManager.getInstance()
					.getAbonnement("gaby").getClass());
		} catch (Exception e) {
		}
	}
}
