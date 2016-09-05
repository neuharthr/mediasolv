/*
 * FormBuilder.java
 * 
 * Created on 20 mai 2006, 01:35
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core.services.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.Utils;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.ClassModel;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.model.GroupModel;
import swingbeanformbuilder.core.parser.SBFBConfigurationContentHandler;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.core.services.ISBFBFormFactory;
import swingbeanformbuilder.gui.ISBFBForm;
import swingbeanformbuilder.gui.swing.document.SBFBBigDecimalDocument;
import swingbeanformbuilder.gui.swing.document.SBFBDoubleDocument;
import swingbeanformbuilder.gui.swing.document.SBFBIntegerDocument;
import swingbeanformbuilder.gui.swing.document.SBFBStandardDocument;
import swingbeanformbuilder.gui.swing.form.AbstractSBFBForm;
import swingbeanformbuilder.gui.swing.form.SBFBForm;
import swingbeanformbuilder.gui.swing.form.SBFBTabbedForm;
import swingbeanformbuilder.gui.swing.form.components.ISBFBCustomComponent;
import swingbeanformbuilder.gui.swing.form.components.SBFBHorizontalGradientLabel;
import swingbeanformbuilder.gui.swing.form.components.SBFBList;
import swingbeanformbuilder.gui.swing.form.components.SBFBOneOnOneTextField;
import swingbeanformbuilder.gui.swing.form.components.SBFBRadioButtonPanel;
import swingbeanformbuilder.gui.swing.form.components.SBFBTextArea;
import swingbeanformbuilder.gui.swing.table.SBFBTable;

/**
 * Swing implementation of ISBFBFormBuilder.
 * 
 * @author s-oualid
 */
public class SwingSBFBFormBuilderImpl implements ISBFBFormFactory {

	private static boolean verticalGlueAdded = false;

