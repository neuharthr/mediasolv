package swingbeanformbuilder.gui.swing.table;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.core.services.ISBFBFormFactory;
import swingbeanformbuilder.gui.swing.table.model.SBFBTableModel;
import swingbeanformbuilder.gui.swing.table.renderer.SBFBAllowedValueRenderer;
import swingbeanformbuilder.gui.swing.table.renderer.SBFBDateRenderer;
import swingbeanformbuilder.gui.swing.table.renderer.SBFBDecoratorRenderer;

/**
 * Simple table component that can be used to handle 1-n associations (List, Set...).
 * 
 * @author s-oualid
 */
public class SBFBTable extends JTable
{

	private boolean		editable	= true;

	private FieldModel	myFieldModel;

	private JMenuItem	modify;

	private JMenuItem	delete;

	private JMenuItem	add;

	public SBFBTable( FieldModel fm, final Window parent )
	{
		super( new SBFBTableModel( fm ) );
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		this.myFieldModel = fm;
		computeTableRenderers();
		setOpaque( true );
		if ( fm.isAllowDigging() )
		{
			addMouseListener( new MouseAdapter()
			{

				public void mousePressed( MouseEvent e )
				{
					if ( e.getClickCount() == 2 )
					{
						int s = SBFBTable.this.getSelectedRow();
						Object o = getSBFBTableModel().getList().get( s );
						FormBuilder.showEditDialog( o, parent, isEditable() );
					}
				}
			} );
		}
		if ( fm.isAllowDelete() && fm.isSetter() )
		{
			addKeyListener( new KeyAdapter()
			{

				public void keyReleased( KeyEvent e )
				{
					if ( e.getKeyCode() == KeyEvent.VK_DELETE )
					{
						removeSelectedElements();
					}
				}
			} );
		}

		if ( fm.isShowMenu() && fm.isAllowDigging() )
		{
			final JPopupMenu menu = new JPopupMenu();
			add = new JMenuItem( SBFBConfiguration.getNewLabel() );
			add.addActionListener( new ActionListener()
			{

				public void actionPerformed( ActionEvent arg0 )
				{
					try
					{
						Object o = getSBFBTableModel().getClassToAdd().newInstance();
						getSBFBTableModel().add( o );
						FormBuilder.showEditDialog( o, parent, isEditable() );
					}
					catch ( Exception e )
					{
						throw new SBFBException( e );
					}
				}
			} );
			if ( getSBFBTableModel().getClassToAdd() == null )
			{
				add.setEnabled( false );
			}
			menu.add( add );
			modify = new JMenuItem( SBFBConfiguration.getModifyLabel() );
			modify.addActionListener( new ActionListener()
			{

				public void actionPerformed( ActionEvent arg0 )
				{
					if ( getSelectedRow() > -1 )
					{
						int s = SBFBTable.this.getSelectedRow();
						Object o = getSBFBTableModel().getList().get( s );
						FormBuilder.showEditDialog( o, parent, isEditable() );
					}
				}
			} );
			if ( !fm.isSetter() || !fm.isAllowDigging() )
			{
				modify.setEnabled( false );
			}
			menu.add( modify );
			delete = new JMenuItem( SBFBConfiguration.getDeleteLabel() );
			delete.addActionListener( new ActionListener()
			{

				public void actionPerformed( ActionEvent arg0 )
				{
					removeSelectedElements();
				}
			} );
			if ( !fm.isAllowDelete() )
			{
				delete.setEnabled( false );
			}
			menu.add( delete );
			addMouseListener( new MouseAdapter()
			{

				public void mousePressed( MouseEvent e )
				{
					if ( e.isPopupTrigger() )
					{
						menu.show( e.getComponent(), e.getX(), e.getY() );
					}
				}

				public void mouseReleased( MouseEvent e )
				{
					if ( e.isPopupTrigger() )
					{
						menu.show( e.getComponent(), e.getX(), e.getY() );
					}
				}
			} );
		}
	}

	private void removeSelectedElements()
	{
		while ( getSelectedRow() >= 0 )
		{
			getSBFBTableModel().remove( getSelectedRow() );
		}
	}

	private void computeTableRenderers()
	{
		Iterator it = getSBFBTableModel().getFields().iterator();
		int i = 0;
		while ( it.hasNext() )
		{
			FieldModel element = ( FieldModel ) it.next();
			if ( element.getMaxlength() != null )
			{
				int max = getFontMetrics( getFont() ).stringWidth( "a" ) * element.getMaxlength().intValue();
				getColumnModel().getColumn( i ).setPreferredWidth( max + 20 );
			}
			if ( ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals( element.getType() ) )
			{
				int max = getFontMetrics( getFont() ).stringWidth( getColumnName( i ) );
				getColumnModel().getColumn( i ).setPreferredWidth( max + 20 );
			}
			else if ( ISBFBFormFactory.FIELD_TYPE_DATE.equals( element.getType() ) )
			{
				SBFBDateRenderer r = new SBFBDateRenderer();
				if ( element.getBackgroundColor() != null )
				{
					getColumnModel().getColumn( i )
							.setCellRenderer( new SBFBDecoratorRenderer( r, element.getBackgroundColor() ) );
				}
				else
				{
					getColumnModel().getColumn( i ).setCellRenderer( r );
				}
				getColumnModel().getColumn( i ).setPreferredWidth( r.getMaxValueLength() + 15 );
			}
			else if ( element.getAllowedValues() != null )
			{
				SBFBAllowedValueRenderer r = new SBFBAllowedValueRenderer( element.getAllowedValues() );
				if ( element.getBackgroundColor() != null )
				{
					getColumnModel().getColumn( i )
							.setCellRenderer( new SBFBDecoratorRenderer( r, element.getBackgroundColor() ) );
				}
				else
				{
					getColumnModel().getColumn( i ).setCellRenderer( r );
				}
				getColumnModel().getColumn( i ).setPreferredWidth( r.getMaxValueLength() + 15 );
			}
			else
			{
				if ( element.getBackgroundColor() != null )
				{
					getColumnModel().getColumn( i ).setCellRenderer(
							new SBFBDecoratorRenderer( getDefaultRenderer( getModel().getColumnClass( i ) ), element
									.getBackgroundColor() ) );
				}
				else
				{
					getColumnModel().getColumn( i ).setCellRenderer(
							new SBFBDecoratorRenderer( getDefaultRenderer( getModel().getColumnClass( i ) ) ) );
				}
			}
			i++;
		}
	}

	/**
	 * Just a hack to avoid fastidious cast in client code ... ^_^
	 * 
	 * @return the SBFBTableModel of this table
	 */
	public SBFBTableModel getSBFBTableModel()
	{
		return ( SBFBTableModel ) super.getModel();
	}

	/**
	 * @return Returns the editable.
	 */
	public boolean isEditable()
	{
		return editable;
	}

	public void setEditable( boolean editable )
	{
		this.editable = editable;
		if ( add != null )
		{
			if ( !editable )
			{
				add.setEnabled( false );
				modify.setEnabled( false );
				delete.setEnabled( false );
			}
			else
			{
				add.setEnabled( getSBFBTableModel().getClassToAdd() != null );
				modify.setEnabled( myFieldModel.isSetter() );
				delete.setEnabled( myFieldModel.isAllowDelete() );
			}
		}
	}

}
