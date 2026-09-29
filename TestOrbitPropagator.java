import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;

public class TestOrbitPropagator {
	private final LocalDateTime sim_date = LocalDateTime.parse("2026-09-08T00:00:00");
	private final Vector3D test_pos_1 = new Vector3D(1.456072367818664E11, -3.899855645378449E10, 1.293038526497781E6);
	private final Vector3D test_pos_2 = new Vector3D(6.447460119084607E10, 2.194488597051807E11, 3.017912932031661E9);
	
	private final Vector3D ellipse_vel_1 = new Vector3D(7.234021363926447E3, 2.867004543662078E04, -7.039925391136848E-1);
	private final Vector3D ellipse_vel_2 = new Vector3D(-2.232817969784878E4, 8.8896819703388818E3, 7.337904066356611E2);
	private final Vector3D parabola_vel = new Vector3D(41962.81765085511, 0, 0);
	private final Vector3D hyperbola_vel = new Vector3D(7E4, 2E04, -7.E04);

	private final SpaceObject sun = new SpaceObject(1.988475e30, null, Vector3D.ORIGIN, Vector3D.ORIGIN);

	// Planets that orbit the Sun (Earth and Mars)
	private final SpaceObject planet_1 = new SpaceObject(5.9722e24, sun, test_pos_1, ellipse_vel_1);
	private final SpaceObject planet_2 = new SpaceObject(6.417E23, sun, test_pos_2, ellipse_vel_2);
	
	// Planet floating alone in space
	private final SpaceObject planet_3 = new SpaceObject(6.417E23, null, new Vector3D(12E18, 0, 0), Vector3D.ORIGIN);
	
	private final OrbitPropagator test_gator = new OrbitPropagator(sim_date, 3600);


	@Test
	public void testConstructor_goodInputs() {
		OrbitPropagator gator = new OrbitPropagator(sim_date, 1800);
		assertEquals(gator.getTimestep(), 1800);
	}
	
	@Test
	public void testConstructor_badDate() {
		assertThrows(NullPointerException.class, () -> new OrbitPropagator(null, 3600));
	}
	
	@Test
	public void testConstructor_badTimestep() {
		OrbitPropagator gator = new OrbitPropagator(sim_date, -10);
		assertEquals(gator.getTimestep(), 3600);
	}
	
	@Test
	public void testFindLaunchWindow_systemRelative() {
//		Record the current state of each SpaceObject
		Vector3D[] state_1 = planet_1.getState();
		Vector3D[] state_2 = planet_2.getState();

		test_gator.findLaunchWindow(planet_1, planet_2);
		
//		Ensure that both SpaceObjects were returned to their initial states
		assertEquals(state_1[0], planet_1.getPosition());
		assertEquals(state_1[1], planet_1.getVelocity());
		assertEquals(state_2[0], planet_2.getPosition());
		assertEquals(state_2[1], planet_2.getVelocity());
	}
	
	@Test
	public void testFindLaunchWindow_notSystemRelative() {
		assertThrows(IllegalArgumentException.class, () -> test_gator.findLaunchWindow(planet_1, planet_3));
	}
	
	@Test
	public void testDateToState_validDate() {
		LocalDateTime test_date = LocalDateTime.parse("2026-09-15T00:00:00");
		Vector3D[] current_state = planet_1.getState();
		Vector3D[] end_state = test_gator.dateToState(planet_1, test_date);
		
		assertNotEquals(current_state[0], end_state[0]);
		assertNotEquals(current_state[1], end_state[1]);
	}
	
	@Test
	public void testDateToState_invalidDate() {
		LocalDateTime test_date = LocalDateTime.parse("1926-09-15T00:00:00");
		assertThrows(IllegalArgumentException.class, () ->  test_gator.dateToState(planet_1, test_date));
	}
}
