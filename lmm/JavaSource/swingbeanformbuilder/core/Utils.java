package swingbeanformbuilder.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class provides simple utils methods to load ressources from class path.
 * 
 * @author s-oualid
 */
public class Utils {

	public static InputStream getInputStreamFromClassPath(String fn) throws FileNotFoundException {
		InputStream is = Utils.class.getResourceAsStream(fn);
		if (is == null) is = Utils.class.getClassLoader().getResourceAsStream(fn);
		if (is == null) is = Utils.class.getClassLoader().getParent().getResourceAsStream(fn);
		if (is == null) is = new FileInputStream(new File(fn));
		return is;
	}

	public static URL getUrlFromClassPath(String fn) {
		URL u = SBFBConfiguration.class.getResource(fn);
		if (u == null) u = Utils.class.getClassLoader().getResource(fn);
		if (u == null) u = Utils.class.getClassLoader().getParent().getResource(fn);
		try {
			if (u == null) u = new File(fn).toURL();
		} catch (MalformedURLException e) {
			u = null;
		}
		return u;
	}
}
