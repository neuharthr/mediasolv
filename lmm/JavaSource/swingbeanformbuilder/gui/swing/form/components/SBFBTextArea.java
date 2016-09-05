package swingbeanformbuilder.gui.swing.form.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * Customisation of the defalt JTextArea to let appears if it's 
 * editable or not.
 * 
 * @author s-oualid
 */
public class SBFBTextArea extends JTextArea {

	public SBFBTextArea() {
		setRows(3);
		setFont(new Font("Sans Serif", Font.PLAIN, 11));
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	}
	
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (editable) {
			setBackground(Color.WHITE);
		} else {
			setBackground(SystemColor.control);
		}		
	}
}
