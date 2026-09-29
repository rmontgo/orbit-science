import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Hashtable;
import java.util.ArrayList;

public class LambertSolver {
//	Compute Lambert solutions for all combinations of launch dates and arrival dates
	public static ArrayList<SpaceTrip> solveRange(
		Hashtable<LocalDateTime, Vector3D[]> launch_dates,
		Hashtable<LocalDateTime, Vector3D[]> arrival_dates,
		SpaceObject orbited_body
	) {
		
		double central_mu = orbited_body.getMu();
		
		ArrayList<SpaceTrip> space_trips = new ArrayList<SpaceTrip>();
		
		for (LocalDateTime launch_date : launch_dates.keySet()) {
			for (LocalDateTime arrival_date: arrival_dates.keySet()) {
				long flight_time = Duration.between(launch_date, arrival_date).getSeconds();
				Vector3D[] start_state = launch_dates.get(launch_date);
				Vector3D[] target_state = arrival_dates.get(arrival_date);
				
				if (isFeasibleTrip(start_state[0], target_state[0], flight_time, central_mu)){
					SpaceTrip trip = createTrip(start_state, target_state, launch_date, arrival_date, flight_time, orbited_body);
					space_trips.add(trip);
				}
			}
		}
		
		return space_trips;
	}

	//	Given the launch and arrival states and the body being orbited, find the most fuel-efficient SpaceTrip
	public static SpaceTrip bestTrip(
		Hashtable<LocalDateTime, Vector3D[]> launch_dates,
		Hashtable<LocalDateTime, Vector3D[]> arrival_dates,
		SpaceObject orbited_body
	) {
		ArrayList<SpaceTrip> all_trips = solveRange(launch_dates, arrival_dates, orbited_body);
		
		SpaceTrip best_route = all_trips.get(0);
		for (int i = 1; i < all_trips.size(); i++) {
			SpaceTrip current_trip = all_trips.get(i);
			if (current_trip.getVInfinity() < best_route.getVInfinity()) {
				best_route = current_trip;
			}
		}
		return best_route;
	}

	//	Calculate the linear distance between two positions
	private static double chordLength(Vector3D start_pos, Vector3D target_pos) {
		Vector3D distance = target_pos.subtractVector(start_pos);		
		double chord_length = distance.getMagnitude();
		
		return chord_length;
	}
	
//	 Solve for the semi-perimeter of the triangle between the central body and two orbiting bodies
	private static double semiPerimeter(Vector3D start_pos, Vector3D target_pos, double chord_length) {
		double semi_perimeter = (start_pos.getMagnitude() + target_pos.getMagnitude() + chord_length) / 2;
		
		return semi_perimeter;
	}
	
//	Solve for the value of the variable alpha based on the semi-perimeter and a hypothetical semi-major axis value
	private static double solveAlpha(double semi_perimeter, double semimajor_axis) {
		double fraction = semi_perimeter / (2 * semimajor_axis);
		double root = Math.sqrt(fraction);

		double inverse_sine = Math.asin(root);
		double alpha = inverse_sine * 2;
		
		return alpha;
	}
	
//	Solve for the variable beta based on the semi-perimeter, chord length,, and a hypothetical semi-major axis value
	private static double solveBeta(double semi_perimeter, double chord_length, double semimajor_axis) {
		double numerator = semi_perimeter - chord_length;
		double denominator = 2 * semimajor_axis;
		
		double fraction = numerator / denominator;
		
		double root = Math.sqrt(fraction);
		
		double inverse_sine = Math.asin(root);
		double beta = inverse_sine * 2;
		
		return beta;
	}
	
//	Calculate the time of flight based on the provided semi-major axis and other values
	private static double flightTime(double semimajor_axis, double mu, double chord_length, double semi_perimeter, double alpha, double beta) {
		double sma_cubed = Math.pow(semimajor_axis, 3);
		double fraction = sma_cubed / mu;
		double root = Math.sqrt(fraction);
		
		double first_difference = alpha - Math.sin(alpha);
		double second_difference = beta - Math.sin(beta);
		
		double big_difference = first_difference - second_difference;
				
		double time_of_flight = root * big_difference;
		
		return time_of_flight;
	}
	
//	Given a start state, a target state, a time of flight, and the mu of the central body, determine whether the trip is feasible
	private static boolean isFeasibleTrip(Vector3D start_pos, Vector3D target_pos, long flight_time, double mu) {
		double chord_length = chordLength(start_pos, target_pos);
		double semi_perimeter = semiPerimeter(start_pos, target_pos, chord_length);
		double min_sma = semi_perimeter / 2;
		double alpha = solveAlpha(semi_perimeter, min_sma);
		double beta = solveBeta(semi_perimeter, chord_length, min_sma);
		double maximum_flight_time = flightTime(min_sma, mu, chord_length, semi_perimeter, alpha, beta);
		
		boolean feasible_trip = maximum_flight_time >= flight_time;
		
		return feasible_trip;
	}

