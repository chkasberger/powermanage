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
//http://www.mayor.de/lian98/doc.de/html/g_iec62056_struct.htm
