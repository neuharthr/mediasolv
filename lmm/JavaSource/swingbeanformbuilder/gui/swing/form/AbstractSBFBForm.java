package swingbeanformbuilder.gui.swing.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.AllowedValue;
import swingbeanformbuilder.core.model.ClassModel;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.services.ISBFBFormFactory;
import swingbeanformbuilder.gui.ISBFBForm;
import swingbeanformbuilder.gui.swing.document.SBFBBigDecimalDocument;
import swingbeanformbuilder.gui.swing.document.SBFBDoubleDocument;
import swingbeanformbuilder.gui.swing.document.SBFBIntegerDocument;
import swingbeanformbuilder.gui.swing.form.components.ISBFBCustomComponent;
import swingbeanformbuilder.gui.swing.form.components.SBFBList;
import swingbeanformbuilder.gui.swing.form.components.SBFBOneOnOneTextField;
import swingbeanformbuilder.gui.swing.form.components.SBFBRadioButtonPanel;
import swingbeanformbuilder.gui.swing.model.CollectionListModel;
import swingbeanformbuilder.gui.swing.table.SBFBTable;
import swingbeanformbuilder.gui.swing.table.model.SBFBTableModel;

/**
 * Provides standard forms method to read and write datas from user object.
 * 
 * @author s-oualid
 */
public class AbstractSBFBForm extends JPanel implements ISBFBForm 
{

	protected Map		fields		= new HashMap();

