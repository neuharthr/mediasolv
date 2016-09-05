package com.lmm.sched.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.lmm.gui.SelectableCellRenderer;
import com.lmm.gui.UIUtils;
import com.lmm.msg.FileMsg;
import com.lmm.sched.data.VideoEntry;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.HeavyClient;
import com.lmm.tools.LMMLogger;


public class PlayListDialog extends JDialog {

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
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JComboBox jComboBox = null;

	private String clientName = null;
	private HeavyClient client = null;
	private String[] themeFiles = new String[0];

	private boolean ok = false;

	private javax.swing.JButton jButton5 = null;
	/**
	 * This is the default constructor
	 */
	public PlayListDialog( Frame owner ) {
		super(owner);
		initialize();
	}
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    protected void initialize() {
        setSize(600, 420);
        setContentPane(getJContentPane());
        setTitle("Edit Play List");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("framePlayList");
		setModal(false);
		//this.setResizable(false);
		setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
		
		//fire this off once this dialog is displaying
		addWindowListener( new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				getJButtonCancel().requestFocusInWindow();
			}
		});

    }
	
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    protected javax.swing.JPanel getJContentPane() {
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

    	TableColumn colTime = getJTablePlayList().getColumnModel().getColumn(
    			PlayerListTableModel.TIME_WINDOW );
    	colTime.setPreferredWidth( 100 );
    	colTime.setWidth( 100 );

    	TableColumn colDay = getJTablePlayList().getColumnModel().getColumn(
    			PlayerListTableModel.DAY_WINDOW );
    	colDay.setPreferredWidth( 100 );
    	colDay.setWidth( 100 );

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
			
			jTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent event)  {

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
			jPanel.add(getJButtonSend(), null);
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
	protected javax.swing.JButton getJButtonSend() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setPreferredSize(new java.awt.Dimension(85,25));
			jButton.setText("Send");
			jButton.setName("Send");
			jButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
			
			jButton.setToolTipText("Send the play list file and save it locally");

			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					String msg = getInvalidMsg();
					if( msg != null ) {							
						JOptionPane.showMessageDialog(PlayListDialog.this,
							msg,
							"Invalid Input", JOptionPane.OK_OPTION);
					}
					else {
						try {
							writeToPropFile( LMMUtils.PLAYER_FILE );
	
							sendFileMsg();					
						}
						catch( IOException ioe ) {
							LMMLogger.error( "Unable to write & send play list file", ioe );
						}
	
						dispose();
					}
				}
			});
		}
		return jButton;
	}
	
	
	protected void sendFileMsg() {
		FileMsg fMsg = new FileMsg();
		fMsg.setFile( new File(LMMUtils.getDataDir() + File.separator + LMMUtils.PLAYER_FILE) );
		fMsg.addHeader( LMMUtils.PROP_FILE, LMMUtils.PLAYER_FILE );
		
		if( getClient() != null )
			getClient().sendMessage( fMsg, getClientName() );
	}
	
	/**
	 * This instance may be NULL, so always check to be safe.
	 * @return
	 */
	protected HeavyClient getClient() {
		return client;
	}

	private void writeToPropFile( String fileName ) throws IOException {
		
		File file = null;
		RandomAccessFile out = null;
		try {
			file = new File( LMMUtils.getDataDir() + File.separator + fileName );

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
	protected javax.swing.JButton getJButtonNew() {
		if(jButton2 == null) {
			jButton2 = new javax.swing.JButton();
			jButton2.setPreferredSize(new java.awt.Dimension(85,25));
			jButton2.setMnemonic(java.awt.event.KeyEvent.VK_N);
			jButton2.setName("New");
			jButton2.setText("New...");
			jButton2.setToolTipText("Add a new play entry to the list below");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    					
					VideoEntryEditor ed = new VideoEntryEditor(PlayListDialog.this);
					ed.setThemeFiles( getThemeFiles() );
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
	protected javax.swing.JButton getJButtonEdit() {
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
					VideoEntryEditor ed = new VideoEntryEditor(PlayListDialog.this);
					ed.setThemeFiles( getThemeFiles() );
					ed.setPlayOrderCriteria(
							new PlayOrderCriteria(
									getTableModel().getMaxPlayOrder(),
								getTableModel().getPlayOrders(selRow)) );
					
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
	 * Returns the input problems, if any, of the current dialog. Returns null
	 * if there is no problems.
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	private String getInvalidMsg() {
		
		String retMsg = null;
		
		final HashSet<Integer>[] pOrders = new HashSet[] {
			new HashSet<Integer>(8),	//channel 0
			new HashSet<Integer>(8),	//channel 1
			new HashSet<Integer>(8),	//channel 2
			new HashSet<Integer>(8)		//channel 3
		};
		
		for( int i = 0; i < getTableModel().getRowCount(); i++ ) {
			VideoEntry ve = getTableModel().getRowAt(i);
			
			for( int j = 0; j < ve.getPlayOrder().length; j++ ) {
				if( pOrders[ve.getChannel()].contains(ve.getPlayOrder()[j]))
					return "Duplicate play order found for the " + UIUtils.CRLF
						+ " theme '" + ve.getFileName()
						+ "', choose a different play order.";

				pOrders[ve.getChannel()].add( ve.getPlayOrder()[j] );
			}

		}
		
		if( getTableModel().getRowCount() <= 0 )
			retMsg = "At least one theme file must be in this play list";
		
		return retMsg;
	}

	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getJButtonDelete() {
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
							PlayListDialog.this,
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
	protected javax.swing.JPanel getJPanelPlayListButtons() {
		if(jPanel1 == null) {
			jPanel1 = new javax.swing.JPanel();
			java.awt.FlowLayout layFlowLayout1 = new java.awt.FlowLayout();
			layFlowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel1.setLayout(layFlowLayout1);
			jPanel1.add(getJButtonNew(), null);
			jPanel1.add(getJButtonEdit(), null);
			jPanel1.add(getJButtonDelete(), null);
			jPanel1.add(getJLabelPlayLists(), null);
			jPanel1.add(getJComboBoxPlayLists(), null);
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
    public String getClientName() {
        return clientName;
    }

    /**
     * @param string
     */
    public void setClientName(String string) {
        clientName = string;
    }

    /**
     * @param client
     */
    public void setClient(HeavyClient client) {
        this.client = client;
    }

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	protected javax.swing.JLabel getJLabelPlayLists() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("Current Play Lists:");
			jLabel.setToolTipText("All Play Lists that have been previously downloaded");
			jLabel.setVisible( false );
		}
		return jLabel;
	}
	
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	protected javax.swing.JComboBox getJComboBoxPlayLists() {
		if(jComboBox == null) {
			jComboBox = new javax.swing.JComboBox();
			jComboBox.setPreferredSize(new java.awt.Dimension(400,25));
			jComboBox.setVisible( false );

			File[] files = new File( LMMUtils.getDataDir() ).listFiles();
			ArrayList fileList = new ArrayList(32);

			for( int i = 0; i < files.length; i++ ) {
				if( files[i].isFile() && files[i].getName().endsWith(LMMUtils.PLAYER_FILE) )
					fileList.add( files[i].getName() );
			}
			
			//alphatetize our list for sanities sake!
			Collections.sort( fileList );

			if( fileList.size() > 0 ) {
				getJComboBoxPlayLists().addItem( null );
				for( int i = 0; i < fileList.size(); i++ )
					getJComboBoxPlayLists().addItem( fileList.get(i) );
			}
			else {
				getJComboBoxPlayLists().addItem(
					" (A play list must first be downloaded from an existing player)" );
				getJComboBoxPlayLists().setToolTipText(
					"No play list files found in '" + LMMUtils.getDataDir() + "'" );
			}
			

			jComboBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {

					getJButtonSend().setEnabled(
						getJComboBoxPlayLists().getItemAt(0) == null &&
						getJComboBoxPlayLists().getSelectedItem() != null );
					
					getTableModel().clear();
					if( getJComboBoxPlayLists().getSelectedItem() instanceof String ) {
						String newFile = (String)getJComboBoxPlayLists().getSelectedItem();
	
						String origPlayerFile = LMMUtils.PLAYER_FILE;
						try {
							LMMUtils.PLAYER_FILE = new File(
									LMMUtils.getDataDir() + File.separator + newFile).getAbsolutePath();

							getJComboBoxPlayLists().setToolTipText( LMMUtils.PLAYER_FILE );

							LMMUtils.reloadLMMProperties();
	
							setVideoEntries( LMMUtils.getVideoEntries() );
						}
						finally {
							//always reset the player files name in the static class
							LMMUtils.PLAYER_FILE = origPlayerFile;
							LMMUtils.reloadLMMProperties();
						}
					}
					else {
						getJComboBoxPlayLists().setToolTipText(null);
					}
						
				}
			});
			
		}
		return jComboBox;
	}
    /**
     * @return
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @param b
     */
    protected void setOk(boolean b) {
        ok = b;
    }

	/**
	 * This method initializes jButton5
	 * 
	 * @return javax.swing.JButton
	 */
	protected javax.swing.JButton getJButtonSave() {
		if(jButton5 == null) {
			jButton5 = new javax.swing.JButton();
			jButton5.setPreferredSize(new java.awt.Dimension(135,25));
			jButton5.setText("Save Without Send");
			jButton5.setMnemonic(java.awt.event.KeyEvent.VK_V);
			jButton5.setName("save");
			jButton5.setToolTipText("Only save the file locally to your computer");

			jButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {

					String msg = getInvalidMsg();
					if( msg != null ) {							
						JOptionPane.showMessageDialog(PlayListDialog.this,
							msg,
							"Invalid Input", JOptionPane.OK_OPTION);
					}
					else {
						
						final String fName = (String)JOptionPane.showInputDialog(
								PlayListDialog.this,
								"Choose a name for your playlist file",
								"Playlist Name",
								JOptionPane.PLAIN_MESSAGE,
								null, null,
								getClientName() );
						
						try {
							if( fName != null && fName.length() > 0 )
								writeToPropFile( fName + "_" + LMMUtils.PLAYER_FILE );
	
						} catch( IOException ioe ) {
							LMMLogger.error( "Unable to write play list file", ioe );
						}
	
						dispose();
					}
				}
			});

		}
		return jButton5;
	}

	public String[] getThemeFiles() {
		return themeFiles;
	}

	public void setThemeFiles(String[] themeFiles) {
		this.themeFiles = themeFiles;
	}

}  //  @jve:visual-info  decl-index=0 visual-constraint="41,28"
