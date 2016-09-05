package swingbeanformbuilder.gui;

import java.awt.Window;
import java.util.Map;

import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.ClassModel;

public interface ISBFBForm {

	/**
	 * @return a Map containing the fields components contained in this forms (the key is the field's name)
	 */
	public abstract Map getFields();

	/**
	 * Load datas from an instance of a user object into this form fields (javabean). 
	 * 
	 * @param o the user object instance to load
	 */
	public abstract void loadData(Object o);

	/**
	 * Create and return a <code>Map</code> object containing user inputs as typed objects.
	 * 
	 * @return a <code>Map</code> object containing user inputs as typed objects
	 */
	public abstract Map readUserInput();

	/**
	 * Save user input in the given instance of a user object (javabean).
	 * 
	 * @param o The instance of a user object (javabean) to fill. 
	 * @throws SBFBException if the user object cannot handles the form's datas.
	 */
	public abstract void saveData(Object o) throws SBFBException;

	/**
	 * Turns this form's components editable or not.
	 */
	public abstract void setEditable(boolean b);

	/* (non-Javadoc)
	 * @see swingbeanformbuilder.gui.ISBFBForm#getParentWindow()
	 */
	public abstract Window getParentWindow();

	/* (non-Javadoc)
	 * @see swingbeanformbuilder.gui.ISBFBForm#isEditable()
	 */
	public abstract boolean isEditable();

	/* (non-Javadoc)
	 * @see swingbeanformbuilder.gui.ISBFBForm#getClassModel()
	 */
	public abstract ClassModel getClassModel();

	public abstract void setFields(Map fields);

}