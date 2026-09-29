import org.junit.Test;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;

public class TestVector3D {

	private final Vector3D test_vector = new Vector3D(1.0, 2.0, 3.0);
	private final Vector3D test_vector_2 = new Vector3D(4.0, 5.0, -6.0);
	double test_scalar = 2.0;

	@Test
	public void testToString() {
		assertEquals("(1.0, 2.0, 3.0)", test_vector.toString());
	}
	
	@Test
	public void testGetX() {
		assertEquals(1.0, test_vector.getX(), 0.0);
	}
	
	@Test
	public void testGetY() {
		assertEquals(2.0, test_vector.getY(), 0.0);
	}
	
	@Test
	public void testGetZ() {
		assertEquals(3.0, test_vector.getZ(), 0.0);
	}
	
	@Test
	public void testGetMagnitude() {
		assertEquals(Math.sqrt(14.0), test_vector.getMagnitude(), 0.0);
	}

	@Test
	public void testAddVector() {
		Vector3D sum_vector = test_vector.addVector(test_vector_2);
		
		assertEquals(5.0, sum_vector.getX(), 0.0);
		assertEquals(7.0, sum_vector.getY(), 0.0);
		assertEquals(-3.0, sum_vector.getZ(), 0.0);
	}
	
	@Test
	public void testSubtractVector() {
		Vector3D sum_vector = test_vector.subtractVector(test_vector_2);
		
		assertEquals(-3.0, sum_vector.getX(), 0.0);
		assertEquals(-3.0, sum_vector.getY(), 0.0);
		assertEquals(9.0, sum_vector.getZ(), 0.0);
	}
	
	@Test
	public void testMultiplyScalar() {
		Vector3D product_vector = test_vector.multiplyScalar(test_scalar);
		
		assertEquals(2.0, product_vector.getX(), 0.0);
		assertEquals(4.0, product_vector.getY(), 0.0);
		assertEquals(6.0, product_vector.getZ(), 0.0);

	}
	
	@Test
	public void testDivideScalar() {
		Vector3D quotient_vector = test_vector.divideScalar(test_scalar);
		
		assertEquals(0.5, quotient_vector.getX(), 0.0);
		assertEquals(1.0, quotient_vector.getY(), 0.0);
		assertEquals(1.5, quotient_vector.getZ(), 0.0);
	}
	
	@Test
	public void testDotProduct() {
		assertEquals(-4.0, test_vector.dotProduct(test_vector_2), 0.0);
	}
	
	@Test
	public void testCrossProduct() {
		Vector3D cross_product = test_vector.crossProduct(test_vector_2);
		
		assertEquals(-27.0, cross_product.getX(), 0.0);
		assertEquals(18.0, cross_product.getY(), 0.0);
		assertEquals(-3.0, cross_product.getZ(), 0.0);
	}
	
	@Test
	public void testCentralAngle() {
		assertEquals(1.6929287087891894, test_vector.centralAngle(test_vector_2), 0.0);
	}
	
	@Test
	public void testMultiplyAdd() {
		Vector3D result_vector = test_vector.multiplyAdd(test_vector_2, test_scalar);
		
		assertEquals(9.0, result_vector.getX(), 0.0);
		assertEquals(12.0, result_vector.getY(), 0.0);
		assertEquals(-9.0, result_vector.getZ(), 0.0);
	}
	
	@Test
	public void testUnitVector() {
		Vector3D unit_vector = test_vector.unitVector();
		double test_vector_magnitude = Math.sqrt(14);
		
		assertEquals(1 / test_vector_magnitude, unit_vector.getX(), 0.0);
		assertEquals(2 / test_vector_magnitude, unit_vector.getY(), 0.0);
		assertEquals(3 / test_vector_magnitude, unit_vector.getZ(), 0.0);
	}
}
