// Represents the Keplerian elements of an orbit around a celestial body

import java.util.Objects;

public class Orbit {
	private final SpaceObject orbited_body;
	private final double specific_orbital_energy;
	private final Vector3D specific_angular_momentum;
	private final double orbital_period;
	private final double semimajor_axis;
	private final Vector3D eccentricity;
	private final Vector3D apoapsis;
	private final Vector3D periapsis;
	private final double mean_motion;
	private final String orbit_type;
	
	public Orbit(Vector3D position, Vector3D velocity, SpaceObject orbited_body) {
		double mu = orbited_body.getMu();
		this.orbited_body = Objects.requireNonNull(orbited_body, "Orbited body cannot be null for an Orbit");
		this.specific_angular_momentum = specificAngularMomentum(position, velocity);
		this.eccentricity = eccentricityVector(mu, this.specific_angular_momentum, position, velocity);
		this.orbit_type = orbitType(eccentricity);
		this.specific_orbital_energy = specificOrbitalEnergy(position, velocity, mu);
		this.semimajor_axis = semiMajorAxis(mu, this.specific_orbital_energy, this.specific_angular_momentum, this.orbit_type);
		this.orbital_period = orbitalPeriod(mu, this.semimajor_axis, this.orbit_type);
		this.apoapsis = computeApoapsis(this.eccentricity, this.semimajor_axis, this.orbit_type);
		this.periapsis = computePeriapsis(this.eccentricity, this.semimajor_axis, this.orbit_type);
		this.mean_motion = meanMotion(this.orbital_period, this.semimajor_axis, mu, this.orbit_type);

//		this.inclination = this.computeInclination(this.specific_angular_momentum);
	}
	
//	Get the apoapsis of this orbit
	public Vector3D getApoapsis() {
		return this.apoapsis;
	}

//	Get the periapsis of this orbit
	public Vector3D getPeriapsis() {
		return this.periapsis;
	}

//	Get the orbital period of this Orbit
	public double getOrbitalPeriod() {
		return this.orbital_period;
	}

//	Get the type of this Orbit
	public String getOrbitType() {
		return this.orbit_type;
	}
	
//	Compute the phase angle, the angle between planets measured from the sun, which allows an efficient Hohmann transfer orbit.
	public double estimateTransitAngle(Orbit target_orbit) {
		double phase_angle;
		
		if (this.isSystemRelative(target_orbit)) {
			double transfer_time = this.estimateTransferTime(target_orbit);
			
			double target_travel_angle = transfer_time * target_orbit.mean_motion;
			phase_angle = Math.abs(Math.PI - target_travel_angle);
		} else {
			phase_angle = 0.0;
		}
		
		return phase_angle;
	}

//	Estimate the time, in seconds, to traverse a Hohmann transfer between this celestial body and the target body
	public double estimateTransferTime(Orbit target_orbit) {
		double transfer_time;
	
		if (this.isSystemRelative(target_orbit)) {
			double transfer_sma = this.transferSMA(target_orbit);
			double sma_cubed = Math.pow(transfer_sma, 3);
			double orbited_mu = this.orbited_body.getMu();
			
			double fraction = sma_cubed / orbited_mu;
			double root = Math.sqrt(fraction);
			
			// Only half of the Hohmann transfer orbit is used, so transfer time is half of the orbital period
			transfer_time = Math.PI * root;
		} else {
			transfer_time = 0.0;
		}
		
		return transfer_time;
	}

//  Returns true if this celestial body and the target celestial body orbit the same object but have different orbits
	public boolean isSystemRelative(Orbit target_orbit) {
		return (this.orbited_body != null && this.orbited_body == target_orbit.orbited_body) &&
		(this.orbit_type == target_orbit.orbit_type && this.orbit_type == "ellipse") &&
		(this.semimajor_axis != target_orbit.semimajor_axis || this.eccentricity.getMagnitude() != target_orbit.eccentricity.getMagnitude());
	}

//	Compute the specific orbital energy of this celestial body using the vis-viva equation.
//	Returns 0.0 if the body does not orbit anything
	static double specificOrbitalEnergy(Vector3D position, Vector3D velocity, double mu) {		
		double velocity_squared = Math.pow(velocity.getMagnitude(), 2);
		double orbit_distance = position.getMagnitude();
		
		double first_fraction = velocity_squared / 2;
		double second_fraction = mu / orbit_distance;
		
		double orbital_energy = first_fraction - second_fraction;
		
		return orbital_energy;
	}
	
//	Compute the specific angular momentum of the orbit, and return the magnitude of this vector
	static Vector3D specificAngularMomentum(Vector3D position, Vector3D velocity) {
		Vector3D specific_angular_momentum = position.crossProduct(velocity);
		
		return specific_angular_momentum;
	}

//	Compute the semimajor axis of the orbit. Returns 0.0 if the body does not orbit anything
//	Uses a different method for computing the semimajor axis of a parabola
	static double semiMajorAxis(double mu, double specific_orbital_energy, Vector3D specific_angular_momentum, String orbit_type) {
		double sma = 0.0;
		
		if (orbit_type == "parabola") {
			sma = parabolaSMA(mu, specific_angular_momentum);
		} else {
			sma = normalSMA(mu, specific_orbital_energy);
		}
		
		return sma;
	}
	
//	Compute the orbital period, in seconds, based on the semimajor axis of the orbit (kilometers) and the mass of the body being orbited (kilograms)
//	Returns 0.0 if the provided mu is less than or equal to 0, or if the orbit type is not "ellipse"
	static double orbitalPeriod(double mu, double sma, String orbit_type) {
		double orbital_period;
		
		if (orbit_type == "ellipse") {
			double sma_cubed = Math.pow(sma, 3);
			double fraction = sma_cubed / mu;
			double root = Math.sqrt(fraction);
			
			orbital_period = 2 * Math.PI * root;
		} else {
			orbital_period = 0.0;
		}
		
		return orbital_period;		
	}
	
//	Compute the eccentricity vector of the orbit
	static Vector3D eccentricityVector(double mu, Vector3D specific_angular_momentum, Vector3D position, Vector3D velocity) {
		Vector3D vel_cross_ang_mom = velocity.crossProduct(specific_angular_momentum);
		
		Vector3D first_vector = vel_cross_ang_mom.divideScalar(mu);
		Vector3D second_vector = position.unitVector();
		
		Vector3D eccentricity_vector = first_vector.subtractVector(second_vector);
		
		return eccentricity_vector;
	}

//	Compute the apoapsis of the orbit. Parabolic and hyperbolic orbits do not have an apoapsis, so a zero vector is returned instead for these cases
	static Vector3D computeApoapsis(Vector3D eccentricity, double semimajor_axis, String orbit_type) {
		Vector3D apoapsis;
		Vector3D unit_ecc = eccentricity.unitVector();
		
		if (orbit_type == "ellipse") {
			double scalar = -1 * semimajor_axis * (1 + eccentricity.getMagnitude());
			apoapsis = unit_ecc.multiplyScalar(scalar);	
		} else {
			apoapsis = Vector3D.ORIGIN;
		}
		
		return apoapsis;
	}

//	Compute the periapsis of the orbit. Uses different formulas for each orbit type
	static Vector3D computePeriapsis(Vector3D eccentricity, double semimajor_axis, String orbit_type) {
		Vector3D periapsis;
		
		if (orbit_type == "ellipse") {
			periapsis = ellipsePeriapsis(eccentricity, semimajor_axis);
		} else if (orbit_type == "parabola") {
			periapsis = parabolaPeriapsis(eccentricity, semimajor_axis);
		} else {
			periapsis = hyperbolaPeriapsis(eccentricity, semimajor_axis);
		}
		
		return periapsis;
	}
	
//  Compute the mean angular velocity, in degrees per second, of the celestial body as it completes its orbit.
	static double meanMotion(double orbital_period, double sma, double mu, String orbit_type) {
		double mean_motion;
	
		if (orbit_type == "ellipse") {
			mean_motion = ellipseMeanMotion(orbital_period);
		} else if (orbit_type == "hyperbola") {
			mean_motion = hyperbolaMeanMotion(mu, sma);
		} else {
			mean_motion = 0.0;
		}
	
		return mean_motion;
	}

//	Compute the semi-major axis of the Hohmann transfer orbit between bodies.
//	Returns 0.0 if the celestial bodies do not orbit the same thing.
	double transferSMA(Orbit target_orbit) {
		double transfer_sma;

		if (this.isSystemRelative(target_orbit)) {
			transfer_sma = (this.semimajor_axis + target_orbit.semimajor_axis) / 2;
		} else {
			transfer_sma = 0.0;
		}
		
		return transfer_sma;
	}

//	Determine the type of orbit that this is based on its eccentricity
	static String orbitType(Vector3D eccentricity) {
		String orbit_type;
		double eccentricity_value = eccentricity.getMagnitude();
		
		if (Math.abs(eccentricity_value - 1) <= 1e-8) {
			orbit_type = "parabola";
		} else if (0 <= eccentricity_value && eccentricity_value < 1) {
			orbit_type = "ellipse";
		} else {
			orbit_type = "hyperbola";
		}
		
		return orbit_type;
	}

//	Compute the semimajor axis for an elliptical or hyperbolic orbit
	private static double normalSMA(double mu, double specific_orbital_energy) {
		double major_axis = -1 * mu / specific_orbital_energy;
		double sma = major_axis / 2;
	
		return sma;
	}

//	Compute the semimajor axis for a parabolic orbit
//	Since the semimajor axis of a parabola is infinite, the periapsis distance is returned instead
	private static double parabolaSMA(double mu, Vector3D specific_angular_momentum) {
		double ang_mom_mag = specific_angular_momentum.getMagnitude();
		double numerator = Math.pow(ang_mom_mag, 2);
		double denominator = 2 * mu;
		
		double periapsis_distance = numerator / denominator;
		
		return periapsis_distance;
	}

//	Calculate the location of the periapsis of an elliptical orbit
	private static Vector3D ellipsePeriapsis(Vector3D eccentricity, double semimajor_axis) {
		double ecc_magnitude = eccentricity.getMagnitude();
		Vector3D unit_ecc = eccentricity.unitVector();

		double scalar = semimajor_axis * (1 - ecc_magnitude);
		Vector3D periapsis = unit_ecc.multiplyScalar(scalar);
		
		return periapsis;
	}
	
//	Calculate the location of the periapsis of a parabolic orbit
	private static Vector3D parabolaPeriapsis(Vector3D eccentricity, double periapsis_distance) {
		Vector3D periapsis = eccentricity.multiplyScalar(periapsis_distance);
		
		return periapsis;
	}
	
//	Calculate the location of the periapsis of a hyperbolic orbit
	private static Vector3D hyperbolaPeriapsis(Vector3D eccentricity, double semimajor_axis) {
		Vector3D unit_ecc = eccentricity.unitVector();
		
		double abs_sma = Math.abs(semimajor_axis);
		double closest_approach = abs_sma * (eccentricity.getMagnitude() - 1);
		
		Vector3D periapsis = unit_ecc.multiplyScalar(closest_approach);
		
		return periapsis;
	}
	
//	Calculate the mean motion of an elliptical orbit
	private static double ellipseMeanMotion(double orbital_period) {
		double mean_motion = (2 * Math.PI) / orbital_period;
		
		return mean_motion;
	}
	
//	Calculate the mean motion of a hyperbolic orbit
	private static double hyperbolaMeanMotion(double mu, double sma) {
		double denominator = Math.pow(-1 * sma, 3);
		double fraction = mu / denominator;
		
		double mean_motion = Math.sqrt(fraction);
		
		return mean_motion;
	}
	
//	Compute the inclination of the orbit
//	double computeInclination(Vector3D angular_momentum) {
//		double magnitude = angular_momentum.getMagnitude();
//		double fraction = angular_momentum.getZ() / magnitude;
//		
//		double inclination_rad = Math.acos(fraction);
//		double inclination_deg = inclination_rad * (180 / Math.PI); 
//		
//		return inclination_deg;
//	}
	
}