	protected Window	parent;
	protected ClassModel	classModel;
	protected boolean	editable	= true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#getFields()
	 */
	public Map getFields()
	{
		return fields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#loadData(java.lang.Object)
	 */
	public void loadData( Object o )
	{
		try
		{
			Iterator it = fields.keySet().iterator();
			while ( it.hasNext() )
			{
				String key = ( String ) it.next();
				Component c = ( Component ) fields.get( key );
				Method m = null;
				if ( c instanceof JCheckBox || c instanceof JToggleButton )
				{
					m = o.getClass().getMethod( "is" + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 ), null );
				}
				else
				{
					m = o.getClass().getMethod( "get" + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 ), null );
				}
				Object result = m.invoke( o, null );

				if ( c instanceof ISBFBCustomComponent )
				{
					ISBFBCustomComponent tf = ( ISBFBCustomComponent ) c;
					tf.setValue( result );
				}
				else if ( c instanceof JFormattedTextField && result instanceof Date )
				{
					( ( JFormattedTextField ) c ).setValue( result );
				}
				else if ( c instanceof JTextComponent )
				{
					String s = String.valueOf( result );
					if ( "null".equals( s ) )
						s = "";
					( ( JTextComponent ) c ).setText( s );
				}
				else if ( c instanceof JComboBox )
				{
					AllowedValue v = new AllowedValue();
					v.setValue( ( String ) result );
					( ( JComboBox ) c ).setSelectedIndex( ( ( DefaultComboBoxModel ) ( ( JComboBox ) c ).getModel() ).getIndexOf( v ) );
				}
				else if ( c instanceof JList )
				{
					JList l = ( JList ) c;
					( ( CollectionListModel ) l.getModel() ).clear();
					( ( CollectionListModel ) l.getModel() ).addAll( ( List ) result );
				}
				else if ( c instanceof SBFBTable )
				{
					SBFBTable t = ( SBFBTable ) c;
					( ( SBFBTableModel ) t.getModel() ).clear();
					( ( SBFBTableModel ) t.getModel() ).addAll( ( List ) result );
				}
				else if ( c instanceof SBFBRadioButtonPanel )
				{
					SBFBRadioButtonPanel p = ( SBFBRadioButtonPanel ) c;
					p.setSelectedItem( ( String ) result );
				}
				else if ( c instanceof JCheckBox )
				{
					JCheckBox ch = ( JCheckBox ) c;
					ch.setSelected( ( ( Boolean ) result ).booleanValue() );
				}
				else if ( c instanceof JToggleButton )
				{
					JToggleButton ch = ( JToggleButton ) c;
					ch.setSelected( ( ( Boolean ) result ).booleanValue() );
				}
				else if ( c instanceof JLabel )
				{
					JLabel l = ( JLabel ) c;
					if ( result instanceof File )
					{
						l.setIcon( new ImageIcon( ( ( File ) result ).getPath() ) );
					}
					else if ( result instanceof String )
					{
						URL url = AbstractSBFBForm.class.getResource( "/" + ( String ) result );
						if ( url != null )
						{
							l.setIcon( new ImageIcon( url ) );
						}
						else
						{
							l.setIcon( new ImageIcon( ( String ) result ) );
						}
					}
					else
					{
						throw new SBFBException( "Actually only File and String type are supported to build images !" );
					}
					l.setBorder( BorderFactory.createLineBorder( Color.lightGray, 1 ) );
				}
				else if ( c instanceof SBFBOneOnOneTextField )
				{
					SBFBOneOnOneTextField tf = ( SBFBOneOnOneTextField ) c;
					tf.setValue( result );
				}
			}

		}
		catch ( Exception e )
		{
			throw new SBFBException( e );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#readUserInput()
	 */
	public Map readUserInput()
	{
		Map r = new HashMap();
		Iterator it = getFields().keySet().iterator();
		while ( it.hasNext() )
		{
			Object k = it.next();
			Object c = getFields().get( k );
			Object v = null;
			FieldModel fm = new FieldModel( classModel );
			fm.setName( ( String ) k );
			fm = ( FieldModel ) classModel.getFields().get( fm.getName() );
			if ( fm.isSetter() )
			{
				if ( c instanceof ISBFBCustomComponent )
				{
					v = ( ( ISBFBCustomComponent ) c ).getValue();
				}
				else if ( c instanceof JTextComponent )
				{
					if ( ( ( JTextComponent ) c ).getDocument() instanceof SBFBIntegerDocument )
					{
						v = new Integer( ( ( JTextComponent ) c ).getText() );
					}
					else if ( ( ( JTextComponent ) c ).getDocument() instanceof SBFBBigDecimalDocument )
					{
						v = new BigDecimal( ( ( JTextComponent ) c ).getText().replace( ',', '.' ) );
					}
					else if ( ( ( JTextComponent ) c ).getDocument() instanceof SBFBDoubleDocument )
					{
						v = new Double( ( ( JTextComponent ) c ).getText().replace( ',', '.' ) );
					}
					else if ( c instanceof JFormattedTextField )
					{
						try
						{
							v = SBFBConfiguration.getDateFormat().parse( ( ( JTextComponent ) c ).getText() );
						}
						catch ( ParseException e )
						{
							v = new Date();
						}
					}
					else
					{
						v = ( ( JTextComponent ) c ).getText();
					}
				}
				else if ( c instanceof JComboBox )
				{
					v = ( ( AllowedValue ) ( ( JComboBox ) c ).getSelectedItem() ).getValue();
				}
				else if ( c instanceof JList )
				{
					v = ( ( CollectionListModel ) ( ( JList ) c ).getModel() ).getList();
				}
				else if ( c instanceof SBFBTable )
				{
					v = ( ( SBFBTable ) c ).getSBFBTableModel().getList();
				}
				else if ( c instanceof SBFBRadioButtonPanel )
				{
					AllowedValue av = ( AllowedValue ) ( ( SBFBRadioButtonPanel ) c ).getSelectedItem();
					v = av != null ? av.getValue() : null;
				}
				else if ( c instanceof JCheckBox )
				{
					v = new Boolean( ( ( JCheckBox ) c ).isSelected() );
				}
				else if ( c instanceof JToggleButton )
				{
					v = new Boolean( ( ( JToggleButton ) c ).isSelected() );
				}
				else if ( c instanceof SBFBOneOnOneTextField )
				{
					v = ( ( SBFBOneOnOneTextField ) c ).getValue();
				}
				r.put( k, v );
			}
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#saveData(java.lang.Object)
	 */
	public void saveData( Object o ) throws SBFBException
	{
		try
		{
			Map result = readUserInput();
			Iterator it = result.keySet().iterator();
			while ( it.hasNext() )
			{
				String key = ( String ) it.next();
				Object v = result.get( key );
				Component c = ( Component ) fields.get( key );
				FieldModel fm = new FieldModel( classModel );
				fm.setName( key );
				fm = ( FieldModel ) classModel.getFields().get( fm.getName() );
				if ( c instanceof JTextComponent && !( ( JTextComponent ) c ).isEditable() )
				{
					continue;
				}
				if ( fm.isSetter() && fm.isVisible() && v != null )
				{
					Class currentType = v.getClass();
					if ( fm.isPrimitive() )
					{
						if ( ISBFBFormFactory.FIELD_TYPE_INTEGER.equals( fm.getType() ) )
						{
							currentType = Integer.TYPE;
						}
						else if ( ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals( fm.getType() ) )
						{
							currentType = Boolean.TYPE;
						}
						else if ( ISBFBFormFactory.FIELD_TYPE_DOUBLE.equals( fm.getType() ) )
						{
							currentType = Double.TYPE;
						}
						else if ( ISBFBFormFactory.FIELD_TYPE_LONG.equals( fm.getType() ) )
						{
							currentType = Long.TYPE;
						}
						else if ( ISBFBFormFactory.FIELD_TYPE_FLOAT.equals( fm.getType() ) )
						{
							currentType = Float.TYPE;
						}
					}
					Method m = null;
					if ( currentType.isInstance( new ArrayList() ) )
					{
						m = o.getClass().getMethod( "get" + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 ), null );
						List l = ( List ) m.invoke( o, null );
						l.clear();
						l.addAll( ( Collection ) v );
					}
					else if ( currentType.isInstance( new HashSet() ) )
					{
						m = o.getClass().getMethod( "get" + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 ), null );
						Set l = ( Set ) m.invoke( o, null );
						l.clear();
						l.addAll( ( Collection ) v );
					}
					else
					{
						m = o.getClass().getMethod( "set" + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 ), new Class[] { currentType } );
						m.invoke( o, new Object[] { v } );
					}
				}
			}
		}
		catch ( Exception e )
		{
			throw new SBFBException( e );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#setEditable(boolean)
	 */
	public void setEditable( boolean b )
	{
		editable = b;
		Iterator it = fields.keySet().iterator();
		while ( it.hasNext() )
		{
			String key = ( String ) it.next();
			Component c = ( Component ) fields.get( key );
			if ( b )
			{
				FieldModel fm = new FieldModel( classModel );
				fm.setName( key );
				fm = ( FieldModel ) classModel.getFields().get( fm.getName() );
				handleEditable( fm, c );
			}
			else
			{
				if ( c instanceof JTextComponent )
				{
					( ( JTextComponent ) c ).setEditable( b );
				}
				else if ( c instanceof SBFBRadioButtonPanel )
				{
					( ( SBFBRadioButtonPanel ) c ).setEditable( b );
				}
				else if ( c instanceof JComboBox )
				{
					( ( JComboBox ) c ).setEnabled( b );
				}
				else if ( c instanceof JToggleButton )
				{
					( ( JToggleButton ) c ).setEnabled( b );
				}
				else if ( c instanceof SBFBList )
				{
					( ( SBFBList ) c ).setEditable( b );
				}
				else if ( c instanceof JCheckBox )
				{
					( ( JCheckBox ) c ).setEnabled( b );
				}
				else if ( c instanceof SBFBTable )
				{
					( ( SBFBTable ) c ).setEditable( b );
				}
				else if ( c instanceof SBFBOneOnOneTextField )
				{
					( ( SBFBOneOnOneTextField ) c ).setEditable( b );
				}
				else if ( c instanceof ISBFBCustomComponent )
				{
					( ( ISBFBCustomComponent ) c ).setEditable( b );
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.ISBFBForm#getParentWindow()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#getParentWindow()
	 */
	public Window getParentWindow()
	{
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.ISBFBForm#isEditable()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#isEditable()
	 */
	public boolean isEditable()
	{
		return editable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.ISBFBForm#getClassModel()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#getClassModel()
	 */
	public ClassModel getClassModel()
	{
		return classModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see swingbeanformbuilder.gui.swing.form.ISBFBForm#setFields(java.util.Map)
	 */
	public void setFields( Map fields )
	{
		this.fields = fields;
	}

	private void handleEditable( final FieldModel field, Component c )
	{
		if ( c instanceof JTextComponent )
		{
			if ( field.isForceEditable() == null )
			{
				( ( JTextComponent ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( JTextComponent ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof SBFBRadioButtonPanel )
		{
			if ( field.isForceEditable() == null )
			{
				( ( SBFBRadioButtonPanel ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( SBFBRadioButtonPanel ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof JComboBox )
		{
			if ( field.isForceEditable() == null )
			{
				( ( JComboBox ) c ).setEnabled( field.isSetter() );
			}
			else
			{
				( ( JComboBox ) c ).setEnabled( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof JCheckBox )
		{
			if ( field.isForceEditable() == null )
			{
				( ( JCheckBox ) c ).setEnabled( field.isSetter() );
			}
			else
			{
				( ( JCheckBox ) c ).setEnabled( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof JToggleButton )
		{
			if ( field.isForceEditable() == null )
			{
				( ( JToggleButton ) c ).setEnabled( field.isSetter() );
			}
			else
			{
				( ( JToggleButton ) c ).setEnabled( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof SBFBList )
		{
			if ( field.isForceEditable() == null )
			{
				( ( SBFBList ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( SBFBList ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof SBFBTable )
		{
			if ( field.isForceEditable() == null )
			{
				( ( SBFBTable ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( SBFBTable ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof SBFBOneOnOneTextField )
		{
			if ( field.isForceEditable() == null )
			{
				( ( SBFBOneOnOneTextField ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( SBFBOneOnOneTextField ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
		else if ( c instanceof ISBFBCustomComponent )
		{
			if ( field.isForceEditable() == null )
			{
				( ( ISBFBCustomComponent ) c ).setEditable( field.isSetter() );
			}
			else
			{
				( ( ISBFBCustomComponent ) c ).setEditable( field.isForceEditable().booleanValue() );
			}
		}
	}

}