package DataCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

	public class BufferedFileWriter
	{
		private final File file;
		private FileWriter fileWritter;
		private final BufferedWriter bufferWritter;

		public BufferedFileWriter(String fileName) throws IOException
		{
			file = new File(fileName);

			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				fileWritter = new FileWriter(file.getName(),true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bufferWritter = new BufferedWriter(fileWritter);
		}

		public void write(String value) throws IOException
		{
			bufferWritter.write(value);
		}

		public void writeln(String value) throws IOException
		{
			bufferWritter.write(value + "\r\n");
		}
	}
}
//http://www.mayor.de/lian98/doc.de/html/g_iec62056_struct.htm
