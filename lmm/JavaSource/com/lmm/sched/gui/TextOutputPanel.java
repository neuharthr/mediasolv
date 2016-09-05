package com.lmm.sched.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.lmm.gui.PopupMenuShower;
import com.lmm.tools.FormatUtils;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.ProcessStarter;

public class TextOutputPanel extends JTextPane {

    // some statuses map closely to MessageITProtocolConstants
    public static final short STATUS_ERROR = -1;
    public static final short STATUS_NORMAL = 0;
    public static final short STATUS_NORMAL_BL = 11;
    public static final short STATUS_NORMAL_MAG = 12;


    //pattern that matches fully qualified file names
    final Pattern FILE_MATCH_PATTER = Pattern.compile("([a-zA-Z]:[\\\\/].*(\\.\\w+))");
    public static final String LMM_LINK = "LMMLink";

    public static final String CR = System.getProperty("line.separator");
    public static final String FS = System.getProperty("file.separator");

    private JPopupMenu jPopupMenu = null;

    private JMenuItem jMenuItemPopupClear = null;

    private JMenuItem jMenuItemCopy = null;

    public TextOutputPanel() {
	super();

	addMouseListener( new PopupMenuShower(getJPopupMenu()) );
	
	final LinkController lc = new LinkController();
	addMouseListener( lc );
	addMouseMotionListener( lc );
    }

    private JPopupMenu getJPopupMenu() {
	if (jPopupMenu == null) {
	    jPopupMenu = new JPopupMenu();
	    jPopupMenu.setName("jPopupMenu");
	    jPopupMenu.add(getJMenuItemCopy());
	    jPopupMenu.add(getJMenuItemPopupClear());

	    jPopupMenu.addPopupMenuListener(new PopupMenuListener() {
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	    });

	}

	return jPopupMenu;
    }

    private JMenuItem getJMenuItemCopy() {
	if (jMenuItemCopy == null) {
	    jMenuItemCopy = new JMenuItem();
	    jMenuItemCopy.setName("jMenuItemCopy");
	    jMenuItemCopy.setMnemonic('a');
	    jMenuItemCopy.setText("Copy");
	    jMenuItemCopy
		    .setToolTipText("Copys all the text in this output panel");

	    jMenuItemCopy
		    .addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    Document doc = getDocument();
				    try {
					doc.getText(0, doc.getLength());
					Clipboard clipboard = Toolkit
						.getDefaultToolkit()
						.getSystemClipboard();

					clipboard.setContents(
						new StringSelection(doc
							.getText(0, doc
								.getLength())),
						null);
				    } catch (BadLocationException be) {
					LMMLogger
						.error(
							"Unable to copy text from pane",
							be);
				    }
				}
			    });
			}
		    });

	}

	return jMenuItemCopy;
    }

    private JMenuItem getJMenuItemPopupClear() {
	if (jMenuItemPopupClear == null) {
	    jMenuItemPopupClear = new JMenuItem();
	    jMenuItemPopupClear.setName("jMenuItemPopupClear");
	    jMenuItemPopupClear.setMnemonic('c');
	    jMenuItemPopupClear.setText("Clear");
	    jMenuItemPopupClear
		    .setToolTipText("Clears the contents from this display");

	    jMenuItemPopupClear
		    .addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    TextOutputPanel.this.clear();
				}
			    });
			}
		    });

	}

	return jMenuItemPopupClear;
    }

    public void clear() {
	setText("");
    }

    public void addOutput(String msg) {
	addOutput(msg, STATUS_NORMAL);
    }

    public void addOutput(String msg, short status) {

	SwingUtilities.invokeLater(new WriteOutput("["
		+ FormatUtils.fullTime(new Date()) + "]  " + msg + CR, status));
    }

    class WriteOutput implements Runnable {
	private String msg = null;

	private short status = STATUS_NORMAL;

	// private javax.swing.JTextPane textPane = null;

	public WriteOutput(String msg_, short status_) {
	    super();
	    msg = msg_;
	    status = status_;
	}

	private java.awt.Color getColor() {
	    if (status <= STATUS_ERROR)
		return Color.RED;
	    else if (status == STATUS_NORMAL_BL) // start normal colors
		return Color.BLUE.darker();
	    else if (status == STATUS_NORMAL_MAG)
		return Color.MAGENTA;
	    else
		return Color.BLACK;
	}

	public void run() {
	    try {
		addText();

	    } catch (BadLocationException ble) {
		ble.printStackTrace();
	    }

	}

	private void addText() throws BadLocationException {
	    Document doc = getDocument();
	    SimpleAttributeSet attset = new SimpleAttributeSet();
	    
	    attset.addAttribute(StyleConstants.Foreground, getColor());
	    
	    Matcher m = FILE_MATCH_PATTER.matcher(msg);
	    if( m.find() ) {
		attset.addAttribute(StyleConstants.Underline, Boolean.TRUE);
		
		String link = m.group(); //save the file path
		link = link.replace("/", FS).replace("\\", FS);	//replace common OS file separtors with our current system separatir

		attset.addAttribute( LMM_LINK, link );
	    }

	    doc.insertString(doc.getLength(), msg, attset);
	    setCaretPosition(doc.getLength());
	}

    }

    class LinkController extends MouseAdapter implements MouseMotionListener {
    	
    	public void mouseDragged(MouseEvent ev) {}

		public void mouseMoved(MouseEvent ev) {
		    JTextPane editor = (JTextPane) ev.getSource();
	
		    if( !editor.isEditable() ) {
				Point pt = new Point(ev.getX(), ev.getY());
		
				int pos = editor.viewToModel(pt);
				if (pos >= 0) {
				    Document doc = editor.getDocument();
				    if (doc instanceof DefaultStyledDocument) {
						DefaultStyledDocument stDoc = (DefaultStyledDocument) doc;
						AttributeSet a = stDoc.getCharacterElement(pos).getAttributes();
						boolean isUnderline = StyleConstants.isUnderline( a );
			
						if( isUnderline ) {
						    setToolTipText("Open this location");
						    if (getCursor() != Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
							setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						    }
						}
						else {
						    setToolTipText(null);
						    if (getCursor() != Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
						    	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						    }
						}
				    }
				}
		    }
		    else {
			setToolTipText(null);
		    }
	}

	/**
         * Called for a mouse click event. If the component is read-only (ie a
         * browser) then the clicked event is used to drive an attempt to follow
         * the reference specified by a link.
         * 
         * @param e
         *                the mouse event
         * @see MouseListener#mouseClicked
         */
	public void mouseClicked(MouseEvent ev) {
	    JTextPane editor = (JTextPane) ev.getSource();

	    if (!editor.isEditable()) {
		Point pt = new Point(ev.getX(), ev.getY());
		int pos = editor.viewToModel(pt);
		if (pos >= 0) {

		    Document doc = editor.getDocument();
		    if (doc instanceof DefaultStyledDocument) {
			DefaultStyledDocument stDoc = (DefaultStyledDocument) doc;
			AttributeSet attset = stDoc.getCharacterElement(pos).getAttributes();
			boolean isUnderline = StyleConstants.isUnderline( attset );

			if( isUnderline ) {
			    ProcessStarter ps = new ProcessStarter();
			    ps.startProcess(
				    ProcessStarter.EXEC_EXPLORER + attset.getAttribute(LMM_LINK).toString(),
				    null,
				    null);

			}
		    }
		}
	    }
	}
    }

}