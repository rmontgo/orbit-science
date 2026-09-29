import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;

public class TestOrbit {
	private final Vector3D test_pos_1 = new Vector3D(1.456072367818664E11, -3.899855645378449E10, 1.293038526497781E6);
	private final Vector3D test_pos_2 = new Vector3D(6.447460119084607E10, 2.194488597051807E11, 3.017912932031661E9);
	
	private final Vector3D ellipse_vel_1 = new Vector3D(7.234021363926447E3, 2.867004543662078E04, -7.039925391136848E-1);
	private final Vector3D ellipse_vel_2 = new Vector3D(-2.232817969784878E4, 8.8896819703388818E3, 7.337904066356611E2);
	private final Vector3D parabola_vel = new Vector3D(41962.81765085511, 0, 0);
	private final Vector3D hyperbola_vel = new Vector3D(7E4, 2E04, -7.E04);

	private final SpaceObject sun = new SpaceObject(1.988475e30, null, Vector3D.ORIGIN, Vector3D.ORIGIN);
	private final SpaceObject other_sun = new SpaceObject(1.988475e30, null, new Vector3D(1E20, 0, 0), Vector3D.ORIGIN);
	private final double test_mu = sun.getMu();
		
	@Test
	public void testSpecificOrbitalEnergy_ellipticalOrbit() {
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);

