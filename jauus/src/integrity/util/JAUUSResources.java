package integrity.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class JAUUSResources {


	public static synchronized InputStream getInputStream( String propFile ) throws IllegalStateException
	{
		try {
			InputStream is = new FileInputStream( propFile );
			return is;
		}
		catch( FileNotFoundException fnf ) {
			System.out.println("[UPD] ClassPath= " + System.getProperty("java.class.path") );
			throw new IllegalStateException("Unable to find " + propFile + " in classpath");
		}

	}

}