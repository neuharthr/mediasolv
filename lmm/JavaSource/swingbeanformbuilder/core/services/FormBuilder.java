/*
 * FormBuilder.java
 * 
 * Created on 20 mai 2006, 01:35
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core.services;

import java.awt.Window;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.swing.JDialog;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.ClassModel;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.gui.ISBFBForm;

/**
 * This class provides static method for building GUIs from Javabeans, it uses the builder class defined in the xml configuration file, if not
 * defined, it uses the Swing implementation as a default.
 * 
 * @author Simon OUALID
 */
public class FormBuilder
{

	private static ISBFBFormFactory	builder	= null;

	static
	{
		try
		{
			builder = ( ISBFBFormFactory ) Class.forName( SBFBConfiguration.getDefaultBuilderClass() ).newInstance();
		}
		catch ( Exception e )
		{
			throw new SBFBException( "Cannot instantiate default builder (" + SBFBConfiguration.getDefaultBuilderClass() + ")", e );
		}
	}

	/**
	 * Build a standard JPanel with entry fields for the specified class using introspection and current configuration.
	 */
	public static ISBFBForm buildForm( Class aClass, Window aWindow )
	{
		return builder.buildForm( aClass, aWindow );
	}

	public static JDialog showEditDialog( final Object s, Window parent, boolean editable )
	{
		return builder.showEditDialog( s, parent, editable );
	}

	public static ISBFBFormFactory getBuilder()
	{
		return builder;
	}

	public static ClassModel refreshClassModelUsingIntrospection( Class aClass )
	{
		Map classes = SBFBConfiguration.getClasses();
		ClassModel c = new ClassModel();
		c.setClassName( aClass.getName() );
		if ( classes.containsKey( c.getClassName() ) )
		{
			c = ( ClassModel ) classes.get( c.getClassName() );
		}
		classes.put( c.getClassName(), c );

		Method[] m = aClass.getMethods();

		for ( int i = 0; i < m.length; i++ )
		{
			if ( m[i].getName().indexOf( "get" ) == 0 || m[i].getName().indexOf( "set" ) == 0 || m[i].getName().indexOf( "is" ) == 0 )
			{
				String fieldName = null;
				if ( m[i].getName().indexOf( "is" ) != 0 )
				{
					fieldName = m[i].getName().substring( 3 );
				}
				else
				{
					fieldName = m[i].getName().substring( 2 );
				}
				if ( fieldName.length() > 0 )
				{
					fieldName = fieldName.substring( 0, 1 ).toLowerCase() + fieldName.substring( 1 );
				}
				else
				{
					fieldName = "";
				}
				if ( !fieldName.equals( "class" ) )
				{
					FieldModel field = new FieldModel( c );
					field.setName( fieldName );
					if ( c.getFields().containsKey( field.getName() ) )
					{
						field = ( FieldModel ) c.getFields().get( field.getName() );
					}
					if ( m[i].getName().indexOf( "get" ) == 0 || m[i].getName().indexOf( "is" ) == 0 )
					{
						field.setGetter( true );
						if ( field.getClassName() == null )
						{
							field.setClassName( m[i].getReturnType().getName() );
						}
						if ( field.getType() == null )
						{
							if ( Integer.class.equals( m[i].getReturnType() ) || Long.class.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_INTEGER );
							}
							else if ( Double.class.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_DOUBLE );
							}
							else if ( BigDecimal.class.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_BIGDECIMAL );
							}
							else if ( m[i].getReturnType().isInstance( new ArrayList() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_LIST );
							}
							else if ( m[i].getReturnType().isInstance( new Date() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_DATE );
							}
							else if ( m[i].getReturnType().isInstance( new String() ) )
							{
								if ( field.getAllowedValues() == null )
								{
									field.setType( ISBFBFormFactory.FIELD_TYPE_STRING );
								}
								else
								{
									field.setType( ISBFBFormFactory.FIELD_TYPE_COMBO );
								}
							}
							else if ( m[i].getReturnType().isInstance( new Boolean( false ) ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_BOOLEAN );
							}
							else if ( Boolean.TYPE.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_BOOLEAN );
								field.setPrimitive( true );
							}
							else if ( Integer.TYPE.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_INTEGER );
								field.setPrimitive( true );
							}
							else if ( Double.TYPE.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_DOUBLE );
								field.setPrimitive( true );
							}
							else if ( Long.TYPE.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_LONG );
								field.setPrimitive( true );
							}
							else if ( Float.TYPE.equals( m[i].getReturnType() ) )
							{
								field.setType( ISBFBFormFactory.FIELD_TYPE_FLOAT );
								field.setPrimitive( true );
							}

							if ( field.getClassName() == null )
							{
								if ( !m[i].getReturnType().isInstance( new ArrayList() ) )
								{
									field.setClassName( m[i].getReturnType().getName() );
								}
							}
						}
					}
					else if ( m[i].getName().indexOf( "set" ) == 0 )
					{
						field.setSetter( true );
					}
					c.getFields().put( field.getName(), field );
				}
			}
		}
		return c;
	}

}
