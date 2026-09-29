import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {

	public static void main(String[] args) {
		Class[] test_classes = {TestVector3D.class, TestOrbit.class};
		
		JUnitCore.runClasses(TestOrbit.class);
//		JUnitCore.runClasses(TestVector3D.class);
	}

}