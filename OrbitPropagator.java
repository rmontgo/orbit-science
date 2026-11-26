import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Objects;

public class OrbitPropagator {
	private final LocalDateTime start_date;
	private final long timestep;
	
	public OrbitPropagator(LocalDateTime start_date, long timestep) {
		this.start_date = Objects.requireNonNull(start_date);
		if (timestep > 0) {
			this.timestep = timestep;
		} else {
			this.timestep = 3600;
		}
	}
	
//	Get the timestep this OrbitPropagator uses
	public long getTimestep() {
		return this.timestep;
	}
	
//	Estimate the celestial body's position a fixed number of steps where each step is a fixed number of steps apart (timestep value)
//	Returns an array of state vectors (2D array of Vector3D objects) indicating all states propagated through
	public Vector3D[] propagateSteps(SpaceObject body, int steps) {
		Vector3D[] states;
		if (steps > 0) {
			states = new Vector3D[steps];
			for (int i = 0; i < steps; i++) {
				Vector3D[] new_state = this.propagate(body);
				states[i] = new_state[0];
				body.setState(new_state);
			}			
		} else {
			throw new IllegalArgumentException("The steps argument must be a positive integer, not " + steps);
		}
		
		return states;
	}
	
//	Estimate the celestial body's position over a given duration where each step is a given distance apart. If the total duration is 11 seconds and the timestep
//	is 2 seconds, the body will only be propagated for 5 steps.
	public Vector3D[] propagateTime(SpaceObject body, long duration) {
		int step_count = (int) (duration / this.timestep);
		Vector3D[] states = new Vector3D[step_count];
		
		for (int i = 0; i < step_count; i++) {
			Vector3D[] new_state = this.propagate(body);
			states[i] = new_state[0];
			body.setState(new_state);
		}
				
		return states;
	}
	
//	Determine the next launch window by propagating two bodies forward in time until the target phase angle is reached
	public LocalDateTime findLaunchWindow(SpaceObject start, SpaceObject target) {
		LocalDateTime launch_window = this.start_date;
				
//		Check that the target angle is valid and that there are valid phase angles between the two bodies
		if (start.isSystemRelative(target)) {
			double target_angle = start.estimateTransitAngle(target);
			
			Vector3D[] start_state = start.getState();
			Vector3D[] target_state = target.getState();
			
			double current_phase_angle = start.currentCentralAngle(target);
			
			while (Math.abs(target_angle - current_phase_angle) > 0.0005) {
				Vector3D[] start_update = this.propagate(start);
				Vector3D[] target_update = this.propagate(target);
				
				start.setState(start_update);
				target.setState(target_update);

				launch_window = launch_window.plusSeconds(this.timestep);
				current_phase_angle = start.currentCentralAngle(target);
			}
			
			start.setState(start_state);
			target.setState(target_state);
		} else {
			throw new IllegalArgumentException("A transfer orbit cannot be generated. The two bodies must be different bodies that orbit the same central bodies in elliptical orbits");
		}
		
		return launch_window;
	}
	
//	Determine the state vector of a SpaceObject on a given date.
	public Vector3D[] dateToState(SpaceObject body, LocalDateTime target_date) {
		if (this.start_date.isBefore(target_date)) {
			Vector3D[] start_state = body.getState();
			
			long time_diff = Duration.between(this.start_date, target_date).toSeconds();
			int step_count = (int) (time_diff / this.timestep);
			
			this.propagateSteps(body, step_count);
			Vector3D[] result_state = body.getState();
			
			body.setState(start_state);
			
			return result_state;			
		} else {
			throw new IllegalArgumentException("Target date must be after the simulation start date!");
		}
	}

//	Create a hash table mapping a timestamp to a SpaceObject's state vector over some range of dates
	public Hashtable<LocalDateTime, Vector3D[]> state_table(SpaceObject body, LocalDateTime center_date, long date_range) {
		Hashtable<LocalDateTime, Vector3D[]> body_states = new Hashtable<LocalDateTime, Vector3D[]>();
		
		LocalDateTime start_date = center_date.minusDays(date_range);
		long date_count = date_range * 2;
				
		for (long i = 0; i < date_count; i++) {
			LocalDateTime new_date = start_date.plusDays(i);
			Vector3D[] new_state = this.dateToState(body, new_date);
			body_states.put(new_date, new_state);
		}
		
		return body_states;
	}
	
//	Get a Vector3D representing the distance between the ship and its target on the arrival date
	public Vector3D distanceOnArrival(SpaceTrip trip) {
		SpaceObject ship = trip.getShip();
		Vector3D[] trip_start = ship.getState();
		
		this.propagateTime(trip.getShip(), trip.getFlightTime());
		
		Vector3D first_pos = trip.getTargetState()[0];
		Vector3D second_pos = ship.getState()[0];
		
		Vector3D distance = first_pos.subtractVector(second_pos);
		
//		Reset the ship's state
		ship.setState(trip_start);
		
		return distance;
	}
	
//	Given a body and the set of all bodies pulling on it, determine the acceleration due to gravity it is currently experiencing.
	public Vector3D simAcceleration(SpaceObject body, HashSet<SpaceObject> sim_bodies) {
		Vector3D current_pos = body.getPosition();
		Vector3D total_acceleration = Vector3D.ORIGIN;
		
		for (SpaceObject sim_body : sim_bodies) {
			Vector3D sim_acceleration = getAcceleration(current_pos, sim_body);
			total_acceleration.addVector(sim_acceleration);
		}
		
		return total_acceleration;
	}

//	Given a time step in seconds, propagate the object's orbit forward. Returns the end position and velocity in meters and meters/second.
	private Vector3D[] propagate(SpaceObject body) {
		Vector3D initial_position = body.getPosition();
		Vector3D initial_velocity = body.getVelocity();
		Vector3D initial_acceleration = orbitAcceleration(body);
		
		Vector3D[] result = new Vector3D[2]; 
		
		if (initial_acceleration.getMagnitude() != 0.0) {
			double half_timestep = this.timestep * 0.5;
			
			Vector3D[] k1 = kEstimate(body, Vector3D.ZERO_STATE, 0);
			Vector3D[] k2 = kEstimate(body, k1, half_timestep);
			Vector3D[] k3 = kEstimate(body, k2, half_timestep);
			Vector3D[] k4 = kEstimate(body, k3, this.timestep);
	
			Vector3D position_mean = rk4WeightedAverage(k1[0], k2[0], k3[0], k4[0]);
			Vector3D velocity_mean = rk4WeightedAverage(k1[1], k2[1], k3[1], k4[1]);
	
			Vector3D new_position = initial_position.multiplyAdd(position_mean, this.timestep);
			Vector3D new_velocity = initial_velocity.multiplyAdd(velocity_mean, this.timestep);
			
			result[0] = new_position;
			result[1] = new_velocity;
		}
		
		return result;			
	}