	/**
	 * Build a standard JPanel with entry fields for the specified class using
	 * introspection and current configuration.
	 */
	public synchronized AbstractSBFBForm buildForm(Class aClass, Window aWindow) {
		ClassModel c = FormBuilder.refreshClassModelUsingIntrospection(aClass);
		SBFBForm panel = new SBFBForm(aWindow, c);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		// gc.anchor = GridBagConstraints.WEST;

		gc.insets = new Insets(2, 2, 2, 2);
		gc.gridx = 0;
		List l = new ArrayList(c.getFields().values());
		Collections.sort(l);
		Iterator it = l.iterator();

		if (c.isShowBanner()) {
			String n = c.getLabel() != null ? c.getLabel() : "Main";
			ImageIcon im = null;
			if (c.getIcon() != null) {
				im = new ImageIcon(Utils.getUrlFromClassPath(c.getIcon()));
			}
			createTitle(c, panel, gc, n, im);
		}

		SBFBTabbedForm tabbedPanel = new SBFBTabbedForm(aWindow, c, panel);
		SBFBForm currentGroupPanel = null;
		GroupModel currentGroupModel = null;
		GridBagConstraints currentGroupConstraints = new GridBagConstraints();
		currentGroupConstraints.anchor = GridBagConstraints.WEST;
		verticalGlueAdded = false;

		FieldModel field = null;
		AbstractSBFBForm panelToReturn = panel;

		while (it.hasNext()) {
			field = (FieldModel) it.next();
			if (field.isVisible()) {
				if (field.getGroup() != null) {
					// New group
					if (currentGroupPanel == null || currentGroupModel != field.getGroup()) {
						if (currentGroupPanel != null) {
							if (SBFBConfigurationContentHandler.RENDER_AS_TAB_VALUE.equals(currentGroupModel.getRender())) {
								tabbedPanel.addSBFBForm(currentGroupPanel, currentGroupModel.getLabel());
								panelToReturn = tabbedPanel;
							} else {
								handleEndOfGroup(c, panel, gc, currentGroupConstraints, currentGroupPanel, currentGroupModel, field);
							}
							currentGroupPanel = null;
							currentGroupModel = null;
						}

						currentGroupPanel = new SBFBForm(panel.getParentWindow(), panel.getClassModel());
						currentGroupPanel.setLayout(new GridBagLayout());
						currentGroupConstraints.gridx = 0;
						currentGroupConstraints.gridy = 0;
						currentGroupConstraints.fill = GridBagConstraints.HORIZONTAL;
						currentGroupConstraints.insets = new Insets(2, 2, 2, 2);
						currentGroupConstraints.weightx = 0;
						currentGroupConstraints.weighty = 0;
						if (field.getGroup().isShowBanner()) {
							String n = field.getGroup().getLabel() != null ? field.getGroup().getLabel() : "Main";
							ImageIcon im = null;
							if (field.getGroup().getIcon() != null) {
								im = new ImageIcon(Utils.getUrlFromClassPath(field.getGroup().getIcon()));
							}
							createTitle(c, currentGroupPanel, currentGroupConstraints, n, im);
						}
						if (ISBFBFormFactory.FIELD_ALIGN_RIGHT.equals(field.getGroup().getAlign())) {
							FieldModel dummyField = new FieldModel(c);
							dummyField.setLabel("");
							dummyField.setFill("horizontal");
							dummyField.setLastOfLine(false);
							addComponentInSBFBGridBag(Box.createHorizontalGlue(), c, currentGroupPanel, currentGroupConstraints, dummyField);
						}
					}
					gc.weighty = 0;
					Component subco = getRightComponentForField(panel, currentGroupConstraints, field);
					addComponentInSBFBGridBag(subco, c, currentGroupPanel, currentGroupConstraints, field);
					currentGroupModel = field.getGroup();
					continue;
				} else {
					if (currentGroupPanel != null) {
						if (SBFBConfigurationContentHandler.RENDER_AS_TAB_VALUE.equals(currentGroupModel.getRender())) {
							tabbedPanel.addSBFBForm(currentGroupPanel, currentGroupModel.getLabel());
							panelToReturn = tabbedPanel;
						} else {
							handleEndOfGroup(c, panel, gc, currentGroupConstraints, currentGroupPanel, currentGroupModel, field);
						}
						currentGroupPanel = null;
						currentGroupModel = null;
					}
				}
				gc.weighty = 0;
				Component co = getRightComponentForField(panel, gc, field);
				addComponentInSBFBGridBag(co, c, panel, gc, field);
			}
		}
		if (currentGroupPanel != null) {
			if (SBFBConfigurationContentHandler.RENDER_AS_TAB_VALUE.equals(currentGroupModel.getRender())) {
				tabbedPanel.addSBFBForm(currentGroupPanel, currentGroupModel.getLabel());
				panelToReturn = tabbedPanel;
			} else {
				handleEndOfGroup(c, panel, gc, currentGroupConstraints, currentGroupPanel, currentGroupModel, field);
			}
			currentGroupPanel = null;
			currentGroupModel = null;
		}

		// If no component fill the vertical extra space, we had a "glue" to the
		// bottom of the form...
		// so the form is stuck at the bottom
		if (!verticalGlueAdded) {
			field = new FieldModel(c);
			field.setLabel("");
			field.setFill("both");
			addComponentInSBFBGridBag(Box.createGlue(), c, panel, gc, field);
		}

		// panelToReturn.handleEditable();
		return panelToReturn;
	}

