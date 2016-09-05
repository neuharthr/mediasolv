package swingbeanformbuilder.gui.swing.form;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JTabbedPane;

import swingbeanformbuilder.core.model.ClassModel;

public class SBFBTabbedForm extends AbstractSBFBForm {

	private JTabbedPane tab;

	public SBFBTabbedForm(Window parent, ClassModel cm, SBFBForm mainForm) {
		this.parent = parent;
		this.classModel = cm;
		setLayout(new BorderLayout());
		tab = new JTabbedPane();
		add(tab, BorderLayout.CENTER);
		addSBFBForm(mainForm,cm.getLabel() != null ? cm.getLabel() : "Main");
	}

	public void addSBFBForm(AbstractSBFBForm form, String name) {
		fields.putAll(form.getFields());
		form.setFields(fields);
		tab.addTab(name,form);
	}

}