	// Determine the acceleration due to gravity that the first SpaceObject experiences from the second
	private static Vector3D getAcceleration(Vector3D position, SpaceObject sim_body) {
		// Order matters here. The position must be subtracted from the orbited body's position. The other way around will mess stuff up
		Vector3D distance = sim_body.getPosition().subtractVector(position);
		
		double distance_magnitude = distance.getMagnitude();
		double distance_cubed = Math.pow(distance_magnitude, 3);
		double fraction = sim_body.getMu() / distance_cubed;
		
		Vector3D acceleration = distance.multiplyScalar(fraction);
		
		return acceleration;
	}
	
	//	Compute the derivative of a given state vector (i.e., given position and velocity, get velocity and acceleration)
	private static Vector3D[] twoBodyODE(Vector3D test_position, Vector3D test_velocity, SpaceObject orbited_body) {
		Vector3D acceleration = getAcceleration(test_position, orbited_body);
		Vector3D[] result = {test_velocity, acceleration};
		
		return result;
	}
	
	//	Get the current acceleration vector due to gravity as a result of the body being orbited. Returns an empty vector if it orbits nothing
	private static Vector3D orbitAcceleration(SpaceObject body) {
		Vector3D acceleration = Vector3D.ORIGIN;
		SpaceObject orbited_body = body.getOrbitedBody();
		
		if (body.getOrbitedBody() != null) {
			acceleration = getAcceleration(body.getPosition(), orbited_body);
		}
		
		return acceleration;
	}
	
	//	Given a state vector, a change estimate, and a scalar, compute a k value for a Runge-Kutta estimate
	private static Vector3D[] kEstimate(SpaceObject body, Vector3D[] change, double scalar) {
		Vector3D position = body.getPosition();
		Vector3D velocity = body.getVelocity();
		
		Vector3D k_pos_est = position.multiplyAdd(change[0], scalar);
		Vector3D k_vel_est = velocity.multiplyAdd(change[1], scalar);
		Vector3D[] new_k = twoBodyODE(k_pos_est, k_vel_est, body.getOrbitedBody());
		
		return new_k;
	}
	
	//	Computes the weighted average of the input vectors. The weight is hardcoded so the third and fourth inputs are worth twice as much.
	private static Vector3D rk4WeightedAverage(Vector3D k1, Vector3D k2, Vector3D k3, Vector3D k4) {
		Vector3D vector_sum = Vector3D.ORIGIN;
		
		Vector3D weighted_k2 = k2.multiplyScalar(2);
		Vector3D weighted_k3 = k3.multiplyScalar(2);
		
		vector_sum = vector_sum.addVector(k1);
		vector_sum = vector_sum.addVector(weighted_k2);
		vector_sum = vector_sum.addVector(weighted_k3);
		vector_sum = vector_sum.addVector(k4);
	
		Vector3D average_vector = vector_sum.divideScalar(6);
		
		return average_vector;
	}
}