		assertTrue("Specific orbital energy of elliptical orbit " + specific_orbital_energy + " is greater than or equal to 0", specific_orbital_energy < 0);
	}
	
	@Test
	public void testSpecificOrbitalEnergy_parabola() {
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);

		assertEquals(0.0, specific_orbital_energy, 1e-6);
	}
	
	@Test
	public void testSpecificOrbitalEnergy_hyperbola() {
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);

		assertTrue("Specific orbital energy of hyperbolic orbit " + specific_orbital_energy + " is less than or equal to 0", specific_orbital_energy > 0);
	}
	
	@Test
	public void testEccentricityVector_ellipticalOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		
		double eccentricity_value = eccentricity_vector.getMagnitude();
		assertTrue("Eccentricity of elliptical orbit " + eccentricity_value + " is not between 0 and 1", 0 <= eccentricity_value && eccentricity_value < 1);
	}
	
	@Test
	public void testEccentricityVector_parabola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		
		double eccentricity_value = eccentricity_vector.getMagnitude();
		assertEquals(1.0, eccentricity_value, 1e-8);
	}
	
	@Test
	public void testEccentricityVector_hyperbola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		
		double eccentricity_value = eccentricity_vector.getMagnitude();
		assertTrue("Eccentricity of hyperbolic orbit " + eccentricity_value + " is not greater than 1", eccentricity_value > 1);
	}
	
	@Test
	public void testOrbitType_ellipse() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		
		String orbit_type = Orbit.orbitType(eccentricity_vector);
		assertEquals("ellipse", orbit_type);
	}
	
	@Test
	public void testOrbitType_parabola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		
		String orbit_type = Orbit.orbitType(eccentricity_vector);
		assertEquals("parabola", orbit_type);
	}

	@Test
	public void testOrbitType_hyperbola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity_vector = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		
		String orbit_type = Orbit.orbitType(eccentricity_vector);
		assertEquals("hyperbola", orbit_type);
	}
	
	@Test
	public void testSemiMajorAxis_ellipticalOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);

		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		assertTrue("Semimajor axis of elliptical orbit " + semimajor_axis + " is less than or equal to 0", semimajor_axis > 0);
	}
	
	@Test
	public void testSemiMajorAxis_parabola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);

		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		assertTrue("Semimajor axis of parabolic orbit (AKA periapsis distance) " + semimajor_axis + " is less than or equal to 0", semimajor_axis > 0);
	}
	
	@Test
	public void testSemiMajorAxis_hyperbola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);

		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		assertTrue("Semimajor axis of hyperbolic orbit " + semimajor_axis + " is greater than or equal to 0", semimajor_axis < 0);
	}
	
	@Test
	public void testOrbitalPeriod_ellipse() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		assertTrue("Orbital period of elliptical orbit " + orbital_period + " is less than or equal to 0", orbital_period > 0);
	}
	
	@Test
	public void testOrbitalPeriod_parabola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		assertEquals(0.0, orbital_period, 0.0);
	}

	@Test
	public void testOrbitalPeriod_hyperbola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		assertEquals(0.0, orbital_period, 0.0);
	}

	@Test
	public void testMeanMotion_ellipse() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		
		double mean_motion = Orbit.meanMotion(orbital_period, semimajor_axis, test_mu, orbit_type);
		assertTrue("Mean motion of elliptical orbit " + mean_motion + " is less than or equal to 0", mean_motion > 0);
	}
	
	@Test
	public void testMeanMotion_parabola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		
		double mean_motion = Orbit.meanMotion(orbital_period, semimajor_axis, test_mu, orbit_type);
		assertEquals(0.0, mean_motion, 0.0);
	}

	@Test
	public void testMeanMotion_hyperbola() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		double orbital_period = Orbit.orbitalPeriod(test_mu, semimajor_axis, orbit_type);
		
		double mean_motion = Orbit.meanMotion(orbital_period, semimajor_axis, test_mu, orbit_type);
		assertTrue("Mean motion of hyperbolic orbit " + mean_motion + " is less than or equal to 0", mean_motion > 0);
	}
	
	@Test
	public void testComputeApoapsis_ellipticalOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D apoapsis = Orbit.computeApoapsis(eccentricity, semimajor_axis, orbit_type);
		double apoapsis_mag = apoapsis.getMagnitude();
		assertTrue("Magnitude of elliptical orbit's apoapsis " + apoapsis_mag + " is less than or equal to 0", apoapsis_mag > 0);
	}

	@Test
	public void testComputeApoapsis_parabolicOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D apoapsis = Orbit.computeApoapsis(eccentricity, semimajor_axis, orbit_type);
		double apoapsis_mag = apoapsis.getMagnitude();
		assertEquals(0.0, apoapsis_mag, 0.0);
	}
	
	@Test
	public void testComputeApoapsis_hyperbolicOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D apoapsis = Orbit.computeApoapsis(eccentricity, semimajor_axis, orbit_type);
		double apoapsis_mag = apoapsis.getMagnitude();
		assertEquals(0.0, apoapsis_mag, 0.0);
	}
	
	@Test
	public void testComputePeriapsis_ellipticalOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, ellipse_vel_1);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, ellipse_vel_1);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, ellipse_vel_1, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D periapsis = Orbit.computePeriapsis(eccentricity, semimajor_axis, orbit_type);
		double periapsis_mag = periapsis.getMagnitude();
		assertTrue("Magnitude of elliptical orbit's periapsis " + periapsis_mag + " is less than or equal to 0", periapsis_mag > 0);
	}
	
	@Test
	public void testComputePeriapsis_parabolicOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, parabola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, parabola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, parabola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D periapsis = Orbit.computePeriapsis(eccentricity, semimajor_axis, orbit_type);
		double periapsis_mag = periapsis.getMagnitude();
		assertTrue("Magnitude of parabolic orbit's periapsis " + periapsis_mag + " is less than or equal to 0", periapsis_mag > 0);
	}

	@Test
	public void testComputePeriapsis_hyperbolicOrbit() {
		Vector3D specific_angular_momentum = Orbit.specificAngularMomentum(test_pos_1, hyperbola_vel);
		Vector3D eccentricity = Orbit.eccentricityVector(test_mu, specific_angular_momentum, test_pos_1, hyperbola_vel);
		double specific_orbital_energy = Orbit.specificOrbitalEnergy(test_pos_1, hyperbola_vel, test_mu);
		String orbit_type = Orbit.orbitType(eccentricity);
		double semimajor_axis = Orbit.semiMajorAxis(test_mu, specific_orbital_energy, specific_angular_momentum, orbit_type);
		
		Vector3D periapsis = Orbit.computePeriapsis(eccentricity, semimajor_axis, orbit_type);
		double periapsis_mag = periapsis.getMagnitude();
		assertTrue("Magnitude of hyperbolic orbit's periapsis " + periapsis_mag + " is less than or equal to 0", periapsis_mag > 0);
	}
	
	@Test
	public void testIsSystemRelative_ellipses() {
		Orbit ellipse_1 = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit ellipse_2 = new Orbit(test_pos_2, ellipse_vel_2, sun);
		
		assertTrue("Two elliptical orbits around the same SpaceObject should be considered system relative", ellipse_1.isSystemRelative(ellipse_2));
	}
	
	@Test
	public void testIsSystemRelative_parabolaHyperbola() {
		Orbit parabola = new Orbit(test_pos_1, parabola_vel, sun);
		Orbit hyperbola = new Orbit(test_pos_2, hyperbola_vel, sun);
		
		assertFalse("Two non-elliptical orbits around the same SpaceObject should not be considered system relative", parabola.isSystemRelative(hyperbola));
	}
	
	@Test
	public void testIsSystemRelative_ellipseParabola() {
		Orbit ellipse = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit parabola = new Orbit(test_pos_2, parabola_vel, sun);
		
		assertFalse("An elliptical and a parabolic orbit around the same SpaceObject should not be considered system relative", parabola.isSystemRelative(ellipse));
	}
	
	@Test
	public void testIsSystemRelative_differentCenterBodies() {
		Orbit ellipse_1 = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit ellipse_2 = new Orbit(test_pos_2, ellipse_vel_2, other_sun);
		
		assertFalse("Two elliptical orbits around different SpaceObjects should not be considered system relative", ellipse_1.isSystemRelative(ellipse_2));
	}
	
	@Test
	public void testEstimateTransferTime_ellipses() {
		Orbit ellipse_1 = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit ellipse_2 = new Orbit(test_pos_2, ellipse_vel_2, sun);
		
		double transfer_time = ellipse_1.estimateTransferTime(ellipse_2);
		
		assertTrue("Transfer time between two elliptical orbits should not be less than or equal to 0.0, is currently " + transfer_time, transfer_time > 0.0);
	}
	
	@Test
	public void testEstimateTransferTime_parabolaHyperbola() {
		Orbit parabola = new Orbit(test_pos_1, parabola_vel, sun);
		Orbit hyperbola = new Orbit(test_pos_2, hyperbola_vel, sun);
		
		double transfer_time = parabola.estimateTransferTime(hyperbola);
		
		assertEquals(0.0, transfer_time, 0.0);
	}
	
	@Test
	public void testEstimateTransitAngle_ellipses() {
		Orbit ellipse_1 = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit ellipse_2 = new Orbit(test_pos_2, ellipse_vel_2, sun);
		
		double transit_angle = ellipse_1.estimateTransitAngle(ellipse_2);
		assertTrue("Transit angle between two objects orbiting the same body " + transit_angle + " should be between 0 and 2 Pi", 0 < transit_angle && transit_angle < (2 * Math.PI));

	}
	
	@Test
	public void testEstimateTransitAngle_parabolaHyperbola() {
		Orbit parabola = new Orbit(test_pos_1, parabola_vel, sun);
		Orbit hyperbola = new Orbit(test_pos_2, hyperbola_vel, sun);
		
		double transit_angle = parabola.estimateTransitAngle(hyperbola);
		assertEquals(0.0, transit_angle, 0.0);
	}
	
	@Test
	public void testTransferSMA_ellipses() {
		Orbit ellipse_1 = new Orbit(test_pos_1, ellipse_vel_1, sun);
		Orbit ellipse_2 = new Orbit(test_pos_2, ellipse_vel_2, sun);

		double transfer_sma = ellipse_1.transferSMA(ellipse_2);
		assertTrue("Transfer orbit semimajor axis " + transfer_sma + " should be greater than 0", transfer_sma > 0);
	}
	
	@Test
	public void testTransferSMA_parabolaHyperbola() {
		Orbit parabola = new Orbit(test_pos_1, parabola_vel, sun);
		Orbit hyperbola = new Orbit(test_pos_2, hyperbola_vel, sun);

		double transfer_sma = parabola.transferSMA(hyperbola);
		assertEquals(0.0, transfer_sma, 0.0);	
	}
	

}
