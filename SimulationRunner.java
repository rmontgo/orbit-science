import java.util.HashSet;
import java.time.LocalDateTime;

public class SimulationRunner {
	public static void main(String[] args) {
//		Create the sun and place it into the fixed bodies set
		SpaceObject sun = CelestialBodyFactory.createBody("Sun");
		HashSet<SpaceObject> fixed_bodies = new HashSet<SpaceObject>();
		fixed_bodies.add(sun);
		
//		Create the planets and put them into the planets set
		String[] planet_names = {"Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};		
		HashSet<SpaceObject> moving_bodies = CelestialBodyFactory.createBodySet(planet_names);

//		Tell all planets that they orbit the sun
		for (SpaceObject planet : moving_bodies) {
			planet.setOrbitedBody(sun);
		}
		
		LocalDateTime start_time = LocalDateTime.parse("2026-11-25T15:00:00");
		
		Simulation sim = new Simulation(start_time, 3600, moving_bodies, fixed_bodies);
		
	}
}
