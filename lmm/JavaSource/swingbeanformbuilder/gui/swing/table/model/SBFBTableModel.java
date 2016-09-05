package swingbeanformbuilder.gui.swing.table.model;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.ClassModel;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.core.services.ISBFBFormFactory;

/**
 * A table model that is automatically generated from a field's <code>ClassModel</code>.
 * 
 * @author s-oualid
 */
public class SBFBTableModel extends AbstractTableModel
{

	private List	managedList	= new ArrayList();

	private List	fields		= new ArrayList();

	private Class	classToAdd;
	private Class	classToRender;

	public SBFBTableModel( FieldModel fm )
	{
		super();
		if ( fm.getClassName() == null )
		{
			throw new SBFBException( "Field " + fm.getName() + " of class " + fm.getClassModel().getClassName() + " cannot be handled in a table because we don't know its type." );
		}
		else
		{
			try
			{
				classToAdd = Class.forName( fm.getClassName() );
				classToRender = Class.forName( fm.getRenderClassName() != null ? fm.getRenderClassName() : fm.getClassName() ); 
				// ClassModel classModel = ( ClassModel ) SBFBConfiguration.getClasses().get( fm.getClassName() );
				ClassModel classModel = FormBuilder.refreshClassModelUsingIntrospection( classToRender );
				Iterator it = classModel.getFields().values().iterator();
				while ( it.hasNext() )
				{
					FieldModel element = ( FieldModel ) it.next();
					if ( element.isVisible() && !ISBFBFormFactory.FIELD_TYPE_LIST.equals( element.getType() ) && !ISBFBFormFactory.FIELD_TYPE_IMAGE.equals( element.getType() ) )
					{
						fields.add( element );
					}
				}
				Collections.sort( fields );
			}
			catch ( ClassNotFoundException e )
			{
				throw new SBFBException( "Field " + fm.getName() + " of class " + fm.getClassModel().getClassName() + " cannot be handled in a table because we cant't access its type (" + fm.getClassName() + ").", e );
			}
		}
	}

	public int getColumnCount()
	{
		return fields.size();
	}

	public int getRowCount()
	{
		return managedList.size();
	}

	public Object getValueAt( int rowIndex, int columnIndex )
	{
		Object o = managedList.get( rowIndex );
		FieldModel field = ( FieldModel ) fields.get( columnIndex );

		String mn = null;
		if ( !ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals( field.getType() ) )
		{
			mn = "get" + field.getName().substring( 0, 1 ).toUpperCase() + field.getName().substring( 1 );
		}
		else
		{
			mn = "is" + field.getName().substring( 0, 1 ).toUpperCase() + field.getName().substring( 1 );
		}
		try
		{
			Method m = o.getClass().getMethod( mn, null );
			Object result = m.invoke( o, null );
			return result;
		}
		catch ( Exception e )
		{
			throw new SBFBException( "Can't access to the getter " + mn + "() on the class " + o.getClass().getName() );
		}
	}

	public String getColumnName( int column )
	{
		FieldModel fm = ( FieldModel ) fields.get( column );
		String n = fm.getLabel() != null ? fm.getLabel() : fm.getName();
		return n.substring( 0, 1 ).toUpperCase() + n.substring( 1 );
	}

	public Class getColumnClass( int columnIndex )
	{
		FieldModel fm = ( FieldModel ) fields.get( columnIndex );
		if ( ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals( fm.getType() ) )
		{
			return Boolean.class;
		}
		else if ( ISBFBFormFactory.FIELD_TYPE_DOUBLE.equals( fm.getType() ) )
		{
			return Double.class;
		}
		else if ( ISBFBFormFactory.FIELD_TYPE_INTEGER.equals( fm.getType() ) )
		{
			return Integer.class;
		}
		else if ( ISBFBFormFactory.FIELD_TYPE_LONG.equals( fm.getType() ) )
		{
			return Long.class;
		}
		else if ( ISBFBFormFactory.FIELD_TYPE_FLOAT.equals( fm.getType() ) )
		{
			return Float.class;
		}
		else if ( ISBFBFormFactory.FIELD_TYPE_BIGDECIMAL.equals( fm.getType() ) )
		{
			return BigDecimal.class;
		}
		return super.getColumnClass( columnIndex );
	}

	public void clear()
	{
		managedList.clear();
		fireTableDataChanged();
	}

	public void addAll( List list )
	{
		managedList.addAll( list );
		fireTableDataChanged();
	}

	/**
	 * @return Returns the editableFields.
	 */
	public List getFields()
	{
		return fields;
	}

	public List getList()
	{
		return managedList;
	}

	public void remove( int i )
	{
		managedList.remove( i );
		fireTableRowsDeleted( i, i );
	}

	/**
	 * @return Returns the classToAdd.
	 */
	public Class getClassToAdd()
	{
		return classToAdd;
	}

	public void add( Object o )
	{
		managedList.add( o );
		fireTableRowsInserted( managedList.size(), managedList.size() );
	}
}
