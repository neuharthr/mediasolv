package com.lmm.group.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.lmm.gui.SelectableCellRenderer;
import com.lmm.msg.FileMsg;
import com.lmm.sched.data.VideoEntry;
import com.lmm.sched.gui.PlayerListTableModel;
import com.lmm.sched.gui.VideoEntryEditor;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.HeavyClient;
import com.lmm.tools.LMMLogger;


public class GroupDialog extends JDialog {

    private javax.swing.JPanel jContentPane = null;
	private javax.swing.JTable jTable = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JPanel jPanel = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;
	private javax.swing.JButton jButton2 = null;
	private javax.swing.JButton jButton3 = null;
	private javax.swing.JButton jButton4 = null;
	private javax.swing.JPanel jPanel1 = null;
	private String clientID = null;
	private HeavyClient client = null;

    /**
     * This is the default constructor
     */
    public GroupDialog() {
        super();
        initialize();
    }

	/**
	 * This is the default constructor
	 */
	public GroupDialog( Frame owner ) {
		super(owner);
		initialize();
	}
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(550, 420);
        this.setContentPane(getJContentPane());
        this.setTitle("Edit Play List");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setName("framePlayList");
		this.setModal(false);
		//this.setResizable(false);
		this.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
    }
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            java.awt.GridBagConstraints consGridBagConstraints9 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();
            java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();
            consGridBagConstraints9.insets = new java.awt.Insets(1,5,2,5);
            consGridBagConstraints9.ipady = 263;
            consGridBagConstraints9.ipadx = 0;
            consGridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
            consGridBagConstraints9.weighty = 2.0D;
            consGridBagConstraints9.weightx = 1.0;
            consGridBagConstraints9.gridy = 1;
            consGridBagConstraints9.gridx = 0;
            consGridBagConstraints8.insets = new java.awt.Insets(2,5,6,5);
            consGridBagConstraints8.ipadx = 0;
            consGridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            consGridBagConstraints8.weighty = 1.0;
            consGridBagConstraints8.weightx = 1.0;
            consGridBagConstraints8.gridy = 2;
            consGridBagConstraints8.gridx = 0;
            consGridBagConstraints10.insets = new java.awt.Insets(6,5,1,5);
            consGridBagConstraints10.ipadx = 0;
            consGridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
            consGridBagConstraints10.weighty = 1.0;
            consGridBagConstraints10.weightx = 1.0;
            consGridBagConstraints10.gridy = 0;
            consGridBagConstraints10.gridx = 0;
            jContentPane.setLayout(new java.awt.GridBagLayout());
            jContentPane.add(getJPanelExitButtons(), consGridBagConstraints8);
            jContentPane.add(getJScrollPane(), consGridBagConstraints9);
            jContentPane.add(getJPanelPlayListButtons(), consGridBagConstraints10);
        }
        return jContentPane;
    }
    
    private void initColumns() {
    
    	TableColumn colName = getJTablePlayList().getColumnModel().getColumn(
    			PlayerListTableModel.NAME );
    	colName.setPreferredWidth( 200 );
    	colName.setWidth( 200 );

    	TableColumn colWeb = getJTablePlayList().getColumnModel().getColumn(
    			PlayerListTableModel.WEB_URL );
    	colWeb.setPreferredWidth( 65 );
    	colWeb.setWidth( 65 );

    	TableColumn colChann = getJTablePlayList().getColumnModel().getColumn(
    			PlayerListTableModel.CHANNEL );
    	colChann.setPreferredWidth( 45 );
    	colChann.setWidth( 45 );
    }

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTablePlayList() {
		if(jTable == null) {
			jTable = new javax.swing.JTable();
			jTable.setName("PlayList");
			
			jTable.setIntercellSpacing( new Dimension(0, 0) );
			jTable.setShowGrid( false );
			jTable.setRowHeight( jTable.getFont().getSize() + 8 );

			jTable.setAutoCreateColumnsFromModel( true );
			jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);

			jTable.setDefaultRenderer( Object.class, new SelectableCellRenderer() );
			jTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

			jTable.createDefaultColumnsFromModel();
			jTable.setModel( new PlayerListTableModel() );
			
			jTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent event)  {

					javax.swing.ListSelectionModel lsm =
						(javax.swing.ListSelectionModel) event.getSource();
	
					//set all actions buttons accordingly
					if( event.getSource() == getJTablePlayList().getSelectionModel() ) {
						int selRow = getJTablePlayList().getSelectedRow();
						getJButtonDelete().setEnabled( selRow >= 0 );
						getJButtonEdit().setEnabled( selRow >= 0 );
					}
				}
			});

			jTable.setModel( new PlayerListTableModel() );
			
			initColumns();			
		}

		return jTable;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTablePlayList());
			jScrollPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.SoftBevelBorder.LOWERED));
			jScrollPane.setPreferredSize(new java.awt.Dimension(275,35));
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelExitButtons() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			jPanel.add(getJButtonSave(), null);
			jPanel.add(getJButtonCancel(), null);
			jPanel.setName("NavPanel");
			jPanel.setPreferredSize(new java.awt.Dimension(275,35));
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonSave() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setPreferredSize(new java.awt.Dimension(85,25));
			jButton.setText("Save");
			jButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
			jButton.setName("Save");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					try {
						writeToPropFile();
						
						sendFileMsg();					
					}
					catch( IOException ioe ) {
						LMMLogger.error( "Unable to write & send play list file", ioe );
					}

					dispose();
				}
			});
		}
		return jButton;
	}
	
	private void sendFileMsg() {
		FileMsg fMsg = new FileMsg();
		fMsg.setFile( new File(LMMUtils.getDataDir() + File.separator + LMMUtils.PLAYER_FILE) );
		fMsg.addHeader( LMMUtils.PROP_FILE, LMMUtils.PLAYER_FILE );
		client.sendMessage( fMsg, getClientID() );
	}

	private void writeToPropFile( ) throws IOException {
		
		File file = null;
		RandomAccessFile out = null;
		try {
			file = new File( LMMUtils.getDataDir() + File.separator + LMMUtils.PLAYER_FILE );

			if( file.exists() )
				file.delete();
			
			file.createNewFile();
			out = new RandomAccessFile( file, "rw" );
			
			for( int i = 0; i < getTableModel().getRowCount(); i++ ) {
				VideoEntry ve = getTableModel().getRowAt(i);
				ve.setEntryID( i + "" );
				out.writeBytes( ve.getStringEntry() );
			}
		
			LMMLogger.info( "Wrote play list file at: " + file.getAbsolutePath() );
		}
		finally {
			try {
				if( out != null ) out.close();
			}
			catch( IOException ioe ) {}
		}
	}
	
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonCancel() {
		if(jButton1 == null) {
			jButton1 = new javax.swing.JButton();
			jButton1.setText("Cancel");
			jButton1.setMnemonic(java.awt.event.KeyEvent.VK_C);
			jButton1.setName("Cancel");
			jButton1.setPreferredSize(new java.awt.Dimension(85,25));
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonNew() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setPreferredSize(new java.awt.Dimension(85,25));
			jButton2.setMnemonic(java.awt.event.KeyEvent.VK_N);
			jButton2.setName("New");
			jButton2.setText("New...");
			jButton2.setToolTipText("Add a new play entry to the list below");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    					
					VideoEntryEditor ed = new VideoEntryEditor(GroupDialog.this);					
					ed.show();
					
					if( ed.getResponse() == JOptionPane.OK_OPTION ) {
						getTableModel().addRow( ed.getVideoEntry() );
					}
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonEdit() {
		if(jButton3 == null) {
			jButton3 = new javax.swing.JButton();
			jButton3.setPreferredSize(new java.awt.Dimension(85,25));
			jButton3.setText("Edit...");
			jButton3.setEnabled(false);
			jButton3.setMnemonic(java.awt.event.KeyEvent.VK_E);
			jButton3.setToolTipText("Modify an existing play entry from the list below");
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					if( getJTablePlayList().getSelectedRow() < 0 )
						return;

					int selRow = getJTablePlayList().getSelectedRow();
					VideoEntryEditor ed = new VideoEntryEditor(GroupDialog.this);
					ed.setVideoEntry(
						getTableModel().getRowAt(selRow) );

					ed.show();
					
					if( ed.getResponse() == JOptionPane.OK_OPTION ) {
						getTableModel().updateRow( selRow, ed.getVideoEntry() );
					}
				}
			});
		}
		return jButton3;
	}
	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonDelete() {
		if(jButton4 == null) {
			jButton4 = new javax.swing.JButton();
			jButton4.setPreferredSize(new java.awt.Dimension(85,25));
			jButton4.setMnemonic(java.awt.event.KeyEvent.VK_D);
			jButton4.setName("Delete");
			jButton4.setText("Delete...");
			jButton4.setEnabled(false);
			jButton4.setToolTipText("Remove an existing play entry from the list below");
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] sel = getJTablePlayList().getSelectedRows();
					if( sel.length > 0 ) {
						int resp = JOptionPane.showConfirmDialog(
							GroupDialog.this,
							"Are you sure you want to remove the " +
							"selected play entrie(s)?",
							"Confirm Removal", JOptionPane.YES_NO_OPTION );
						
						if( resp == JOptionPane.OK_OPTION ) {
							for( int i = sel.length-1; i >= 0; i-- )
								getTableModel().removeRow( sel[i] );
						}

					}

				}
			});
		}
		return jButton4;
	}
	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelPlayListButtons() {
		if(jPanel1 == null) {
			jPanel1 = new javax.swing.JPanel();
			java.awt.FlowLayout layFlowLayout1 = new java.awt.FlowLayout();
			layFlowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel1.setLayout(layFlowLayout1);
			jPanel1.add(getJButtonNew(), null);
			jPanel1.add(getJButtonEdit(), null);
			jPanel1.add(getJButtonDelete(), null);
			jPanel1.setName("OperationPanel");
		}
		return jPanel1;
	}
    /**
     * @return
     */
    public VideoEntry[] getVideoEntries() {
		VideoEntry[] retEntries = new VideoEntry[ getTableModel().getRowCount() ];
		for( int i = 0; i < retEntries.length; i++ )
			retEntries[i] = getTableModel().getRowAt(i);

        return retEntries;
    }

    /**
     * @param entry
     */
    public void setVideoEntries(VideoEntry[] entries) {
        for( int i = 0; i < entries.length; i++ )
        	getTableModel().addRow( entries[i] );
    }

	public void show() {
		//always show this dialog in the middle of the parent
		setLocation(
			getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2, 
			getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2 );

		super.show();
	}

    /**
     * @return
     */
    public PlayerListTableModel getTableModel() {
        return (PlayerListTableModel)getJTablePlayList().getModel();
    }

    /**
     * @return
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @param string
     */
    public void setClientID(String string) {
        clientID = string;
    }

    /**
     * @param client
     */
    public void setClient(HeavyClient client) {
        this.client = client;
    }

}  //  @jve:visual-info  decl-index=0 visual-constraint="41,28"
