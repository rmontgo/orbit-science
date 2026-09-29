import java.time.LocalDateTime;
import java.time.Duration;

public class SpaceTrip {
	private final SpaceObject ship;
	private final LocalDateTime departure_date;
	private final LocalDateTime arrival_date;
	private final Vector3D[] start_state;
	private final Vector3D[] target_state;
	private final long flight_time;
	private final double v_infinity;
	private final double arrival_excess;
	private final double total_excess;
	
	public SpaceTrip(LocalDateTime departure_date, LocalDateTime arrival_date, Vector3D[] start_state, Vector3D[] target_state, Vector3D depart_vel, Vector3D arrival_vel, SpaceObject orbited_body) {
		this.ship = new SpaceObject(1, orbited_body, start_state[0], depart_vel);
		this.departure_date = departure_date;
		this.arrival_date = arrival_date;
		this.start_state = start_state;
		this.target_state = target_state;
		this.v_infinity = vInfinity(start_state[1], depart_vel);
		this.arrival_excess = arrivalExcess(arrival_vel, target_state[1]);
		this.total_excess = totalExcess(start_state[1], depart_vel, target_state[1], arrival_vel);
		this.flight_time = Duration.between(departure_date, arrival_date).toSeconds();
		
	}
	
//	Get the ship object associated with this SpaceTrip
	public SpaceObject getShip() {
		return this.ship;
	}

	//	Get the flight time of this SpaceTrip
	public long getFlightTime() {
		return this.flight_time;
	}

	//	Get the arrival date of this SpaceTrip
	public LocalDateTime getArrivalDate() {
		return this.arrival_date;
	}

	//	Get the arrival date of this SpaceTrip
	public LocalDateTime getDepartureDate() {
		return this.departure_date;
	}

	//	Get the state vector of the start body on the departure date
	public Vector3D[] getStartState() {
		return this.start_state;
	}

	//	Get the state vector of the target body on the arrival date
	public Vector3D[] getTargetState() {
		return this.target_state;
	}

	//	Returns the delta V required for this trip
	public double getDeltaV() {
		return this.total_excess;
	}

	//	Returns the v infinity
	public double getVInfinity() {
		return this.v_infinity;
	}

	//	Returns the arrival excess velocity
	public double getArrivalExcess() {
		return this.arrival_excess;
	}

	//	Compute the Delta V required to make the trip in two burns. The first burn is the injection burn to escape the current gravity well
//	The second burn is the insertion burn to enter orbit of the new body
	private static double totalExcess(Vector3D start_vel, Vector3D depart_vel, Vector3D target_vel, Vector3D arrive_vel) {
		Vector3D injection_burn = start_vel.subtractVector(depart_vel);		
		Vector3D insertion_burn = arrive_vel.subtractVector(target_vel);
		
		double total_excess = injection_burn.getMagnitude() + insertion_burn.getMagnitude();
		
		return total_excess;
	}
	
//	Computes the excess velocity of the spacecraft after escaping its starting gravity well
	private static double vInfinity(Vector3D start_vel, Vector3D departure_vel) {
		Vector3D v_infinity_vector = start_vel.subtractVector(departure_vel);
		double v_infinity = v_infinity_vector.getMagnitude();
		
		return v_infinity;
	}
	
//	Computes the excess velocity relative to the target upon arrival
	private static double arrivalExcess(Vector3D arrival_vel, Vector3D target_vel) {
		Vector3D arrival_excess_vector = arrival_vel.subtractVector(target_vel);
		double arrival_excess = arrival_excess_vector.getMagnitude();
		
		return arrival_excess;
	}
	
	public String toString() {
		String result = "Departure Date: " + this.departure_date + "\nArrival Date: " + this.arrival_date + "\nMission Duration: " + (this.flight_time / 86400) + " days\nV Infinity: " + this.v_infinity + "\nTotal Hyperbolic Excess: " + this.total_excess + "\n";
		
		return result;
	}

}
