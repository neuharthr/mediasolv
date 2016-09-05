/*
 * FieldsConfiguration.java
 * 
 * Created on 20 mai 2006, 10:52
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import swingbeanformbuilder.core.parser.SBFBConfigurationContentHandler;

/**
 * This class stores the SBFB configuration object model. SBFB use the
 * informations stored in this class to customize the form building process.
 * 
 * @author Simon OUALID
 */
public class SBFBConfiguration {

	private static Map classes = new HashMap();

	// FIXME Use ressource bundles (i18n)
	private static String newLabel = "New";

	private static String modifyLabel = "Modify";

	private static String deleteLabel = "Delete";

	private static String defaultBuilderClass = "swingbeanformbuilder.core.services.impl.SwingSBFBFormBuilderImpl";

	private static DateFormat dateFormat = DateFormat.getDateInstance();

	private static String dateFormatString = null;

	public static final ImageIcon ADD_ICON = new ImageIcon(SBFBConfiguration.class.getResource("/swingbeanformbuilder/gui/swing/resources/iFolder_add.png"));
	public static final ImageIcon REMOVE_ICON = new ImageIcon(SBFBConfiguration.class.getResource("/swingbeanformbuilder/gui/swing/resources/iFolder_remove.png"));

	public static Map getClasses() {
		return classes;
	}

	public static void loadConfiguration(String fn) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			InputStream is = Utils.getInputStreamFromClassPath(fn);
			parser.parse(is, new SBFBConfigurationContentHandler());
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	public static void setDateFormat(DateFormat dateFormat) {
		SBFBConfiguration.dateFormat = dateFormat;
	}

	public static DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param deleteLabel
	 *            The deleteLabel to set.
	 */
	public static void setDeleteLabel(String deleteLabel) {
		SBFBConfiguration.deleteLabel = deleteLabel;
	}

	/**
	 * @param modifyLabel
	 *            The modifyLabel to set.
	 */
	public static void setModifyLabel(String modifyLabel) {
		SBFBConfiguration.modifyLabel = modifyLabel;
	}

	/**
	 * @param newLabel
	 *            The newLabel to set.
	 */
	public static void setNewLabel(String newLabel) {
		SBFBConfiguration.newLabel = newLabel;
	}

	/**
	 * @return Returns the deleteLabel.
	 */
	public static String getDeleteLabel() {
		return deleteLabel;
	}

	/**
	 * @return Returns the modifyLabel.
	 */
	public static String getModifyLabel() {
		return modifyLabel;
	}

	/**
	 * @return Returns the newLabel.
	 */
	public static String getNewLabel() {
		return newLabel;
	}

	public static String getDefaultBuilderClass() {
		return defaultBuilderClass;
	}

	public static void setDefaultBuilderClass(String defaultBuilderClass) {
		SBFBConfiguration.defaultBuilderClass = defaultBuilderClass;
	}

	public static String getDateFormatString() {
		return dateFormatString;
	}

	public static void setDateFormatString(String dateFormatString) {
		SBFBConfiguration.dateFormatString = dateFormatString;
	}

}
