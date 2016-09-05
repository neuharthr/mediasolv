package swingbeanformbuilder.core.services;

import java.awt.Window;

import javax.swing.JDialog;

import swingbeanformbuilder.gui.swing.form.AbstractSBFBForm;

/**
 * Interface implemented by the SBFB form builders (swing one, swt one, etc...)
 * 
 * @author s-oualid
 */
public interface ISBFBFormFactory
{

	public static final String	FIELD_RENDER_TABLE	= "table";

	public static final String	FIELD_TYPE_COMBO	= "combo";

	public static final String	FIELD_TYPE_RADIO	= "radio";

	public static final String	FIELD_RENDER_TOGGLE	= "toggle";

	public static final String	FIELD_TYPE_STRING	= "string";

	public static final String	FILL_BOTH		= "both";

	public static final String	FILL_VERTICAL		= "vertical";

	public static final String	FILL_NONE		= "none";

	public static final String	FILL_HORIZONTAL		= "horizontal";

	public static final String	FIELD_RENDER_LIST	= "list";

	public static final String	SELECTION_MODE_MULTIPLE	= "multiple";

	public static final String	SELECTION_MODE_INTERVAL	= "interval";

	public static final String	SELECTION_MODE_SINGLE	= "single";

	static public final String	FIELD_TYPE_LIST		= "list";

	static public final String	FIELD_TYPE_DATE		= "date";

	static public final String	FIELD_TYPE_INTEGER	= "integer";

	static public final String	FIELD_TYPE_DOUBLE	= "double";

	static public final String	FIELD_TYPE_LONG		= "long";

	static public final String	FIELD_TYPE_FLOAT	= "float";

	public static final String	FIELD_TYPE_BIGDECIMAL	= "bigdecimal";

	public static final String	FIELD_TYPE_BOOLEAN	= "boolean";

	public static final String	FIELD_TYPE_IMAGE	= "image";

	public static final String	FIELD_ALIGN_CENTER	= "center";
	public static final String	FIELD_ALIGN_LEFT	= "left";
	public static final String	FIELD_ALIGN_RIGHT	= "right";

	/**
	 * Build a standard JPanel with entry fields for the specified class using introspection and current configuration.
	 */
	public AbstractSBFBForm buildForm( Class aClass, Window aWindow );

	public JDialog showEditDialog( final Object s, Window parent, boolean editable );
}