	private void handleEndOfGroup(ClassModel c, SBFBForm panel, GridBagConstraints gc, GridBagConstraints currentGroupConstraints, SBFBForm currentGroupPanel, GroupModel currentGroupModel, FieldModel field) {
		// We have to generate a "fake" field to add our inner panel in the main
		// SBFB panel
		FieldModel oldField = field;
		field = new FieldModel(c);
		field.setLabel("");
		field.setFill(currentGroupModel.getFill());
		field.setAlign(currentGroupModel.getAlign());
		field.setLastOfLine(currentGroupModel.isLastOfLine());
		if (!SBFBConfigurationContentHandler.NO_BORDER_VALUE.equals(currentGroupModel.getBorder())) {
			currentGroupPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), currentGroupModel.getLabel()));
		}
		if (ISBFBFormFactory.FIELD_ALIGN_LEFT.equals(currentGroupModel.getAlign())) {
			currentGroupConstraints.gridx += 2;
			addComponentInSBFBGridBag(Box.createHorizontalGlue(), c, currentGroupPanel, currentGroupConstraints, field);
		}
		addComponentInSBFBGridBag(currentGroupPanel, c, panel, gc, field);
		field = oldField;
	}

	private static void createTitle(ClassModel c, SBFBForm panel, GridBagConstraints gc, String n, ImageIcon im) {
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = Math.max(2, c.getMaxColumnSpanned() + 1);
		gc.weightx = 1;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		Insets oldInsets = gc.insets;
		gc.insets = new Insets(0, 0, 4, 0);
		panel.add(new SBFBHorizontalGradientLabel(im, n), gc);
		gc.insets = oldInsets;
		gc.gridy++;
	}

	private static void addComponentInSBFBGridBag(Component co, ClassModel c, JPanel panel, GridBagConstraints gc, FieldModel field) {
		gc.weightx = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		String n = field.getLabel() != null ? field.getLabel() : field.getName();

		// On n'affiche pas de label pour les Panel, sauf les associations 1-1
		if (!ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals(field.getType()) && n.length() > 0) {
			JLabel label = new JLabel(n.substring(0, 1).toUpperCase() + n.substring(1), SwingConstants.RIGHT);
			if (field.getLabelColor() != null) {
				label.setForeground(field.getLabelColor());
			}
			if (field.isBoldLabel()) {
				label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
			}
			if( field.getToolTip() != null ) {
				label.setToolTipText( field.getToolTip() );
			}
			
			int oldfill = gc.fill;
			gc.fill = GridBagConstraints.HORIZONTAL;
			panel.add(label, gc);
			gc.fill = oldfill;
		}

		gc.gridx++;

		if (field.isLastOfLine()) {
			field.setColumnSpanned(c.getMaxColumnSpanned() - gc.gridx + 1);
		}

		gc.gridwidth = field.getColumnSpanned();
		handleFillOptions(gc, field);
		if ("none".equals(field.getFill())) {
			gc.weightx = 0;
		} else {
			gc.weightx = 1;
		}
		panel.add(co, gc);

		if (field.isLastOfLine()) {
			gc.gridy++;
			gc.gridx = 0;
		} else {
			gc.gridx++;
		}
	}

	private static Component getRightComponentForField(ISBFBForm panel, GridBagConstraints gc, FieldModel field) {
		Component co = null;
		// We check if the class of this component is handled by SBFB
		if (field.getCustomComponent() == null) {
			ClassModel fieldClassModel = (ClassModel) SBFBConfiguration.getClasses().get(field.getClassName());
			if (fieldClassModel != null && fieldClassModel.getCustomComponent() != null) {
				field.setCustomComponent(fieldClassModel.getCustomComponent());
			}
		}
		// If the field has allowed values, we don't create a custom component
		// for it
		if (field.getCustomComponent() != null && field.getAllowedValues() == null) {
			co = createCustomComponent(panel, field, gc);
		} else if (ISBFBFormFactory.FIELD_TYPE_LIST.equals(field.getType())) {
			if (ISBFBFormFactory.FIELD_RENDER_TABLE.equals(field.getRender())) {
				co = createTableComponent(panel, field, gc);
			} else if (ISBFBFormFactory.FIELD_RENDER_LIST.equals(field.getRender())) {
				co = createListComponent(panel, field, gc);
			} else {
				throw new SBFBException("Unknown render mode : '" + field.getRender() + "' for field '" + field.getName() + "' of class '" + field.getClassModel().getClassName() + "'");
			}
		} else if (field.getAllowedValues() != null) {
			if (ISBFBFormFactory.FIELD_TYPE_RADIO.equals(field.getType())) {
				co = createRadioComponent(panel, field, gc);
			} else if (ISBFBFormFactory.FIELD_TYPE_COMBO.equals(field.getType())) {
				co = createComboComponent(panel, field, gc);
			} else {
				throw new SBFBException("Unknown type : '" + field.getType() + "' for field '" + field.getName() + "' of class '" + field.getClassModel().getClassName() + "'");
			}
		} else if (ISBFBFormFactory.FIELD_TYPE_BOOLEAN.equals(field.getType())) {
			if (ISBFBFormFactory.FIELD_RENDER_TOGGLE.equals(field.getRender())) {
				co = createToggleComponent(panel, field, gc);
			} else {
				co = createCheckboxComponent(panel, field, gc);
			}
		} else if (ISBFBFormFactory.FIELD_TYPE_IMAGE.equals(field.getType())) {
			co = createImageComponent(panel, field, gc);
		} else {
			co = createTextComponent(panel, field, gc);
		}
		return co;
	}

	private static Component createCustomComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		try {
			ISBFBCustomComponent co = (ISBFBCustomComponent) Class.forName(field.getCustomComponent()).newInstance();
			handleFillOptions(gc, field);
			panel.getFields().put(field.getName(), co);
			// for custom textfields, check if it has a max length
			if (co instanceof JTextField) {
				handleMaxLength(field, gc, (Component) co);
			}
			return (Component) co;
		} catch (Exception e) {
			throw new SBFBException("Cannot instanciate custom component (" + field.getCustomComponent() + ") !", e);
		}
	}

	private static Component createImageComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		String n = field.getLabel() != null ? field.getLabel() : field.getName();
		n = n.substring(0, 1).toUpperCase() + n.substring(1);
		JLabel caption = new JLabel(n, SwingConstants.CENTER);
		caption.setFont(new Font(caption.getFont().getName(), Font.BOLD, caption.getFont().getSize()));
		p.add(caption);
		JLabel img = new JLabel();
		img.setHorizontalAlignment(SwingConstants.CENTER);
		p.add(img);

		if( field.getToolTip() != null ) {
			img.setToolTipText( field.getToolTip() );
		}

		panel.getFields().put(field.getName(), img);
		return p;
	}

	private static Component createTableComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		SBFBTable table = new SBFBTable(field, panel.getParentWindow());
		JScrollPane p = new JScrollPane(table);
		p.setPreferredSize(new Dimension(200, 150));
		handleFillOptions(gc, field);
		panel.getFields().put(field.getName(), table);
		return p;
	}

	private static Component createCheckboxComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		String n = field.getLabel() != null ? field.getLabel() : field.getName();
		n = n.substring(0, 1).toUpperCase() + n.substring(1);
		JCheckBox box = new JCheckBox(n);
		handleFillOptions(gc, field);

		if( field.getToolTip() != null ) {
			box.setToolTipText( field.getToolTip() );
		}

		panel.getFields().put(field.getName(), box);		
		return box;
	}

	private static Component createRadioComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		SBFBRadioButtonPanel p = new SBFBRadioButtonPanel(field.getAllowedValues());
		panel.getFields().put(field.getName(), p);
		return p;
	}

	private static Component createTextComponent(final ISBFBForm panel, final FieldModel field, final GridBagConstraints gc) {
		JComponent tf = null;
		if (ISBFBFormFactory.FILL_BOTH.equals(field.getFill()) || ISBFBFormFactory.FILL_VERTICAL.equals(field.getFill())) {
			tf = new SBFBTextArea();
		} else if (ISBFBFormFactory.FIELD_TYPE_DATE.equals(field.getType())) {
			tf = new JFormattedTextField(SBFBConfiguration.getDateFormat());
		} else if (ISBFBFormFactory.FIELD_TYPE_STRING.equals(field.getType()) || ISBFBFormFactory.FIELD_TYPE_BIGDECIMAL.equals(field.getType()) || ISBFBFormFactory.FIELD_TYPE_DOUBLE.equals(field.getType()) || ISBFBFormFactory.FIELD_TYPE_LONG.equals(field.getType()) || ISBFBFormFactory.FIELD_TYPE_INTEGER.equals(field.getType())) {
			if( field.isPassword() )
				tf = new JPasswordField();
			else
				tf = new JTextField();
		} else {
			try {
				Class cl = null;
				if (field.getClassName() != null) {
					cl = Class.forName(field.getClassName());
				}
				tf = new SBFBOneOnOneTextField(panel.getParentWindow(), cl);
			} catch (ClassNotFoundException e) {
				throw new SBFBException("Can't bind class " + field.getClassName(), e);
			}
		}
		SBFBStandardDocument document = null;
		handleMaxLength(field, gc, tf);
		handleFillOptions(gc, field);
		if (ISBFBFormFactory.FIELD_TYPE_INTEGER.equals(field.getType()) || ISBFBFormFactory.FIELD_TYPE_LONG.equals(field.getType())) {
			document = new SBFBIntegerDocument();
		} else if (ISBFBFormFactory.FIELD_TYPE_DOUBLE.equals(field.getType())) {
			document = new SBFBDoubleDocument();
		} else if (ISBFBFormFactory.FIELD_TYPE_BIGDECIMAL.equals(field.getType())) {
			document = new SBFBBigDecimalDocument();
		} else if (ISBFBFormFactory.FIELD_TYPE_STRING.equals(field.getType())) {
			document = new SBFBStandardDocument();
		}

		if (field.getMaxlength() != null) {
			document.setMaxLength(field.getMaxlength().intValue());
		}
		if (tf instanceof JTextComponent && document != null) {
			((JTextComponent) tf).setDocument(document);
		}

		if (field.getBackgroundColor() != null) {
			tf.setBackground(field.getBackgroundColor());
		}

		Component c = null;
		if (tf instanceof JTextArea) {
			JScrollPane scroll = new JScrollPane(tf);
			c = scroll;
		} else {
			c = tf;
		}
		panel.getFields().put(field.getName(), tf);
		return c;
	}

	private static void handleMaxLength(final FieldModel field, final GridBagConstraints gc, Component tf) {
		if (field.getMaxlength() != null) {
			gc.weightx = 0;
			gc.fill = GridBagConstraints.NONE;
			if (tf instanceof JTextField) {
				((JTextField) tf).setColumns(field.getMaxlength().intValue());
			}
		} else {
			gc.weightx = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
		}
	}

	private static void handleFillOptions(final GridBagConstraints gc, final FieldModel field) {
		if (field.getFill() != null) {
			if (ISBFBFormFactory.FILL_HORIZONTAL.equals(field.getFill())) {
				gc.weightx = 1;
				gc.weighty = 0;
				gc.fill = GridBagConstraints.HORIZONTAL;
			} else if (ISBFBFormFactory.FILL_NONE.equals(field.getFill())) {
				gc.weightx = 0;
				gc.weighty = 0;
				gc.fill = GridBagConstraints.NONE;

			} else if (ISBFBFormFactory.FILL_VERTICAL.equals(field.getFill())) {
				gc.weightx = 0;
				gc.weighty = 1;
				gc.fill = GridBagConstraints.VERTICAL;
				verticalGlueAdded = true;
			} else if (ISBFBFormFactory.FILL_BOTH.equals(field.getFill())) {
				gc.weightx = 1;
				gc.weighty = 1;
				gc.fill = GridBagConstraints.BOTH;
				verticalGlueAdded = true;
			} else {
				throw new SBFBException("Unknown fill mode : '" + field.getFill() + "' for field '" + field.getName() + "' of class '" + field.getClassModel().getClassName() + "'");
			}
		}
		if (field.getAlign() != null) {
			if (ISBFBFormFactory.FIELD_ALIGN_LEFT.equals(field.getAlign())) {
				gc.anchor = GridBagConstraints.WEST;
			} else if (ISBFBFormFactory.FIELD_ALIGN_RIGHT.equals(field.getAlign())) {
				gc.anchor = GridBagConstraints.EAST;
			} else if (ISBFBFormFactory.FIELD_ALIGN_CENTER.equals(field.getAlign())) {
				gc.anchor = GridBagConstraints.CENTER;
			} else {
				throw new SBFBException("Unknown align mode : '" + field.getAlign() + "' for field '" + field.getName() + "' of class '" + field.getClassModel().getClassName() + "'");
			}
		}
	}

	private static Component createToggleComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		JToggleButton b = new JToggleButton(field.getLabel() != null ? field.getLabel() : field.getName());
		panel.getFields().put(field.getName(), b);
		
		if( field.getToolTip() != null ) {
			b.setToolTipText( field.getToolTip() );
		}
		
		return b;
	}

	private static Component createComboComponent(ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		JComboBox box = new JComboBox(new DefaultComboBoxModel(field.getAllowedValues().toArray()));
		panel.getFields().put(field.getName(), box);
		
		return box;
	}

	private static Component createListComponent(final ISBFBForm panel, FieldModel field, GridBagConstraints gc) {
		Class c = null;
		try {
			if (field.getClassName() != null) {
				c = Class.forName(field.getClassName());
			}
		} catch (Exception e) {
			throw new SBFBException("Cannot instanciate specified class (to let the user add it into a list) " + field.getClassName(), e);
		}
		SBFBList list = new SBFBList(field, panel.getParentWindow(), c);

		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;
		panel.getFields().put(field.getName(), list);
		return new JScrollPane(list);
	}

	public JDialog showEditDialog(final Object s, Window parent, boolean editable) {
		JDialog dialog = null;
		if (parent instanceof Frame) {
			dialog = new JDialog((Frame) parent);
		} else {
			dialog = new JDialog((Dialog) parent);
		}
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.setModal(true);
		final AbstractSBFBForm form = buildForm(s.getClass(), dialog);

		form.loadData(s);

		dialog.getContentPane().add(form, BorderLayout.CENTER);

		final JDialog finalDialog = dialog;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JButton button = new JButton("Ok");
		ActionListener okAl = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				form.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				form.saveData(s);
				form.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				finalDialog.dispose();
			}
		};
		button.addActionListener(okAl);
		button.setEnabled(editable);
		panel.add(Box.createHorizontalGlue());
		panel.add(button);
		button = new JButton("Cancel");

		ActionListener cancelAl = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				finalDialog.dispose();
			}
		};
		button.addActionListener(cancelAl);
		panel.add(button);
		panel.add(Box.createHorizontalGlue());
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);

		form.setEditable(editable);

		if (!"".equals(s.toString().trim())) {
			dialog.setTitle("Detail of " + s);
		} else {
			dialog.setTitle("New ...");
		}

		// Shortcuts
		form.registerKeyboardAction(okAl, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		form.registerKeyboardAction(cancelAl, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		dialog.pack();
		final Dimension minDimension = dialog.getSize();
		final JDialog fdialog = dialog;
		dialog.addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				if (fdialog.getSize().width < minDimension.width) {
					fdialog.setSize(minDimension.width, fdialog.getSize().height);
				}
				if (fdialog.getSize().height < minDimension.height) {
					fdialog.setSize(fdialog.getSize().width, minDimension.height);
				}
			}
		});
		dialog.setBounds(panel.getParent().getBounds().x + 30, panel.getParent().getBounds().y + 30, dialog.getWidth(), dialog.getHeight());
		dialog.setVisible(true);
		return dialog;
	}

}
