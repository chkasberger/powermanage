/**
 * 
 */

/**
 * @author User
 *
 */
public class JUtil {
	public static String getMethodName(int stack) {
		String functionName;
		functionName = (new Exception().getStackTrace()[stack].getClassName()
				+ "." + new Exception().getStackTrace()[stack].getMethodName());
		return functionName;
	}
}
