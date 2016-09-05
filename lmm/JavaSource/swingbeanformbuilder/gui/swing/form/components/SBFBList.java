package swingbeanformbuilder.gui.swing.form.components;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.core.services.ISBFBFormFactory;
import swingbeanformbuilder.gui.swing.model.CollectionListModel;

/**
 * Simple list component used as default to handle 1-n associations (List, Set...).
 * 
 * @author s-oualid
 */
public class SBFBList extends JList {

	private boolean editable = true;
	private Class myClass;
	private FieldModel myFieldModel;
	private JMenuItem delete;
	private JMenuItem add;
	private JMenuItem modify;
	
	public SBFBList(FieldModel fm, final Window parent, final Class classToAdd) {
		super(new CollectionListModel(new ArrayList()));
		this.myClass = classToAdd;
		this.myFieldModel = fm;
		setOpaque(true);
		if (fm.isAllowDigging()) {
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						FormBuilder.showEditDialog(getSelectedValue(),parent,isEditable());
					}
				}
			});
		}
		if (fm.isAllowDelete() && fm.isSetter()) {
			addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						removeSelectedElements();
					}
				}
			});
		}		
		if (fm.getSelectionMode() != null) {
			if (ISBFBFormFactory.SELECTION_MODE_SINGLE.equals(fm.getSelectionMode())) {
				setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} else if (ISBFBFormFactory.SELECTION_MODE_INTERVAL.equals(fm.getSelectionMode())) {
				setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			} else if (ISBFBFormFactory.SELECTION_MODE_MULTIPLE.equals(fm.getSelectionMode())) {
				setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			} else {
				throw new SBFBException("Unknown selection mode : '"
						+ fm.getSelectionMode() + "' for field '"
						+ fm.getName() + "' of class '"
						+ fm.getClassModel().getClassName() + "'");
			}
		}		

		if (fm.isShowMenu() && fm.isAllowDigging()) {
			final JPopupMenu menu = new JPopupMenu();
			add = new JMenuItem(SBFBConfiguration.getNewLabel());
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						Object o = classToAdd.newInstance();
						((CollectionListModel) getModel()).add(o);
						FormBuilder.showEditDialog(o,parent,isEditable());
					} catch (Exception e) {
						throw new SBFBException(e);
					}
				}
			});
			if (classToAdd == null) {
				add.setEnabled(false);
			}
			menu.add(add);
			modify = new JMenuItem(SBFBConfiguration.getModifyLabel());
			modify.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (getSelectedValue() != null) {
						FormBuilder.showEditDialog(getSelectedValue(),parent,isEditable());
					}
				}
			});
			if (!fm.isSetter() || !fm.isAllowDigging()) {
				modify.setEnabled(false);
			}
			menu.add(modify);
			delete = new JMenuItem(SBFBConfiguration.getDeleteLabel());
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeSelectedElements();				
				}
			});
			if (!fm.isAllowDelete()) {
				delete.setEnabled(false);
			}
			menu.add(delete);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						menu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						menu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			});
		}		
	}
	
	private void removeSelectedElements() {
		if (getSelectedIndex() >= 0) {
			int[] s = getSelectedIndices();
			for (int i = 0; i < s.length; i++) {
				((CollectionListModel) getModel()).remove(s[i]);	
			}
		}
	}

	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (add != null) {
			if (!editable) {
				add.setEnabled(false);
				modify.setEnabled(false);
				delete.setEnabled(false);
			} else {
				add.setEnabled(myClass != null);
				modify.setEnabled(myFieldModel.isSetter());
				delete.setEnabled(myFieldModel.isAllowDelete());
			}
		}
		if (editable) {
			setBackground(Color.WHITE);
		} else {
			setBackground(SystemColor.control);
		}
	}
	
}
