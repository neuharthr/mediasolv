package swingbeanformbuilder.gui.swing.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import swingbeanformbuilder.core.model.AllowedValue;

/**
 * A renderer that automatically computes its preferred size from the
 * values it renders.
 * 
 * @author s-oualid
 */
public class SBFBAllowedValueRenderer implements TableCellRenderer {

	private List availableItems = null;
	private static int maxValueLength = 50;
	
	public SBFBAllowedValueRenderer(List availableItems) {
		this.availableItems = availableItems;
		Iterator iter = availableItems.iterator();
		JLabel dummy = new JLabel();
		while (iter.hasNext()) {
			AllowedValue element = (AllowedValue) iter.next();
			int size = dummy.getFontMetrics(dummy.getFont()).stringWidth(element.toString());
			if (size > maxValueLength) {
				maxValueLength = size;
			}
		}
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		if (value != null) {
			AllowedValue v = new AllowedValue();
			v.setValue((String) value);
			if (availableItems.contains(v)) {
				v = (AllowedValue) availableItems.get(availableItems.indexOf(v));
			}
			label.setText(v.toString());
		}
		label.setBackground(Color.white);
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
