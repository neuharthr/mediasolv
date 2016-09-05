package com.lmm;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * A TreeTable example, showing a JTreeTable, operating on the local file
 * system.
 * 
 */

public class TreeTableExample0 {
	public static void main(String[] args) {
		new TreeTableExample0();
	}

	public TreeTableExample0() {

		try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame("TreeTable");
		JTreeTable treeTable = new JTreeTable(new FileSystemModel());

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		frame.getContentPane().add(new JScrollPane(treeTable));
		frame.pack();
		frame.show();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
