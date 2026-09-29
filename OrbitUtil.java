import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Hashtable;


public class OrbitUtil {
	public static void main(String[] args) {
		SpaceObject sun = new SpaceObject(1.988475e30, null, Vector3D.ORIGIN, Vector3D.ORIGIN);
		
		Vector3D earth_pos = new Vector3D(
			1.456072367818664E11,
			-3.899855645378449E10,
			1.293038526497781E6
		);
				
		Vector3D mars_pos = new Vector3D(
			6.447460119084607E10,
			2.194488597051807E11,
			3.017912932031661E9
		);

		Vector3D earth_vel = new Vector3D(
			7.234021363926447E3,
			2.867004543662078E04,
			-7.039925391136848E-1
		);
		
		Vector3D mars_vel = new Vector3D(
			-2.232817969784878E4,
			8.8896819703388818E3,
			7.337904066356611E2
		);
		
		SpaceObject earth = new SpaceObject(
			5.9722e24,
			sun,
			earth_pos,
			earth_vel
		);
		
		SpaceObject mars = new SpaceObject(
			6.417E23,
			sun,
			mars_pos,
			mars_vel
		);

		double transit_time = earth.estimateTransitTime(mars);
		LocalDateTime sim_start_date = LocalDateTime.parse("2026-09-08T00:00:00");
		OrbitPropagator gator = new OrbitPropagator(sim_start_date, 3600);

		LocalDateTime rough_launch_date = gator.findLaunchWindow(earth, mars);
		LocalDateTime rough_arrival_date = rough_launch_date.plusSeconds((long) transit_time);
		
		LocalDateTime launch_date = rough_launch_date.toLocalDate().atStartOfDay();
		LocalDateTime arrival_date = rough_arrival_date.toLocalDate().atStartOfDay();
		
		System.out.println("Estimated Launch Date: " + launch_date.toLocalDate());
		System.out.println("Estimated Arrival Date: " + arrival_date.toLocalDate());

		Hashtable<LocalDateTime, Vector3D[]> earth_states = gator.state_table(earth, launch_date, 30);
		Hashtable<LocalDateTime, Vector3D[]> mars_states = gator.state_table(mars, arrival_date, 30);

		SpaceTrip best_route = LambertSolver.bestTrip(earth_states, mars_states, earth.getOrbitedBody());
		SpaceObject ship = best_route.getShip();
		
		System.out.println("");
		System.out.println(best_route);
		
		Vector3D closest_approach = gator.distanceOnArrival(best_route);
		System.out.println("Closest Approach: " + closest_approach.getMagnitude() + " meters");
		
		Vector3D[] earth_trajectory = gator.propagateTime(earth, earth.getOrbitalPeriod());
		Vector3D[] mars_trajectory = gator.propagateTime(mars, mars.getOrbitalPeriod());
		Vector3D[] ship_trajectory = gator.propagateTime(ship, best_route.getFlightTime());
		
		writePositionData(earth_trajectory, "./earth.csv");
		writePositionData(mars_trajectory, "./mars.csv");
		writePositionData(ship_trajectory, "./ship.csv");
		
	}
	
//	Write trajectory data to a file
	public static void writePositionData(Vector3D[] state_data, String filename) {
		try (FileWriter writer = new FileWriter(filename)) {
			for (int i = 0; i < state_data.length; i++) {
				Vector3D position = state_data[i];
				writer.write(position.getX() + "," + position.getY() + "," + position.getZ() + "\n");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	
