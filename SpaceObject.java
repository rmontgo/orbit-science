// Represents a space object with mass (kilograms), a semimajor axis (meters), a body orbited (other SpaceObject object), and a state vector (StateVector object). 
public class SpaceObject {
	
	private static final double grav_const = 6.6743015 * Math.pow(10, -11);
	
	private final double mu; 
	private final SpaceObject orbited_body;
	private Vector3D position;
	private Vector3D velocity;
	private Orbit orbit;
	
	public SpaceObject(double mass, SpaceObject orbited_body, Vector3D position, Vector3D velocity) {
		this.mu = computeMu(mass);
		this.orbited_body = orbited_body;
		this.position = position;
		this.velocity = velocity;
		this.orbit = computeOrbit(position, velocity, orbited_body);		
		
	}
	
//	Returns the Mu value of this SpaceObject
	public double getMu() {
		return this.mu;
	}
	
//	Returns the current position of this SpaceObject
	public Vector3D getPosition() {
		return this.position;
	}
	
//	Returns the current velocity of this SpaceObject
	public Vector3D getVelocity() {
		return this.velocity;
	}
	
//	Return an array containing both the position and velocity of this SpaceObject
	public Vector3D[] getState() {
		Vector3D[] state = {this.position, this.velocity};
		
		return state;
	}
	
//	Return the SpaceObject that this SpaceObject orbits, or null if there isn't one
	public SpaceObject getOrbitedBody() {
		return this.orbited_body;
	}
	
//	Return the orbital period of this SpaceObject cast to a long
	public long getOrbitalPeriod() {
		return (long) this.orbit.getOrbitalPeriod();
	}
	
//	Get the Mu value of the SpaceObject that this SpaceObject orbits.
	//	Returns 0.0 if it does not orbit anything
		public double getOrbitedMu() {
			double orbited_mu;
			
			if (this.orbited_body != null) {
				orbited_mu = this.orbited_body.getMu();
			} else {
				orbited_mu = 0.0;
			}
			
			return orbited_mu;
		}
		
	//	Update the state of the space object
		public void setState(Vector3D[] state_vector) {
			if (state_vector.length == 2) {
				this.position = state_vector[0];
				this.velocity = state_vector[1];				
			}
		}

	//	Estimate the current central angle, in degrees, between two space objects, as measured from the object they both orbit 
	public double currentCentralAngle(SpaceObject target_body) {
		double phase_angle;
		
		if (this.isSystemRelative(target_body)) {
			phase_angle = this.position.centralAngle(target_body.position);
		} else {
			phase_angle = 0.0;
		}
		
		return phase_angle;
	}
	
//	Estimate the time it will take to use a Hohmann transfer to travel from one SpaceObject to another
	public double estimateTransitTime(SpaceObject target_body) {
		double transit_time;
		
		if (this.isSystemRelative(target_body)) {
			transit_time = this.orbit.estimateTransferTime(target_body.orbit);
		} else {
			transit_time = 0.0;
		}
		
		return transit_time;
	}
	
//	Wrapper method for Orbit.estimateTransitAngle(), which estimates the ideal transit angle between two bodies that orbit the same object
	public double estimateTransitAngle(SpaceObject target_body) {
		double phase_angle;
		
		if (this.isSystemRelative(target_body)) {
			phase_angle = this.orbit.estimateTransitAngle(target_body.orbit);
		} else {
			phase_angle = 0.0;
		}
		
		return phase_angle;
	}
	
//	Wrapper method for the Orbit.isSystemRelative method. Checks whether this and target_body orbit the same SpaceObject
	public boolean isSystemRelative(SpaceObject target_body) {
		boolean is_system_relative;

		if (this.orbit != null && target_body.orbit != null) {
			is_system_relative = this.orbit.isSystemRelative(target_body.orbit);			
		} else {
			is_system_relative = false;
		}
		
		return is_system_relative;
	}
	
//	Get the current distance of the body from whatever it orbits. Returns an empty vector if it orbits nothing
	public Vector3D orbitDistance() {
		Vector3D distance_vector;
		
		if (this.orbited_body != null) {
			distance_vector = this.position.subtractVector(this.orbited_body.position);
		} else {
			distance_vector = Vector3D.ORIGIN;
		}
		
		return distance_vector;
	}

//	Get the Orbit based on this SpaceObject's current position, velocity, and what it is orbiting.
	public static Orbit computeOrbit(Vector3D position, Vector3D velocity, SpaceObject orbited_body) {
		Orbit orbit;
		
		if (orbited_body != null) {
			orbit = new Orbit(position, velocity, orbited_body);
		} else {
			orbit = null;
		}
		
		return orbit;
	}
	
//	Determine the mu value. If the provided mass is 0, mu will be set to G instead to prevent issues
	static double computeMu(double mass) {
		double mu;
		
		if (mass > 0) {
			mu = mass * grav_const;
		} else {
			throw new IllegalArgumentException("Mass must be a positive value instead of: " + mass);
		}
		
		return mu;
	}
		
}
