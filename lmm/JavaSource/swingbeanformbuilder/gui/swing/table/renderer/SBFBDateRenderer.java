package swingbeanformbuilder.gui.swing.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import swingbeanformbuilder.core.SBFBConfiguration;

/**
 * Default renderer for <code>Date</code>
 * 
 * @author s-oualid
 */
public class SBFBDateRenderer implements TableCellRenderer {

	private static int maxValueLength = 50;
	
	public SBFBDateRenderer() {
		JLabel dummy = new JLabel();
		int size = dummy.getFontMetrics(dummy.getFont()).stringWidth(SBFBConfiguration.getDateFormat().format(new Date()));
		if (size > maxValueLength) {
			maxValueLength = size;
		}
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setBackground(Color.white);
		if (value != null) {
			label.setText(SBFBConfiguration.getDateFormat().format((Date)value));
		}
		if (isSelected) {
			label.setOpaque(true);
			label.setForeground(table.getSelectionForeground());
			label.setBackground(table.getSelectionBackground());
		}
		label.setOpaque(true);
		return label;
	}

	/**
	 * @return Returns the maxValueLength.
	 */
	public int getMaxValueLength() {
		return maxValueLength;
	}

	
}
