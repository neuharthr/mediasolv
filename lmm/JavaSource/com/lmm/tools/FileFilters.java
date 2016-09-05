package com.lmm.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class FileFilters {
	
	public static FileFilter XMLFileFilterGUI = new FileFilter() {
	    public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".xml");
	    }
	
	    public String getDescription() {
	        return "Theme Files (*.xml)";
	    }
	};

	public static FileFilter ImageFileFilterGUI = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".gif")
				|| f.getName().toLowerCase().endsWith(".jpg")
				|| f.getName().toLowerCase().endsWith(".bmp");
		}
	
		public String getDescription() {
			return "Images (*.gif, *.jpg, *.bmp)";
		}
	};

	public static FileFilter MPGFileFilterGUI = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith(".mpg")
				|| f.getName().toLowerCase().endsWith(".mpeg");
		}
	
		public String getDescription() {
			return "Video Files (*.mpg, *.mpeg)";
		}
	};


	

	/* *****
	 * Non GUI filters
	 * ****/
	public static java.io.FileFilter XMLFileFilter = new java.io.FileFilter() {
	    public boolean accept(File f) {
			return f != null && f.isFile()
					&& f.getName().toLowerCase().endsWith(".xml");
	    }
	};

	public static java.io.FileFilter FileFilter = new java.io.FileFilter() {
	    public boolean accept(File f) {
			return f != null && f.isFile();
	    }
	};

	public static java.io.FileFilter LogFileFilter = new java.io.FileFilter() {
	    public boolean accept(File f) {
			return f != null && f.isFile()
					&& f.getName().toLowerCase().endsWith(".log");
	    }
	};

	//proof of performance file types
	public static java.io.FileFilter PoPFileFilter = new java.io.FileFilter() {
	    public boolean accept(File f) {
			return f != null && f.isFile()
				&& (f.getName().toLowerCase().endsWith(".jpg")
					|| f.getName().toLowerCase().endsWith(".gif"));
	    }
	};

}