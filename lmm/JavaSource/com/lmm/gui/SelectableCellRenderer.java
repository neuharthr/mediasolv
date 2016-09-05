package com.lmm.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.lmm.msg.MsgUtils;
import com.lmm.tools.FormatUtils;

public class SelectableCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private static final long serialVersionUID = 1L;
	private Color borderColor = Color.BLUE;
	private Font plainFont = null;


	public SelectableCellRenderer() {
		super();
		setOpaque(true);
	}
	
	/**
	 * 
	 */
	public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// do anything that only needs to be assigned once per repainting here
		if( row == 0 && column == 0 ) {
			plainFont = new Font( "Trebuchet MS", Font.PLAIN, 14 );
		}

		if( isSelected )
		{				
			//Each border is a newly allocated object (uses the new operator)
			//  but, we only create at most the number of columns the table has
			if( column == 0 )
				setBorder( BorderFactory.createMatteBorder( 2, 2, 2, 0, borderColor) );
			else if( column == (table.getModel().getColumnCount()-1) )
				setBorder( BorderFactory.createMatteBorder( 2, 0, 2, 2, borderColor) );
			else
				setBorder( BorderFactory.createMatteBorder( 2, 0, 2, 0, borderColor) );
	
		}
		else
		{
			// BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			setBorder( BorderFactory.createEmptyBorder() );
		}
	
	
		//DisplayableModel dm = getDisplayableTableModel( table.getModel() );
		if( table.getModel() instanceof DisplayableModel ) {
			handleColor( (DisplayableModel)table.getModel(), row, column, isSelected );
			((JComponent)this).setToolTipText( 
				((DisplayableModel)table.getModel()).getToolTip(row, column) );
		}
				
		if( value != null ) {
			setLabelText( value );
		}
		else {
			setLabelText( "" );
		}
	
		setCellAlignment( value );

		return this;
	}

	private void setLabelText( Object value ) {

		setIcon( null );

		if( value instanceof Long ) {
			setText( FormatUtils.decFormat( ((Long)value).longValue() ) );
		}
		else if( value instanceof Date ) {
			setText( FormatUtils.stdDate( (Date)value) );
		} else if( value instanceof MsgUtils.Statuses ) {
			setIcon( ((MsgUtils.Statuses)value).getImageIcon() );
			setText( "" );// value.toString() );
		}
		else
			setText( value.toString() );
		
	}

	private void setCellAlignment( Object value )
	{
		if( value instanceof Number || value instanceof MsgUtils.Statuses ) {
			setHorizontalAlignment( SwingConstants.CENTER );
		}
		else {
			setHorizontalAlignment( SwingConstants.LEFT );
		}
					
	}
		
	
	/**
	 * Handles any color handling needed
	 */
	private void handleColor(DisplayableModel model, int row, int column, boolean isSelected ) 
	{
		setBackground( model.getCellBGColor( row, column ));
		
		setForeground( model.getCellFGColor( row, column ));		
		setFont( plainFont );
	}
}
