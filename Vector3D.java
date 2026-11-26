
public class Vector3D {
	private final double x;
	private final double y;
	private final double z;
	private final double magnitude;
	
	public static final Vector3D ORIGIN = new Vector3D(0.0, 0.0, 0.0);
	public static final Vector3D[] ZERO_STATE = {ORIGIN, ORIGIN};
	
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.magnitude = computeMagnitude(this.x, this.y, this.z);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
		
//	Return the magnitude of the vector
	public double getMagnitude() {
		return this.magnitude;
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
	
//	Check if two vectors contain the exact same XYZ values
	public boolean equals(Vector3D other) {
		boolean is_equal = (this.x == other.x && this.y == other.y && this.z == other.z);
		
		return is_equal;
	}
	

//	Add the XYZ values of another vector to the XYZ values of this vector. Returns a new vector with the result values
	public Vector3D addVector(Vector3D other) {
		Vector3D sum_vector = new Vector3D(
			this.x + other.x,
			this.y + other.y,
			this.z + other.z
		);
		
		return sum_vector;
	}
	
//	Returns a Vector3D object containing the difference between this object and another
	public Vector3D subtractVector(Vector3D other) {
		Vector3D difference_vector = new Vector3D(
			this.x - other.x,
			this.y - other.y,
			this.z - other.z
		);
		
		return difference_vector;
	}

//  Multiply each component of the vector by a scalar value. Returns a new vector with the new values
	public Vector3D multiplyScalar(double scalar) {
		Vector3D productVector = new Vector3D(
			this.x * scalar,
			this.y * scalar,
			this.z * scalar
		);
		
		return productVector;
	}
	
//	Divide each component of the vector by a scalar value. Returns a new vector with the new values
	public Vector3D divideScalar(double scalar) {
		Vector3D quotient_vector = new Vector3D(
			this.x / scalar,
			this.y / scalar,
			this.z / scalar
		);
		
		return quotient_vector;
	}
	
//	Computes the dot product of this vector and another one. Returns the dot product
	public double dotProduct(Vector3D other_vector) {
		double dot_product = (this.x * other_vector.x) + (this.y * other_vector.y) + (this.z * other_vector.z);
		
		return dot_product;
	}
	
//	Computes the cross product of this vector and another one. Returns the cross product as a new vector
	public Vector3D crossProduct(Vector3D other_vector) {
		double cross_x = (this.y * other_vector.z) - (this.z * other_vector.y);
		double cross_y = (this.z * other_vector.x) - (this.x * other_vector.z);
		double cross_z = (this.x * other_vector.y) - (this.y * other_vector.x);
		
		return new Vector3D(cross_x, cross_y, cross_z);
	}
	
//	Compute the angle between two vectors, in radians
	public double centralAngle(Vector3D other_vector) {
		double numerator = this.dotProduct(other_vector);
		double denominator = this.getMagnitude() * other_vector.getMagnitude();

		double fraction = numerator / denominator;
		double phase_angle= Math.acos(fraction);

		return phase_angle;
	}
	
//	Multiply a vector by a scalar, then add it to the current scalar and return the result
	public Vector3D multiplyAdd(Vector3D change, double scalar) {
		Vector3D scaled_change = change.multiplyScalar(scalar);
		Vector3D result = this.addVector(scaled_change);
		
		return result;
	}
	
//	Get the unit vector of this vector
	public Vector3D unitVector() {
		double magnitude = this.getMagnitude();
		Vector3D unit_vector = this.divideScalar(magnitude);
		
		return unit_vector;
	}

	//  Compute and return the magnitude of the vector
	private static double computeMagnitude(double x, double y, double z) {
		double x_squared = Math.pow(x, 2);
		double y_squared = Math.pow(y, 2);
		double z_squared = Math.pow(z, 2);
		
		double sum = x_squared + y_squared + z_squared;
		
		double vector_magnitude = Math.sqrt(sum);
		
		return vector_magnitude;
	}

}