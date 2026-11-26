import java.util.HashSet;

public class CelestialBodyFactory {

	public static SpaceObject createBody(String name) {
		double mass;
		double radius;
		Vector3D position;
		Vector3D velocity;
		
//		All ephemeris data was gathered November 25, 2026 at 15:00
		switch (name) {
		case "Sun":
			mass = 1988410e24;
			radius = 695700000;
			position = Vector3D.ORIGIN;
			velocity = Vector3D.ORIGIN;

		case "Mercury": 
			mass = 3.302e23;
			radius = 2439400;
			position = new Vector3D(-5.194020633803178e10, 1.567233807794963e10, 6.044566267197418e9);
			velocity = new Vector3D(-2.415055717912483e4, -4.455588939729431e4, -1.426301709963013e3);
			break;

		case "Venus":
			mass = 48.685e23;
			radius = 6051840;
			position = new Vector3D(1.403003258057609e10, 1.068092354089650e11, 6.583270144544914e8);
			velocity = new Vector3D(-3.484078760598256e4, 4.383869688025560e3, 2.070555868352649e3);
			break;

		case "Earth": 
			mass = 5.97219e24;
			radius = 6371010;
			position = new Vector3D(6.708951695399117e10, 1.315475693840807e11, -8.929628781914711e6);
			velocity = new Vector3D(-2.700816817333846e4, 1.341808995456107e4, -2.400571630962745e-1);
			break;

		case "Mars":
			mass = 6.4171e23;
			radius = 3389920;
			position = new Vector3D(-9.058951649486235e10, 2.244996779437493e11, 6.925923281270742e9);
			velocity = new Vector3D(-2.155240246599824e4, -7.007348669779335e3, 3.816010904974720e2);
			break;

		case "Jupiter": 
			mass = 18.9819e26;
			radius = 71492000;
			position = new Vector3D(-5.685890521479321e11, 5.577624089317424e11, 1.040449182056251e10);
			velocity = new Vector3D(-9.310537297806793e3, -8.726111659885046e3, 2.445019075509158e2);
			break;

		case "Saturn": 
			mass = 5.6834e26;
			radius = 60268000;
			position = new Vector3D(1.373120519810574e12, 3.096241944812940e11, -6.005101405138414e10);
			velocity = new Vector3D(-2.660108958686228e3, 9.398964197832347e3, -5.836900810147760e1);
			break;

		case "Uranus": 
			mass = 86.813e24;
			radius = 25559000;
			position = new Vector3D(1.306574735773204e12, 2.596663167247615e12, -7.297301006681085e9);
			velocity = new Vector3D(-6.143300006881057e3, 2.738799127936978e3, 8.996732129396401e1);
			break;

		case "Neptune": 
			mass = 102.409e24;
			radius = 24766000;
			position = new Vector3D(4.462136387144624e12, 2.326905743592061e11, -1.076209453063203e11);
			velocity = new Vector3D(-3.278410605161794e2, 5.454366032497646e3, -1.055657423990977e2);
			break;
		
		default:
			mass = 0.0;
			radius = 0.0;
			position = Vector3D.ORIGIN;
			velocity = Vector3D.ORIGIN;
		}
		
		return new SpaceObject(name, mass, radius, null, position, velocity);
	}
	
//	Given an array of strings, create the corresponding set of planets
	public static HashSet<SpaceObject> createBodySet(String[] planet_names){
		HashSet<SpaceObject> planets = new HashSet<SpaceObject>();
		
		for (String planet_name: planet_names) {
			SpaceObject planet = createBody(planet_name);
		}
		
		return planets;
	}
}
