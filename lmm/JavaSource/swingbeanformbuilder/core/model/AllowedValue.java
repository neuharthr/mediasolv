/*
 * AllowedValue.java
 * 
 * Created on 20 mai 2006, 21:25
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core.model;

/**
 * A simple POJO that handle a couple of label/value, used for checkboxes, radiobutton, and combobox.
 * 
 * @author Simon OUALID
 */
public class AllowedValue
{

	private String	label	= null;
	private String	value	= "";

	/** Creates a new instance of AllowedValue */
	public AllowedValue()
	{
	}

	public String getValue()
	{
		return value != null ? value : "";
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel( String label )
	{
		this.label = label;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public String toString()
	{
		return label != null ? label : value;
	}

	public boolean equals( Object obj )
	{
		if (obj == null) return false;
		if ( obj instanceof AllowedValue )
		{
			return getValue().equals( ( ( AllowedValue ) obj ).getValue() );
		}
		return obj.toString().equals( getValue() );
	}

	public int hashCode()
	{
		return getValue().hashCode();
	}
}
