package swingbeanformbuilder.gui.swing.table.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A renderer used as a decorator to draw custom backgrounds on 
 * existing renderers.
 * 
 * @author s-oualid
 */
public class SBFBDecoratorRenderer implements TableCellRenderer {

	private Color color = null;
	private TableCellRenderer tcr = null;
	
	public SBFBDecoratorRenderer(TableCellRenderer tcr) {
		this(tcr,Color.white);
	}
	
	public SBFBDecoratorRenderer(TableCellRenderer tcr, Color color) {
		this.color = color;
		this.tcr = tcr;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = tcr.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		if (!isSelected) {
			c.setBackground(color);
		}
		return c;
	}
	
}
