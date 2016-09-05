package com.lmm.gui;

import java.awt.Color;


public interface DisplayableModel {


	public Color getCellBGColor(int row, int col);
	public Color getCellFGColor(int row, int col);
	public Color getSelectedFGColor(int row, int col);

	public String getToolTip(int row, int col);
}
