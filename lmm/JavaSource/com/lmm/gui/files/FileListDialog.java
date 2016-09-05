package com.lmm.gui.files;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.lmm.gui.GenericTableModel;
import com.lmm.gui.PopupMenuShower;
import com.lmm.gui.SelectableCellRenderer;
import com.lmm.gui.TableSorter;
import com.lmm.msg.BaseMsg;
import com.lmm.sched.gui.LMMJFrame;
import com.lmm.sched.proc.LMMUtils;


public class FileListDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel jContentPane = null;
    private final String destination;

	private javax.swing.JTable jTableData = null;
	private javax.swing.JScrollPane jScrollPaneTable = null;
	private TableSorter sorterTableModel = null;

	private javax.swing.JLabel jLabel = null;
	
	private JPopupMenu jPopupMenu = null;
	private JMenuItem jMenuItemPopupDelete = null;
	private JMenuItem jMenuItemPopupRename = null;
	private JMenuItem jMenuItemPopupGetFile = null;

	public FileListDialogActions fileListActions = null;
	
	//a message created by this dialog if the user chooses so
	private BaseMsg[] nextMsg = null;

    /**
     * This is the default constructor
     */
    private FileListDialog() {
        super();
        initialize();
        destination = null;
    }

	public FileListDialog(LMMJFrame parent, final String destination) {
		super(parent);
		initialize();
		this.destination = destination;
	}
	
	private LMMJFrame getParentLMMFrame() {
		return (LMMJFrame)getParent();
	}

	public void setVisible(boolean val) {
		//always show this dialog in the middle of the parent
		if( val ) {
			setLocation(
				getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2, 
				getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2 );
		}

		//super.show();
		super.setVisible(val);

		//if the dialog is going away
		if( !val ) {
			execute();
			dispose();
		}

	}

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(560, 450);
        this.setContentPane(getJContentPane());
        
        fileListActions = new FileListDialogActions(this);

    	MouseListener listener = new PopupMenuShower( getJPopupMenu() );
    	getJTableData().addMouseListener( listener );    	
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
            consGridBagConstraints3.insets = new java.awt.Insets(5,6,2,6);
            consGridBagConstraints3.ipadx = 177;
            consGridBagConstraints3.gridy = 0;
            consGridBagConstraints3.gridx = 0;
            consGridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTH;
            consGridBagConstraints2.insets = new java.awt.Insets(2,5,5,4);
            consGridBagConstraints2.ipady = 43;
            consGridBagConstraints2.ipadx = 67;
            consGridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
            consGridBagConstraints2.weighty = 1.0;
            consGridBagConstraints2.weightx = 1.0;
            consGridBagConstraints2.gridy = 1;
            consGridBagConstraints2.gridx = 0;
            jContentPane.setLayout(new java.awt.GridBagLayout());
            jContentPane.add(getJScrollPaneTable(), consGridBagConstraints2);
            jContentPane.add(getJLabel(), consGridBagConstraints3);
        }
        return jContentPane;
    }
	/**
	 * This method initializes jTableData
	 * 
	 * @return javax.swing.JTable
	 */
	protected javax.swing.JTable getJTableData() {
		if(jTableData == null) {
			jTableData = new javax.swing.JTable();

			jTableData.setIntercellSpacing( new Dimension(0, 0) );
			jTableData.setShowGrid( false );
			jTableData.setRowHeight( jTableData.getFont().getSize() + 8 );

			jTableData.setAutoCreateColumnsFromModel( true );
			jTableData.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);

			SelectableCellRenderer selRend = new SelectableCellRenderer();
			jTableData.setDefaultRenderer( Date.class, selRend );
			jTableData.setDefaultRenderer( String.class, selRend );
			jTableData.setDefaultRenderer( Long.class, selRend );
			jTableData.setDefaultRenderer( Object.class, selRend );

			jTableData.createDefaultColumnsFromModel();

			jTableData.getTableHeader().setPreferredSize(
					new Dimension(jTableData.getTableHeader().getWidth(), jTableData.getRowHeight()) );	
		}

		return jTableData;
	}
	
	protected TableSorter getTableModel() {
		return sorterTableModel;
	}
	
	public void setTableModel( String[] columns, Class[] colClasses, Vector<Vector> v ) {
		GenericTableModel tableModel = new GenericTableModel( columns, colClasses );
		tableModel.setRows( v );

		sorterTableModel = new TableSorter( tableModel );
		getJTableData().setModel( sorterTableModel );
		sorterTableModel.addMouseListenerToHeaderInTable( getJTableData() );
		
		//check for blank columns and shrink them, they are for spacing
		for( int i = 0; i < columns.length; i++ ) {
			if( columns[i] == null || columns[i].length() <= 0 )
				getJTableData().getColumnModel().getColumn(i).setMaxWidth( 5 );
		}
	}
	
	/**
	 * This method initializes jScrollPaneTable
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneTable() {
		if(jScrollPaneTable == null) {
			jScrollPaneTable = new javax.swing.JScrollPane();
			jScrollPaneTable.setViewportView(getJTableData());
			jScrollPaneTable.setPreferredSize(new java.awt.Dimension(410,300));
			jScrollPaneTable.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
		}
		return jScrollPaneTable;
	}

	private JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.setName("jPopupMenu");			
			
			jPopupMenu.add( getJMenuPopupGetFile() );
			jPopupMenu.add( getJMenuPopupRename() );
			jPopupMenu.add( getJMenuPopupDelete() );
			
	    	jPopupMenu.addPopupMenuListener( new PopupMenuListener() {
	    	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {	    	    	

	    	    	getJMenuPopupRename().setEnabled(
	    	    			getJTableData().getSelectedRowCount() == 1 );

	    	    	getJMenuPopupDelete().setEnabled(
	    	    			getJTableData().getSelectedRowCount() >= 0 );

	    	    	//only allow this through FTP
	    	    	getJMenuPopupGetFile().setEnabled(
	    	    			getJTableData().getSelectedRowCount() >= 0
	    	    			&& LMMUtils.isFTPEnabled() );
	    	    }

	    	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
	    	    public void popupMenuCanceled(PopupMenuEvent e) {}
			});

		}

		return jPopupMenu;
	}
	
	private JMenuItem getJMenuPopupGetFile() {
		if (jMenuItemPopupGetFile == null) {
			jMenuItemPopupGetFile = new JMenuItem();
			jMenuItemPopupGetFile.setName("jMenuItemPopupGet");
			jMenuItemPopupGetFile.setMnemonic('g');
			jMenuItemPopupGetFile.setText("Retrieve File(s)...");
			jMenuItemPopupGetFile.setToolTipText("Retrieves the selected files from the player (FTP configuration is required)");
			jMenuItemPopupGetFile.setEnabled(false);
			
			jMenuItemPopupGetFile.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							fileListActions.getFiles();
						}
					});
				}
			});
			
		}

		return jMenuItemPopupGetFile;
	}

	private JMenuItem getJMenuPopupDelete() {
		if (jMenuItemPopupDelete == null) {
			jMenuItemPopupDelete = new JMenuItem();
			jMenuItemPopupDelete.setName("jMenuItemPopupDelete");
			jMenuItemPopupDelete.setMnemonic('d');
			jMenuItemPopupDelete.setText("Delete...");
			jMenuItemPopupDelete.setToolTipText("Deletes the selected files on the player that this file list is from");
			jMenuItemPopupDelete.setEnabled(false);
			
			jMenuItemPopupDelete.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							fileListActions.deleteFiles();
						}
					});
				}
			});
			
		}

		return jMenuItemPopupDelete;
	}

	private JMenuItem getJMenuPopupRename() {
		if (jMenuItemPopupRename == null) {
			jMenuItemPopupRename = new JMenuItem();
			jMenuItemPopupRename.setName("jMenuItemPopupRename");
			jMenuItemPopupRename.setMnemonic('r');
			jMenuItemPopupRename.setText("Rename...");
			jMenuItemPopupRename.setToolTipText("Renames the selected file on the player");
			jMenuItemPopupRename.setEnabled(false);
			
			jMenuItemPopupRename.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							fileListActions.renameFile();
						}
					});
				}
			});
			
		}

		return jMenuItemPopupRename;
	}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("Right click on the file(s) in the table for more options.");
			jLabel.setToolTipText("Select multiple files by holding down CTRL or SHIFT while clicking");
		}
		return jLabel;
	}

	public BaseMsg[] getNextMsgs() {
		return nextMsg;
	}

	/**
	 * Could be NULL if the user is not interested in sending a new command/message
	 * @param nextMsg
	 */
	protected void setNextMsgs(BaseMsg[] nextMsg) {
		this.nextMsg = nextMsg;
	}

	protected void execute() {
		if( getNextMsgs() != null && getNextMsgs().length > 0 ) {
			for( int i = 0; i < getNextMsgs().length; i++ )
				getParentLMMFrame().sendMessage( getNextMsgs()[i], destination );
		}	
	}
	
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"