	//	Solve the Lambert problem for the velocity at the start and end points. Only works for elliptical orbits! 
	private static SpaceTrip createTrip(Vector3D[] start_state, Vector3D[] target_state, LocalDateTime launch_date, LocalDateTime arrival_date, long flight_time, SpaceObject orbited_body) {
		double chord_length = chordLength(start_state[0], target_state[0]);
		double semi_perimeter = semiPerimeter(start_state[0], target_state[0], chord_length);
		double mu = orbited_body.getMu();
		
		double sma_minimum = semi_perimeter / 2;
		double sma_maximum = semi_perimeter * 5;
		
		double test_sma = 0.0;
		double implied_flight_time = 0.0;
		double alpha = 0.0;
		double beta = 0.0;
		
		while (Math.abs(flight_time - implied_flight_time) > 1) {
			test_sma = (sma_minimum + sma_maximum) / 2;
			alpha = solveAlpha(semi_perimeter, test_sma);
			beta = solveBeta(semi_perimeter, chord_length, test_sma);
			
			implied_flight_time = flightTime(test_sma, mu, chord_length, semi_perimeter, alpha, beta);
			
//			If the implied flight time is smaller than the real flight time, increase the test_sma to decrease the implied flight time
			if (implied_flight_time < flight_time) {
				sma_maximum = test_sma;
			}

//			Otherwise, decrease the test_sma to increase the implied flight time
			else {
				sma_minimum = test_sma;
			}
		}

		double change_ecc_anomaly = alpha - beta;
		Vector3D[] vel_array = solveV1V2(start_state[0], target_state[0], test_sma, change_ecc_anomaly, mu, flight_time);

		SpaceTrip trip = new SpaceTrip(launch_date, arrival_date, start_state, target_state, vel_array[0], vel_array[1], orbited_body);
		
		return trip;
	}
	
//	Get an array of Vector3D objects containing the required start velocity and expected target velocity for the orbit
	private static Vector3D[] solveV1V2(Vector3D start_pos, Vector3D target_pos, double semimajor_axis, double change_ecc_anomaly, double mu, double flight_time) {
		double f_coeff = solveFGPrime(start_pos, semimajor_axis, change_ecc_anomaly);
		double g_coeff = solveG(semimajor_axis, mu, change_ecc_anomaly, flight_time);
		double f_prime = solveFPrime(start_pos, target_pos, semimajor_axis, mu, change_ecc_anomaly);
		double g_prime = solveFGPrime(target_pos, semimajor_axis, change_ecc_anomaly);
		
		Vector3D start_vel = startVel(start_pos, target_pos, f_coeff, g_coeff);
		Vector3D target_vel = targetVel(start_pos, start_vel, f_prime, g_prime);
		
		Vector3D[] vel_array = {start_vel, target_vel};
		
		return vel_array;
	}
	

	
//	Calculate the semi-latus rectum from the start and target position, the chord length, and delta theta
//	private double semiLatusRectum(Vector3D start_pos, Vector3D target_pos, double chord_length, double delta_theta) {
//		double numerator_scalar = (1 - Math.cos(delta_theta));
//		double denominator_diff = chord_length * Math.cos(delta_theta / 2);
//		double start_mag = start_pos.getMagnitude();
//		double target_mag = target_pos.getMagnitude();
//		
//		double numerator = start_mag * target_mag * numerator_scalar;
//		double denominator = start_mag + target_mag - denominator_diff;
//		
//		double semi_latus_rectum = numerator / denominator;
//		
//		return semi_latus_rectum;
//	}
	
//	Solve for the f' Lagrange coefficient
	private static double solveFPrime(Vector3D start_pos, Vector3D target_pos, double semimajor_axis, double mu, double change_ecc_anomaly) {
		double numerator_product = mu * semimajor_axis;
		double numerator = Math.sqrt(numerator_product);
		
		double denominator = start_pos.getMagnitude() * target_pos.getMagnitude();
		double fraction = -1 * (numerator / denominator);
		
		double ratio = Math.sin(change_ecc_anomaly);
		
		double f_prime = fraction * ratio;
		
		return f_prime;
	}
	
//	Solve for the either the f or g' Lagrange coefficients. If the start position is given, the result is f. If the target position is given, the result is g'
	private static double solveFGPrime(Vector3D pos, double semimajor_axis, double change_ecc_anomaly) {
		double scalar = semimajor_axis / pos.getMagnitude();		
		double difference = 1 - Math.cos(change_ecc_anomaly);
		
		double product = scalar * difference;
		double result_coefficient = 1 - product;
		
		return result_coefficient;
	}
	
//	Solve for the g Lagrange coefficient needed for v2
	private static double solveG(double semimajor_axis, double mu, double change_ecc_anomaly, double flight_time) {
		double sma_cubed = Math.pow(semimajor_axis, 3);
		double fraction = sma_cubed / mu;
		double root = Math.sqrt(fraction);
		
		double difference = change_ecc_anomaly - Math.sin(change_ecc_anomaly);
		double product = root * difference;
		
		double g_coeff = flight_time - product;
		
		return g_coeff;
	}
	
//	Calculate v1, the velocity at r1 that needs to be reached to enter the transfer orbit
	private static Vector3D startVel(Vector3D start_pos, Vector3D target_pos, double f_coeff, double g_coeff) {
		Vector3D scaled_start_pos = start_pos.multiplyScalar(f_coeff);
		
		Vector3D target_pos_diff = target_pos.subtractVector(scaled_start_pos);
		
		Vector3D new_start_vel = target_pos_diff.divideScalar(g_coeff);
		
		return new_start_vel;
	}
	
//	Calculate v2, the resulting velocity at r2
	private static Vector3D targetVel(Vector3D start_pos, Vector3D start_vel, double f_prime, double g_prime) {
		Vector3D scaled_start_pos = start_pos.multiplyScalar(f_prime);
		Vector3D scaled_start_vel = start_vel.multiplyScalar(g_prime);
		
		Vector3D new_target_vel = scaled_start_pos.addVector(scaled_start_vel);
		
		return new_target_vel;
	}
	
}
