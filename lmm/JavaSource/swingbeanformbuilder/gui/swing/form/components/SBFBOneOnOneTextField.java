package swingbeanformbuilder.gui.swing.form.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.services.FormBuilder;

/**
 * Simple component to handle 1-1 association between a form and a 
 * custom user object.
 * 
 * @author s-oualid
 */
public class SBFBOneOnOneTextField extends JPanel {

	private Object value = null;
	private JTextField tf;
	private JButton b;
	private boolean editable = true;
	
	public SBFBOneOnOneTextField(final Window parent, final Class classToAdd) {
		super(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1;
		tf = new JTextField();
		tf.setEditable(false);
		add(tf,gc);
		gc.gridx++;
		b = new JButton();
		b.setIcon(SBFBConfiguration.ADD_ICON);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (value == null && classToAdd != null) {
					try {
						value = classToAdd.newInstance();
					} catch (Exception e1) {
						throw new SBFBException("Can't instantiate class : " + classToAdd.getName(),e1);
					}
				}
				if (value != null) {
					FormBuilder.showEditDialog(value,parent,editable);
					tf.setText(value.toString());
				}
			}
		});
		b.setPreferredSize(new Dimension(27,b.getPreferredSize().height));
		gc.weightx = 0;
		gc.fill = GridBagConstraints.NONE;
		add(b,gc);
		
		gc.gridx++;
		b = new JButton();
		b.setIcon(SBFBConfiguration.REMOVE_ICON);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tf.setText("");
				value = null;
			}
		});
		b.setPreferredSize(new Dimension(27,b.getPreferredSize().height));
		add(b,gc);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		if (value != null) {
			tf.setText(value.toString());
		}
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
