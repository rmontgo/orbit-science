import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;


public class Simulation {
	private final LocalDateTime start_time;
	private final long time_step;
	private HashSet<SpaceObject> moving_bodies;
	private HashSet<SpaceObject> fixed_bodies;
	private HashSet<SpaceObject> mass_bodies;
	private OrbitPropagator gator;
	private SpaceObject spaceship;
	
	public Simulation(LocalDateTime start_time, long time_step, HashSet<SpaceObject> moving_bodies, HashSet<SpaceObject> fixed_bodies) {
		this.start_time = start_time;
		this.time_step = time_step;
		this.moving_bodies = moving_bodies;
		this.fixed_bodies = fixed_bodies;
		this.gator = new OrbitPropagator(start_time, time_step);
		this.mass_bodies = new HashSet<SpaceObject>(this.fixed_bodies);
		this.mass_bodies.addAll(moving_bodies);
		
	}
	
//	Propagate the simulation forward by some number of steps
	public void propagateSteps(long steps) {
		
	}
	
//	Get the set of all objects in the Simulation exerting gravity on a body
	public HashSet<SpaceObject> actingBodies(SpaceObject body) {
		HashSet<SpaceObject> acting_bodies = new HashSet<SpaceObject>(this.mass_bodies);
		
		acting_bodies.remove(body);
		
		return acting_bodies;
	}
}
