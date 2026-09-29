import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class TestSpaceObject {
	private static final double grav_const = 6.6743015 * Math.pow(10, -11);
	
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

	
	@Test
	public void testOrbitDistance_orbitingBody() {
		Vector3D orbit_distance = planet_1.orbitDistance();
		double distance_mag = orbit_distance.getMagnitude();
		
		assertTrue("SpaceObject in an orbit should have positive distance to body. Instead, distance is: " + distance_mag, distance_mag > 0);
	}
	
	@Test
	public void testOrbitDistance_floatingBody() {
		Vector3D orbit_distance = planet_3.orbitDistance();
		double distance_mag = orbit_distance.getMagnitude();
		
		assertEquals(0.0, distance_mag, 0.0);
	}
	
	@Test
	public void testComputeMu_realMass() {
		SpaceObject planet = new SpaceObject(5.9722e24, sun, test_pos_1, ellipse_vel_1);
		double mu_value = planet.getMu();
		
		assertTrue("SpaceObject should have a Mu value greater than 0. Instead, the Mu value is: " + mu_value, mu_value > 0);
	}
	
	@Test
	public void testComputeMu_badMass() {
		assertThrows(IllegalArgumentException.class, () -> SpaceObject.computeMu(-5.9722e24));
	}
	
	@Test
	public void testCurrentCentralAngle_twoOrbiting() {
		double central_angle = planet_1.currentCentralAngle(planet_2);
		
		assertTrue("Central angle between bodies appears to be: " + central_angle + " it should be between 0 and 2 Pi", 0 < central_angle && central_angle < 2 * Math.PI);
	}
	
	@Test
	public void testCurrentCentralAngle_nonRelative() {
		double central_angle = planet_1.currentCentralAngle(planet_3);
		
		assertEquals(0.0, central_angle, 0.0);
	}
	
	@Test
	public void testOrbitDistance_hasOrbit() {
		Vector3D orbit_distance = planet_1.orbitDistance();
		double distance_mag = orbit_distance.getMagnitude();
		
		assertTrue("The orbit distance of a SpaceObject with an assigned OrbitedBody should be a positive value. Instead it is: " + distance_mag, distance_mag > 0);
	}
	
	@Test
	public void testOrbitDistance_noOrbit() {
		Vector3D orbit_distance = planet_3.orbitDistance();
		double distance_mag = orbit_distance.getMagnitude();
		
		assertEquals(0.0, distance_mag, 0.0);
	}

	@Test
	public void testTransitTime_hasOrbit() {
		double transit_time = planet_1.estimateTransitTime(planet_2);
		
		assertTrue("Transit time should be a positive number, but instead is: " + transit_time, transit_time > 0);

	}
	
	@Test
	public void testTransitTime_noOrbit() {
		double transit_time = planet_2.estimateTransitTime(planet_3);
		
		assertEquals(0.0, transit_time, 0.0);

	}

	
}
