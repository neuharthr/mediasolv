package swingbeanformbuilder.gui.swing.form.components;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import swingbeanformbuilder.core.model.AllowedValue;

/**
 * Create a group of radio button from a field (containing
 * &lt;allowed-values/&gt; in its configuration).
 * 
 * @author s-oualid
 */
public class SBFBRadioButtonPanel extends JPanel {

	private Map buttons = new HashMap();

	private ButtonGroup group = new ButtonGroup();

	public SBFBRadioButtonPanel(List allowedValues) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 2));
		Iterator it = allowedValues.iterator();
		while (it.hasNext()) {
			AllowedValue element = (AllowedValue) it.next();
			JRadioButton b = new JRadioButton(element.toString());
			group.add(b);
			add(b);
			buttons.put(element, b);
		}
	}

	public void setSelectedItem(String v) {
		AllowedValue a = new AllowedValue();
		a.setValue(v);
		if (((JRadioButton) buttons.get(a)) != null) {
			((JRadioButton) buttons.get(a)).setSelected(true);
		}
	}

	public Object getSelectedItem() {
		Iterator it = buttons.keySet().iterator();
		while (it.hasNext()) {
			AllowedValue element = (AllowedValue) it.next();
			JRadioButton b = (JRadioButton) buttons.get(element);
			if (b.isSelected()) {
				return element;
			}
		}
		return null;
	}

	public void setEditable(boolean b) {
		Iterator it = buttons.keySet().iterator();
		while (it.hasNext()) {
			AllowedValue element = (AllowedValue) it.next();
			JRadioButton bu = (JRadioButton) buttons.get(element);
			bu.setEnabled(b);
		}
	}
}