package com.lmm.gui.files;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

class ChooserView extends FileView {

	private OvalIcon[] ovalIcons = new OvalIcon[ChooserAccessory.FilterColors.values().length];
	private ImageIcon[] imgIcons = new ImageIcon[ChooserAccessory.FilterColors.values().length];

	public ChooserView() {
		for( int i = 0; i < ChooserAccessory.FilterColors.values().length; i++ )
			imgIcons[i] = ChooserAccessory.FilterColors.values()[i].getImageIcon();
	}

	public String getName(File f) {
		String s = f.getName();
		if (s.length() == 0) {
			s = f.getAbsolutePath();
		}
		return s;
	}

	public String getDescription(File f) {
		return f.getName();
	}

	public String getTypeDescription(File f) {
		return f.getAbsolutePath();
	}

	public Icon getIcon(File f) {
		for( int i = 0; i < ChooserAccessory.FilterColors.values().length; i++ ) {
			if( ChooserAccessory.FilterColors.values()[i].getFilter().accept(f) )
				return imgIcons[i];
		}

		return null;
	}

	/**
	 * Do not allow the traversing of directories for this view
	 */
	public Boolean isTraversable(File file) {
		return false;
	}


	class OvalIcon implements Icon {
		Color color;

		public OvalIcon(Color c) {
			color = c;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillOval(x, y, getIconWidth(), getIconHeight());
		}

		public int getIconWidth() {
			return 10;
		}

		public int getIconHeight() {
			return 15;
		}
	}
}
