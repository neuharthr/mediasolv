/*
 * ClassModel.java
 * 
 * Created on 20 mai 2006, 15:52
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent Classes in the SBFB object model, it contains usefull information for building forms from instances of this class (obtained by
 * introspection or within the configuration).
 * 
 * @author Simon OUALID
 */
public class ClassModel
{

	private String	className		= null;
	private String	customComponent		= null;
	private String	icon			= null;
	private String	label			= null;
	private Map	fields			= new HashMap();
	private int	maxColumnSpanned	= 0;
	private boolean	showBanner		= true;

	/** Creates a new instance of ClassModel */
	public ClassModel()
	{
	}

	public Map getFields()
	{
		return fields;
	}

	public void setClassName( String className )
	{
		this.className = className;
	}

	public String getClassName()
	{
		return className;
	}

	public boolean equals( Object obj )
	{
		if ( obj instanceof ClassModel )
		{
			return ( ( ClassModel ) obj ).getClassName().equals( getClassName() );
		}
		return false;
	}

	public int hashCode()
	{
		return getClassName().hashCode();
	}

	public int getMaxColumnSpanned()
	{
		return maxColumnSpanned;
	}

	public void setMaxColumnSpanned( int maxColumnSpanned )
	{
		this.maxColumnSpanned = maxColumnSpanned;
	}

	public void setLabel( String value )
	{
		this.label = value;
	}

	public String getLabel()
	{
		return label;
	}

	public void setShowBanner( boolean b )
	{
		this.showBanner = b;
	}

	public boolean isShowBanner()
	{
		return showBanner;
	}

	public String getIcon()
	{
		return icon;
	}

	public void setIcon( String icon )
	{
		this.icon = icon;
	}

	
	public String getCustomComponent()
	{
		return customComponent;
	}

	
	public void setCustomComponent( String customComponent )
	{
		this.customComponent = customComponent;
	}

}
