package com.lmm.gui;

import java.awt.event.MouseAdapter;
import javax.swing.JPopupMenu;

public class PopupMenuShower extends MouseAdapter {
	private JPopupMenu popup = null;

	public PopupMenuShower() {
		super();
	}

	public PopupMenuShower(JPopupMenu popupMenu) {
		super();
		this.popup = popupMenu;
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
		showIfPopupTrigger(e);
	}

	protected void showIfPopupTrigger(java.awt.event.MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}