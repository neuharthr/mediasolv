package com.lmm.sched.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.gui.ISBFBForm;

import messageit.message.LocalMsg;
import messageit.message.Message;

import com.lmm.client.FTPHandler;
import com.lmm.db.DBCommon;
import com.lmm.gui.OkCancelDialog;
import com.lmm.gui.PopupMenuShower;
import com.lmm.gui.SelectableCellRenderer;
import com.lmm.gui.UIMutex;
import com.lmm.gui.UIUtils;
import com.lmm.gui.files.FileListDialog;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.FileListMsg;
import com.lmm.msg.FileMsg;
import com.lmm.msg.GlobalConfigMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.msg.MsgUtils;
import com.lmm.msg.PropertiesMsg;
import com.lmm.sched.jobs.AutoUpdateJob;
import com.lmm.sched.proc.LMMGlobalConfig;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.HeavyClient;
import com.lmm.server.LMMServer;
import com.lmm.tools.LMMFlags;
import com.lmm.tools.LMMLogger;

/**
 * @author Owner
 * 
 */
public class LMMJFrame extends JFrame implements LMMMsgListener {

	private static final long serialVersionUID = 1L;

	public static final String MASTER_GUI_ID = "[Dashboard]";

	public static final String BEAN_GUI_CONFIG = "bean_editor.xml";

	private DashBoardActions dashActions = null;
	protected UIMutex clientLock = null;
	private JPopupMenu jPopupMenu = null;
	private javax.swing.JPanel jContentPane = null;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTable jTable = null;

	protected HeavyClient client = null;
	private javax.swing.JMenuBar jJMenuBar = null;
	private javax.swing.JMenu jMenuDashboard = null;
	private javax.swing.JMenu jMenu1 = null;
	private javax.swing.JMenu jMenu2 = null;
	private javax.swing.JMenu jMenu3 = null;
	private javax.swing.JMenu jMenu4 = null;
	private javax.swing.JMenuItem jMenuItem = null;
	private javax.swing.JMenuItem jMenuItem1 = null;
	private javax.swing.JMenuItem jMenuItem2 = null;
	private javax.swing.JMenuItem jMenuItem3 = null;
	private javax.swing.JMenuItem jMenuItem4 = null;
	private javax.swing.JMenuItem jMenuItem5 = null;
	private javax.swing.JMenuItem jMenuItem6 = null;
	private javax.swing.JMenuItem jMenuItem7 = null;
	private javax.swing.JMenuItem jMenuItem8 = null;
	private javax.swing.JMenuItem jMenuItem9 = null;
	private javax.swing.JMenuItem jMenuItem10 = null;
	private javax.swing.JMenuItem jMenuItem11 = null;
	private javax.swing.JMenuItem jMenuItem12 = null;
	private javax.swing.JMenuItem jMenuItem13 = null;
	private javax.swing.JMenuItem jMenuItem14 = null;
	private javax.swing.JMenuItem jMenuItem15 = null;
	private javax.swing.JMenuItem jMenuItem16 = null;
	private javax.swing.JMenuItem jMenuItem17 = null;	
	private javax.swing.JMenuItem jMenuItem18 = null;
	private javax.swing.JMenuItem jMenuItem19 = null;
	private javax.swing.JMenuItem jMenuItem20 = null;
	private javax.swing.JMenuItem jMenuItem21 = null;
	private javax.swing.JMenuItem jMenuItem22 = null;
	
	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JScrollPane jScrollPane1 = null;
	private com.lmm.sched.gui.TextOutputPanel textOutputPanel = null;
	private JSplitPane jSplitPane = null;

	/**
	 * This is the default constructor
	 */
	public LMMJFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(620, 445);
		this.setContentPane(getJContentPane());
		setTitle("MediaSOLV Dashboard  (Not Connected)");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(UIUtils.LMM_LOGO_GIF));

		//init our specialized action handler 
		dashActions = new DashBoardActions(this);

		// we need to know when something in the JTable is selected
		getJTable().getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged( ListSelectionEvent event) {
						// set all actions buttons accordingly
						if (event.getSource() == getJTable().getSelectionModel()) {
							setMenuItemsEnabled();
						}
					}
				});

		// we need to know when something in the JTable is selected
		getJTable().getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getJLabelCount().setText(
								"" + getTableDataModel().getRowCount());
						repaint();
					}
				});

			}
		});

		// forces our table events to fire
		getTableDataModel().fireTableStructureChanged();		

		initTableColumns();
		
		//add the mouse listener for selecting a row when the right-click is done.
		getJTable().addMouseListener( new MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger() && getJTable().getSelectedRows().length <= 1) {
					int row = getJTable().rowAtPoint( e.getPoint() );
					//int column = getJTable().columnAtPoint( e.getPoint() );
					//getJTable().changeSelection(row, column, false, false);
					
					getJTable().setRowSelectionInterval(row, row);
				}
			}
		});
		
    	MouseListener listener = new PopupMenuShower( getJPopupMenu() );
    	getJTable().addMouseListener( listener );
	}
	
	private void initTableColumns() {
		//make the status column small
    	TableColumn colStatus = getJTable().getColumnModel().getColumn(ClientTableModel.STATE);
    	colStatus.setPreferredWidth( 35 ); colStatus.setWidth( 35 ); colStatus.setMaxWidth( 35 );

    	TableColumn colName = getJTable().getColumnModel().getColumn(ClientTableModel.NAME);
    	colName.setPreferredWidth( 180 ); colName.setWidth( 180 );
    	
    	TableColumn colTime = getJTable().getColumnModel().getColumn(ClientTableModel.TIME);
    	colTime.setPreferredWidth( 90 ); colTime.setWidth( 90 );
    	
    	TableColumn colPlay = getJTable().getColumnModel().getColumn(ClientTableModel.CURRENT_PLAY);
    	colPlay.setPreferredWidth( 240 ); colPlay.setWidth( 240 );
	}

	private void setMenuItemsEnabled() {
		int[] selRows = getJTable().getSelectedRows();

		//one or more players are selected
		getJMenuItemRemove().setEnabled(selRows.length >= 1);
		getJMenuItemPing().setEnabled(selRows.length >= 1);
		getJMenuItemPopupPing().setEnabled(selRows.length >= 1);

		//one connected player is selected
		getJMenuItemPlayList().setEnabled(isValidSingleSelect());
		getJMenuItemPopupPlayList().setEnabled(isValidSingleSelect());
		getJMenuItemProperties().setEnabled(isValidSingleSelect());
		getJMenuItemPopupProperties().setEnabled(isValidSingleSelect());
		getJMenuItemConfigure().setEnabled(isValidSingleSelect());

		//one or more connected players are selected
		getJMenuItemGetFileList().setEnabled(isValidMultiSelect());
		getJMenuItemPopupGetFileList().setEnabled(isValidMultiSelect());
		getJMenuItemSendFile().setEnabled(isValidMultiSelect());
		getJMenuItemPopupSendFile().setEnabled(isValidMultiSelect());
		getJMenuItemRestartPlayer().setEnabled(isValidMultiSelect());
		getJMenuItemRestartPC().setEnabled(isValidMultiSelect());
		getJMenuItemFileRequest().setEnabled(isValidMultiSelect());
		getJMenuItemSendPlaylist().setEnabled(isValidMultiSelect());
		getJMenuItemSynchronize().setEnabled(isValidMultiSelect());

	}

	/**
	 * Attempts to start our client connection. This is done in a seperate thread so the UI
	 * can repaint itself immediatley.
	 */
	private synchronized void startClient() {

		new Thread( new Runnable() {
			public void run() {
				// make the client a unique name all the time
				client = new HeavyClient(MASTER_GUI_ID + "_"
						+ LMMUtils.getComputerName() + ":" + new Date().getTime(),
						LMMJFrame.this);

				client.connect();
			}
		}, "ClientBGStart").start();

	}

	void restartClientIfChanged() {
		//do not bother with any connectivity if we are not set up for access
		if( LMMUtils.getServerHost() == null )
			return;

		//try to stop, then start the client if our connection properties have changed
		if( client != null &&
			( !LMMUtils.getServerHost().equals(client.getHost())
				|| LMMUtils.getServerPort() != client.getPort()) ) {
			
			try {
				client.disconnect();
				client = null;
			}
			catch( IOException ioe ) {
				LMMLogger.error("Unable to stop the client connection", ioe);
			}
			
			startClient();
		}

	}
	private boolean isValidSingleSelect() {
		return getJTable().getSelectedRows().length == 1
				&& isValidMultiSelect();
	}

	private boolean isValidMultiSelect() {
		int[] selRows = getJTable().getSelectedRows();
		boolean valid = false;

		for (int rowid : selRows) {
			valid = getTableDataModel().getRowAt(rowid).getStatus() == MsgUtils.Statuses.Healthy;
			if (!valid)
				break;
		}

		return valid;
	}

	public void messageReceived(Message m) {

		try {
			LMMLogger.debug(" FRAME: " + m.getSender() + " message received: "
					+ m.getContent());

			if (m.getContent() instanceof ClientStateMsg) {
				handleClientStateMsg(
						new ClientStateMsg[] { (ClientStateMsg) m.getContent() });
			}
			else if (m.getContent() instanceof ClientStateMsg[]) {
				handleClientStateMsg((ClientStateMsg[]) m.getContent());
			}
			else if (m.getContent() instanceof FileMsg) {
				handlePropertyFile((FileMsg) m.getContent(), m.getSender());
			}
			else if (m.getContent() instanceof String) {
				handleMessage(m);
			}
			else if (m.getContent() instanceof FileListMsg) {
				handleFileList((FileListMsg) m.getContent(), m.getSender());
			}
			else if (m.getContent() instanceof PropertiesMsg) {
				handleProperties((PropertiesMsg) m.getContent(), m.getSender());
			}
			else if (m.getContent() instanceof GlobalConfigMsg) {
				handleGlobalConfigMsg((GlobalConfigMsg) m.getContent(), m.getSender());
			}
			else if (m.getContent() instanceof CmdMsg ) {
				handleCmdMsg((CmdMsg) m.getContent(), m.getSender());
			}
			
		} catch (FileNotFoundException fnf) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(getJTable(),
							"The requested resource was not found or the maximum size limit of "
									+ (FileMsg.MAX_FILE_SIZE / 1000000)
									+ " MB has been exceeded, try again",
							"Resource request rrror", JOptionPane.OK_OPTION);
				}
			});

		} catch (Exception ex) {
			LMMLogger.info("Unable to handle message, " + ex.getMessage());
		}

	}
	
	private void handleCmdMsg( final CmdMsg cmd, final String sender ) {

		if( cmd.getCmd() == CmdMsg.Commands.FTP_GET_FILE ) {

			getTextOutputPanel().addOutput(
				"Attempting to download file '"
				+ cmd.getHeader(CmdMsg.HDR_FILE_NAME).toString()
				+ "' from " + sender + "...",
				Message.STATUS_FROM_SELF);

			FTPHandler ftpHand = new FTPHandler(
					cmd.getHeader(CmdMsg.HDR_FILE_NAME).toString(),
					client,
					sender);

			String fldrName = (cmd.getHeader(CmdMsg.HDR_FOLDER_NAME) == null
					? LMMUtils.getVideoDir() : cmd.getHeader(CmdMsg.HDR_FOLDER_NAME).toString());

			ftpHand.executeGetFile(
				this,
				cmd,
				fldrName);
		}		

	}

	private void handleProperties(final PropertiesMsg propMsg,
			final String sender) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(LMMJFrame.this, sender
						+ UIUtils.CRLF
						+ UIUtils.CRLF
						+ "Player Version: "
						+ propMsg.getVersion()
						+ (LMMUtils.VERSION.equalsIgnoreCase(propMsg
								.getVersion()) ? "" : "  (MISMATCH)")
						+ UIUtils.CRLF
						+ "IP Addresses: "
						+ propMsg.getIpAddress()
						+ UIUtils.CRLF
						+ "Up time: "
						+ propMsg.getTotalRuntime()
						+ UIUtils.CRLF + UIUtils.CRLF + "UUID: "
						+ propMsg.getUuid(), "Properties",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	private void handleGlobalConfigMsg(final GlobalConfigMsg gMsg,
			final String sender) {

		if (!gMsg.isBroadcastMsg()) {
			// if this message is not from a global message, lets handle it
			final OkCancelDialog diag = new OkCancelDialog(LMMJFrame.this,
					"Settings: " + sender, false);

			SBFBConfiguration.loadConfiguration(BEAN_GUI_CONFIG);
			final ISBFBForm form = FormBuilder.buildForm(LMMGlobalConfig.class,
					diag);

			// Load data in the SBFBForm...
			final LMMGlobalConfig lConfig = gMsg.getGlobalConfig();
			form.loadData(lConfig);

			diag.setOkButtonText("Save");
			diag.setDisplayPanel((JPanel) form);
			diag.pack();
			diag.setVisible(true);

			diag.addOkButtonListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							int resp = JOptionPane.showConfirmDialog(
									LMMJFrame.this,
									"Are you sure you want to save the following settings "
											+ "to the player: " + sender,
									"Confirm Save", JOptionPane.YES_NO_OPTION);

							if (resp == JOptionPane.OK_OPTION) {
								form.saveData(lConfig);
								sendMessage(new GlobalConfigMsg(lConfig),
										sender);
							}
						}
					});
				}
			});

		}

	}

	private void handleFileList(FileListMsg fListMsg, final String sender) {

		Vector<Vector> data = new Vector<Vector>(64);
		for (int i = 0; i < fListMsg.getNames().length; i++) {
			Vector row = new Vector();
			row.add(""); // spacer
			row.add(fListMsg.getNames()[i]);	//unique ID
			row.add(new Long(fListMsg.getLengths()[i]));
			row.add(new Date(fListMsg.getModified()[i]));

			row.add(fListMsg.getFolders()[i]);	//invisible data			
			row.add(fListMsg.getGrouping()[i]);	//invisible data
			
			data.add(row);
		}

		final FileListDialog diag = new FileListDialog(this, sender);
		diag.setTitle("Player file listing for: " + sender);
		diag.setResizable(true);
		diag.setTableModel(
			new String[] { "", "Name", "Size", "Last Modified" },
			new Class[] { String.class, String.class, Long.class, Date.class },
			data );

		diag.setVisible(true);
	}

	public void messageLocalReceived(LocalMsg lm) {

		//potential conn State Change
		if (lm.isConnected()) {
			setTitle("MediaSOLV Dashboard  (Connected)");
		} else {
			getTextOutputPanel().addOutput("Connection to server went down",
					TextOutputPanel.STATUS_ERROR);

			setTitle("MediaSOLV Dashboard  (Not Connected)");
		}
		
		if( lm.isVersionMismatch() ) {
			//force an auto update
			if( new AutoUpdateJob().doUpdate() > 0 ) {
				LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
				UIUtils.showUpdateUI();
			}			
		}

	}

	private void handleMessage(Message m) throws IOException,
			ClassNotFoundException {

		String msg = (String) m.getContent();
		String sender = m.getSender();

		getTextOutputPanel().addOutput(
				(sender == null ? "" : "(" + sender + "): ") + msg,
				m.getStatus());

		// instantiate a new ClientMutex for this msg
		clientLock = new UIMutex(sender, msg, m.getStatus());
	}

	private void showPlayListDialog(File file, String clientName,
			String[] themeFiles) {

		String origPlayerFile = LMMUtils.PLAYER_FILE;
		try {
			LMMUtils.PLAYER_FILE = file.getAbsolutePath();
			LMMUtils.reloadLMMProperties();

			PlayListDialog pDialog = new PlayListDialog(this);
			pDialog.setClient(client);
			pDialog.setClientName(clientName);
			pDialog.setThemeFiles(themeFiles);
			pDialog.setTitle(clientName + " Play Entries");

			pDialog.setVideoEntries(LMMUtils.getVideoEntries());

			pDialog.setVisible(true);
		} finally {
			// always reset the player files name in the static class
			LMMUtils.PLAYER_FILE = origPlayerFile;
			LMMUtils.reloadLMMProperties();
		}

	}

	private void handleClientStateMsg(ClientStateMsg[] cMsg) {
		for (int i = 0; i < cMsg.length; i++)
			getTableDataModel().updateClient(cMsg[i]);
	}

	private void handlePropertyFile(FileMsg fMsg, String sender)
			throws FileNotFoundException {

		if (fMsg.getContent().length <= 0)
			throw new FileNotFoundException(
					"The configuration requested was not found");

		// fMsg.getHeader(LMMUtils.PROP_FILE);
		File file = null;
		RandomAccessFile out = null;
		try {
			// file name format: <clientname>_lmm.properties
			file = new File(LMMUtils.getDataDir() + File.separator + sender
					+ "_" + fMsg.getFile().getName());
			if (file.exists())
				file.delete();

			file.createNewFile();
			out = new RandomAccessFile(file, "rw");
			out.write(fMsg.getContent());

			LMMLogger.info("wrote property file at: " + file.getAbsolutePath());

			// handle player.properties if need be
			if (LMMUtils.PLAYER_FILE.equals(fMsg.getFile().getName()))
				showPlayListDialog(
						file, sender, 
						(String[])fMsg.getHeader(FileMsg.HEADER_THEME_LIST));

		} catch (IOException ioe) {
			LMMLogger.error("Unable to write new temp file", ioe);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ioe) {
			}
		}

	}

	public void exceptionRaised(Exception ex) {
	}

	private JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.setName("jPopupMenu");			
			
			jPopupMenu.add( getJMenuItemPopupPing() );
			jPopupMenu.add( getJMenuItemPopupPlayList() );
			jPopupMenu.add( getJMenuItemPopupGetFileList() );
			jPopupMenu.add( getJMenuItemPopupSendFile() );
			jPopupMenu.add( getJMenuItemPopupProperties() );
			
	    	jPopupMenu.addPopupMenuListener( new PopupMenuListener() {
	    	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	    	    	setMenuItemsEnabled();
	    	    }

	    	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
	    	    public void popupMenuCanceled(PopupMenuEvent e) {}
			});

		}

		return jPopupMenu;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints41 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints42 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints43 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints44 = new java.awt.GridBagConstraints();
			consGridBagConstraints43.insets = new java.awt.Insets(2, 6, 0, 543);
			consGridBagConstraints43.ipadx = -1;
			consGridBagConstraints43.gridy = 1;
			consGridBagConstraints43.gridx = 1;
			consGridBagConstraints43.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints43.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints44.insets = new java.awt.Insets(1, 5, 4, 6);
			consGridBagConstraints44.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints44.weighty = 1.0;
			consGridBagConstraints44.weightx = 1.0;
			consGridBagConstraints44.gridwidth = 2;
			consGridBagConstraints44.gridy = 2;
			consGridBagConstraints44.gridx = 0;
			consGridBagConstraints41.insets = new java.awt.Insets(1, 1, 1, 0);
			consGridBagConstraints41.ipadx = -1;
			consGridBagConstraints41.gridwidth = 2;
			consGridBagConstraints41.gridy = 0;
			consGridBagConstraints41.gridx = 0;
			consGridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints42.insets = new java.awt.Insets(2, 5, 0, 5);
			consGridBagConstraints42.gridy = 1;
			consGridBagConstraints42.gridx = 0;
			consGridBagConstraints42.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jContentPane.setLayout(new java.awt.GridBagLayout());
			jContentPane.add(getJJMenuBar(), consGridBagConstraints41);
			jContentPane.add(getJLabelTotal(), consGridBagConstraints42);
			jContentPane.add(getJLabelCount(), consGridBagConstraints43);
			jContentPane.add(getJSplitPane(), consGridBagConstraints44);

			getJMenuDashboard().add(getJMenuItemSetup());
			getJMenuDashboard().add(getJMenuItemSynchronizeAll());
			getJMenuDashboard().add(getJMenuItemRefreshPlayers());
			getJMenuDashboard().add(new JSeparator());
			getJMenuDashboard().add(getJMenuItemExit());

			getJMenuPlayer().add(getJMenuItemPing());
			getJMenuPlayer().add(getJMenuItemPlayList());
			getJMenuPlayer().add(getJMenuItemGetFileList());
			getJMenuPlayer().add(getJMenuItemSendFile());

			getJMenuPlayer().add(new JSeparator());
			getJMenuPlayer().add(getJMenuAdvanced());
			getJMenuPlayer().add(getJMenuItemRemove());
			getJMenuPlayer().add(getJMenuItemProperties());

			getJMenuHelp().add(getJMenuItemCheatSheet());
			getJMenuHelp().add(getJMenuItemAbout());

			getJMenuAdvanced().add(getJMenuItemSendPlaylist());
			getJMenuAdvanced().add(getJMenuItemRestartPlayer());
			getJMenuAdvanced().add(getJMenuItemRestartPC());

			getJMenuAdvanced().add(new JSeparator());
			getJMenuAdvanced().add(getJMenuItemConfigure());
			getJMenuAdvanced().add(getJMenuItemFileRequest());
			getJMenuAdvanced().add(getJMenuItemSynchronize());
		}

		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(
					java.awt.Color.gray, 2));
			jScrollPane.setPreferredSize(new java.awt.Dimension(602, 600));
		}
		return jScrollPane;
	}

	public static void main(String[] args) {

		try {
			LMMLogger.info("Starting MediaSOLV Dashboard v. " + LMMUtils.VERSION);

			System.setProperty(DBCommon.DB_FILENAME_KEY, "db_dashboard.yap");
			ToolTipManager.sharedInstance().setDismissDelay(6500);
			try {
				UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			}catch( Exception ex ) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}

			if (new AutoUpdateJob().doUpdate() <= 0) {
				LMMJFrame f = new LMMJFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// set the location of the frame to the center of the screen
				f.setLocation(
					(Toolkit.getDefaultToolkit().getScreenSize().width - f.getSize().width) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - f.getSize().height) / 2);

				f.setVisible(true);
				f.repaint();
				f.startClient();
			} else {
				LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
				UIUtils.showUpdateUI();
			}

		} catch (Exception ex) {
			LMMLogger.error("Caugth exception in main", ex);
		}
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	protected javax.swing.JTable getJTable() {
		if (jTable == null) {
			jTable = new javax.swing.JTable();
			jTable.setName("PlayerTable");

			jTable.setIntercellSpacing(new Dimension(0, 0));
			jTable.setShowGrid(false);
			jTable.setRowHeight(jTable.getFont().getSize() + 8);

			jTable.setAutoCreateColumnsFromModel(true);
			jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);

			jTable.setDefaultRenderer(Object.class, new SelectableCellRenderer());
			jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			jTable.setModel(new ClientTableModel());
			jTable.createDefaultColumnsFromModel();
		}
		
		return jTable;
	}

	protected ClientTableModel getTableDataModel() {
		return (ClientTableModel) getJTable().getModel();
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private javax.swing.JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new javax.swing.JMenuBar();
			jJMenuBar.add(getJMenuDashboard());

			if (!LMMFlags.isDashViewOnly(LMMUtils.getAppFlags())) {
				jJMenuBar.add(getJMenuPlayer());
				jJMenuBar.add(getJMenuTools());
			}

			jJMenuBar.add(getJMenuHelp());
			jJMenuBar.setPreferredSize(new java.awt.Dimension(612, 22));
			jJMenuBar.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jJMenuBar.setMinimumSize(new java.awt.Dimension(612, 22));
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getJMenuDashboard() {
		if (jMenuDashboard == null) {
			jMenuDashboard = new javax.swing.JMenu();
			jMenuDashboard.setName("Dashboard");
			jMenuDashboard.setMnemonic(java.awt.event.KeyEvent.VK_D);
			jMenuDashboard.setText("Dashboard");
		}
		return jMenuDashboard;
	}

	/**
	 * This method initializes jMenu1
	 * 
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getJMenuPlayer() {
		if (jMenu1 == null) {
			jMenu1 = new javax.swing.JMenu();
			jMenu1.setMnemonic(java.awt.event.KeyEvent.VK_P);
			jMenu1.setName("Player");
			jMenu1.setText("Player");

			// be sure to update our menu items availability
			jMenu1.addMenuListener(new MenuListener() {
				public void menuSelected(MenuEvent e) {
					setMenuItemsEnabled();
				}

				public void menuDeselected(MenuEvent e) {}
				public void menuCanceled(MenuEvent e) {}
			});

		}
		return jMenu1;
	}

	private javax.swing.JMenu getJMenuTools() {
		if (jMenu4 == null) {
			jMenu4 = new javax.swing.JMenu();
			jMenu4.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenu4.setName("Tools");
			jMenu4.setText("Tools");

			// for now, this menu has NO menuItems
			getJMenuTools().setVisible(false);
		}
		return jMenu4;
	}

	/**
	 * This method initializes jMenu2
	 * 
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getJMenuHelp() {
		if (jMenu2 == null) {
			jMenu2 = new javax.swing.JMenu();
			jMenu2.setName("Help");
			jMenu2.setMnemonic(java.awt.event.KeyEvent.VK_H);
			jMenu2.setText("Help");
		}
		return jMenu2;
	}

	private javax.swing.JMenu getJMenuAdvanced() {
		if (jMenu3 == null) {
			jMenu3 = new javax.swing.JMenu();
			jMenu3.setName("Advanced");
			jMenu3.setMnemonic(java.awt.event.KeyEvent.VK_V);
			jMenu3.setText("Advanced");
		}
		return jMenu3;
	}

	/**
	 * Seperate thread to send the message in
	 */
	public void sendMessage(final Serializable msg, final String receiver) {
		// send the message from a seperate thread
		new Thread(new Runnable() {
			public void run() {
				client.sendMessage(msg, receiver);
			}
		}).start();

	}

	/**
	 * Seperate thread to send the message in
	 */
	protected void sendMessage(final Serializable msg, final String receiver, final String topic) {

		// send the message from a seperate thread
		new Thread(new Runnable() {
			public void run() {
				client.sendMessage(msg, receiver, topic);
			}
		}).start();

	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemExit() {
		if (jMenuItem == null) {
			jMenuItem = new javax.swing.JMenuItem();
			jMenuItem.setBounds(68, 57, 74, 10);
			jMenuItem.setText("Exit");
			jMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
			jMenuItem.setName("Exit");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return jMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRemove() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new javax.swing.JMenuItem();
			jMenuItem3.setBounds(68, 57, 74, 10);
			jMenuItem3.setText("Remove...");
			jMenuItem3.setMnemonic(java.awt.event.KeyEvent.VK_R);
			jMenuItem3.setName("Remove");
			jMenuItem3.setEnabled(false);
			jMenuItem3.setToolTipText("Removes the player from the table below "
				+ "(it will reappear upon successful communication)");

			jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.removeItem();
						}
					});
				}
			});
		}
		return jMenuItem3;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRestartPlayer() {
		if (jMenuItem5 == null) {
			jMenuItem5 = new javax.swing.JMenuItem();
			jMenuItem5.setBounds(68, 57, 74, 10);
			jMenuItem5.setText("Restart...");
			jMenuItem5.setMnemonic(java.awt.event.KeyEvent.VK_E);
			jMenuItem5.setName("PlayerRestart");
			jMenuItem5.setEnabled(false);
			jMenuItem5.setToolTipText("Restarts the selected player & forces the player to check for application updates");

			jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.restartPlayer();
						}
					});
				}
			});
		}

		return jMenuItem5;
	}

	private javax.swing.JMenuItem getJMenuItemRestartPC() {
		if (jMenuItem11 == null) {
			jMenuItem11 = new javax.swing.JMenuItem();
			jMenuItem11.setBounds(68, 57, 74, 10);
			jMenuItem11.setText("Reboot...");
			jMenuItem11.setMnemonic(java.awt.event.KeyEvent.VK_B);
			jMenuItem11.setName("PlayerReboot");
			jMenuItem11.setEnabled(false);
			jMenuItem11.setToolTipText("Reboots the remote player (this operation will take 30 seconds or more on the remote system)");

			jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.restartPC();
						}
					});
				}
			});
		}

		return jMenuItem11;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPing() {
		if (jMenuItem6 == null || jMenuItem18 == null) {
			jMenuItem6 = new javax.swing.JMenuItem();
			jMenuItem18 = new javax.swing.JMenuItem();
			
			jMenuItem6.setBounds(68, 57, 74, 10); jMenuItem18.setBounds(68, 57, 74, 10);
			
			jMenuItem6.setText("Ping"); jMenuItem18.setText("Ping");

			jMenuItem6.setEnabled(false); jMenuItem18.setEnabled(false);

			jMenuItem6.setToolTipText("Requests a response from the selected player, used to see if the player is connected");
			jMenuItem18.setToolTipText("Requests a response from the selected player, used to see if the player is connected");

			
			jMenuItem6.setMnemonic(java.awt.event.KeyEvent.VK_G);
			jMenuItem18.setMnemonic(java.awt.event.KeyEvent.VK_G);

			jMenuItem6.setName("Ping"); jMenuItem18.setName("Ping");


			jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.ping();
						}
					});
				}
			});
			jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.ping();
						}
					});
				}
			});

		}

		return jMenuItem6;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPopupPing() {
		if( jMenuItem18 == null )
			getJMenuItemPing();
		return jMenuItem18;
	}
	
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPopupPlayList() {
		if( jMenuItem19 == null )
			getJMenuItemPlayList();
		return jMenuItem19;
	}
	
	private javax.swing.JMenuItem getJMenuItemPopupGetFileList() {
		if( jMenuItem20 == null )
			getJMenuItemGetFileList();
		return jMenuItem20;
	}
	
	private javax.swing.JMenuItem getJMenuItemPopupSendFile() {
		if( jMenuItem21 == null )
			getJMenuItemSendFile();
		return jMenuItem21;
	}

	private javax.swing.JMenuItem getJMenuItemPopupProperties() {
		if( jMenuItem22 == null )
			getJMenuItemProperties();
		return jMenuItem22;
	}

	private javax.swing.JMenuItem getJMenuItemSynchronizeAll() {
		if (jMenuItem12 == null) {
			jMenuItem12 = new javax.swing.JMenuItem();
			jMenuItem12.setBounds(68, 57, 74, 10);
			jMenuItem12.setText("Synch ALL Players...");
			jMenuItem12.setMnemonic(java.awt.event.KeyEvent.VK_Z);
			jMenuItem12.setName("SynchronizeAll");
			jMenuItem12.setToolTipText("Send this dashboards settings to ALL players in the system");

			jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.synchAll();
						}
					});
				}
			});
		}

		return jMenuItem12;
	}

	private javax.swing.JMenuItem getJMenuItemSynchronize() {
		if (jMenuItem14 == null) {
			jMenuItem14 = new javax.swing.JMenuItem();
			jMenuItem14.setBounds(68, 57, 74, 10);
			jMenuItem14.setText("Synch Settings...");
			jMenuItem14.setMnemonic(java.awt.event.KeyEvent.VK_Y);
			jMenuItem14.setName("SendSettings");
			jMenuItem14.setToolTipText("Send this dashboards settings to the selected player(s)");

			jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.synch();
						}
					});
				}
			});
		}

		return jMenuItem14;
	}

	private javax.swing.JMenuItem getJMenuItemFileRequest() {
		if (jMenuItem17 == null) {
			jMenuItem17 = new javax.swing.JMenuItem();
			jMenuItem17.setBounds(68, 57, 74, 10);
			jMenuItem17.setText("File Request...");
			jMenuItem17.setMnemonic(java.awt.event.KeyEvent.VK_R);
			jMenuItem17.setName("FileRequest");
			jMenuItem17.setEnabled(false);
			jMenuItem17.setToolTipText("Retrieves specialized player files, such as reports & logs from the selected player(s).");

			jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							final DashBoardRequestDialog dbr =
									new DashBoardRequestDialog( dashActions, LMMJFrame.this );
							
							dbr.setVisible(true);
						}
					});
				}
			});
		}

		return jMenuItem17;
	}

	private javax.swing.JMenuItem getJMenuItemSendPlaylist() {
		if (jMenuItem13 == null) {
			jMenuItem13 = new javax.swing.JMenuItem();
			jMenuItem13.setBounds(68, 57, 74, 10);
			jMenuItem13.setText("Send Play List...");
			jMenuItem13.setMnemonic(java.awt.event.KeyEvent.VK_P);
			jMenuItem13.setEnabled(false);
			jMenuItem13.setName("SendPlayList");
			jMenuItem13.setToolTipText("Sends the local playlist file to the selected player(s)");

			jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.sendPlayList();
						}
					});
				}
			});
		}

		return jMenuItem13;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemPlayList() {
		if (jMenuItem8 == null || jMenuItem19 == null) {
			jMenuItem8 = new javax.swing.JMenuItem();
			jMenuItem19 = new javax.swing.JMenuItem();

			jMenuItem8.setBounds(68, 57, 74, 10); jMenuItem19.setBounds(68, 57, 74, 10);

			jMenuItem8.setName("EditPlayList"); jMenuItem19.setName("EditPlayList");

			jMenuItem8.setMnemonic(java.awt.event.KeyEvent.VK_P); jMenuItem19.setMnemonic(java.awt.event.KeyEvent.VK_P);

			jMenuItem8.setText("Play List..."); jMenuItem19.setText("Play List...");

			jMenuItem8.setToolTipText("Allows modification of the play list on the selected player");
			jMenuItem19.setToolTipText("Allows modification of the play list on the selected player");

			jMenuItem8.setEnabled(false); jMenuItem19.setEnabled(false);

			jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.editPlayList();
						}
					});
				}
			});
			jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.editPlayList();
						}
					});
				}
			});

		}
		return jMenuItem8;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemSetup() {
		if (jMenuItem15 == null) {
			jMenuItem15 = new javax.swing.JMenuItem();
			jMenuItem15.setBounds(68, 57, 74, 10);
			jMenuItem15.setName("Settings");
			jMenuItem15.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenuItem15.setText("Settings...");
			jMenuItem15.setToolTipText("Modify the settings for this dashboard");

			jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {							
							dashActions.settings();
						}
					});
				}
			});

		}
		return jMenuItem15;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemRefreshPlayers() {
		if (jMenuItem16 == null) {
			jMenuItem16 = new javax.swing.JMenuItem();
			jMenuItem16.setBounds(68, 57, 74, 10);
			jMenuItem16.setName("RefreshPlayers");
			jMenuItem16.setMnemonic(java.awt.event.KeyEvent.VK_F);
			jMenuItem16.setText("Refresh Players");
			jMenuItem16
					.setToolTipText("Refreshes every player on the dashboard including players that have been dorment");

			jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							sendMessage(
								new CmdMsg(CmdMsg.Commands.SERVER_GET_PLAYERS),
								LMMServer.MASTER_SERVER_ID);
						}
					});
				}
			});

		}
		return jMenuItem16;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemGetFileList() {
		if (jMenuItem9 == null || jMenuItem20 == null) {
			jMenuItem9 = new javax.swing.JMenuItem();
			jMenuItem20 = new javax.swing.JMenuItem();

			jMenuItem9.setBounds(68, 57, 74, 10); jMenuItem20.setBounds(68, 57, 74, 10);

			jMenuItem9.setName("FileList"); jMenuItem20.setName("FileList");

			jMenuItem9.setMnemonic(java.awt.event.KeyEvent.VK_I);
			jMenuItem20.setMnemonic(java.awt.event.KeyEvent.VK_I);

			jMenuItem9.setText("File List..."); jMenuItem20.setText("File List...");

			jMenuItem9.setMnemonic(java.awt.event.KeyEvent.VK_I);
			jMenuItem20.setMnemonic(java.awt.event.KeyEvent.VK_I);

			jMenuItem9.setToolTipText("Gets a listing of the files in the uploaded directory of the player");
			jMenuItem20.setToolTipText("Gets a listing of the files in the uploaded directory of the player");

			jMenuItem9.setEnabled(false); jMenuItem20.setEnabled(false);

			jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.getFileListings();
						}
					});
				}
			});
			jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.getFileListings();
						}
					});
				}
			});

		}
		return jMenuItem9;
	}

	private javax.swing.JMenuItem getJMenuItemProperties() {
		if (jMenuItem10 == null || jMenuItem22 == null) {
			jMenuItem10 = new javax.swing.JMenuItem();
			jMenuItem22 = new javax.swing.JMenuItem();

			jMenuItem10.setBounds(68, 57, 74, 10); jMenuItem22.setBounds(68, 57, 74, 10);

			jMenuItem10.setName("Properties"); jMenuItem22.setName("Properties");

			jMenuItem10.setMnemonic(java.awt.event.KeyEvent.VK_P); jMenuItem22.setMnemonic(java.awt.event.KeyEvent.VK_P);

			jMenuItem10.setText("Properties..."); jMenuItem22.setText("Properties...");

			jMenuItem10.setToolTipText("Gets a listing of properties for the selected player");
			jMenuItem22.setToolTipText("Gets a listing of properties for the selected player");

			jMenuItem10.setEnabled(false); jMenuItem22.setEnabled(false);

			jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.getProperties();
						}
					});
				}
			});
			jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.getProperties();
						}
					});
				}
			});

		}
		return jMenuItem10;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemConfigure() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new javax.swing.JMenuItem();
			jMenuItem2.setBounds(68, 57, 74, 10);
			jMenuItem2.setName("Settings");
			jMenuItem2.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenuItem2.setText("Settings...");
			jMenuItem2
					.setToolTipText("Retrieves the settings for the selected player and allows the settings to be modified");
			jMenuItem2.setEnabled(false);

			jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] sel = getJTable().getSelectedRows();
					if (sel.length == 1) {
						getTextOutputPanel().addOutput(
							"Requesting player(s) settings...",
							Message.STATUS_FROM_SELF);

						sendMessage(
							new CmdMsg(CmdMsg.Commands.GET_SETTINGS),
							getSelectedRecipients());
					}

				}
			});

		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemAbout() {
		if (jMenuItem4 == null) {
			jMenuItem4 = new javax.swing.JMenuItem();
			jMenuItem4.setBounds(68, 57, 74, 10);
			jMenuItem4.setName("About");
			jMenuItem4.setMnemonic(java.awt.event.KeyEvent.VK_B);
			jMenuItem4.setText("About...");

			jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.about();
						}
					});
				}
			});

		}
		return jMenuItem4;
	}

	/**
	 * 1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	/*
	 * private javax.swing.JMenuItem getJMenuItemUpdate() { if(jMenuItem16 ==
	 * null) { jMenuItem16 = new javax.swing.JMenuItem();
	 * jMenuItem16.setBounds(68, 57, 74, 10); jMenuItem16.setName("Update");
	 * jMenuItem16.setMnemonic(java.awt.event.KeyEvent.VK_U);
	 * jMenuItem16.setText("Update...");
	 * 
	 * jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
	 * public void actionPerformed(java.awt.event.ActionEvent e) {
	 * SwingUtilities.invokeLater( new Runnable() { public void run() {
	 * JOptionPane.showMessageDialog( LMMJFrame.this, "Version: " +
	 * LMMUtils.VERSION + CRLF + "Copyright (C) 2005 - " +
	 * String.format("%1$tY", new Date()) + " Last Mile Marketing, all rights
	 * reserved" + CRLF + CRLF + "www.lastmilemarketing.com" + CRLF +
	 * "877-753-4021" + CRLF + "support@lastmilemarketing.com", "About MediaSOLV
	 * Dashboard", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(LMM_LOGO_GIF) ); }
	 * }); } });
	 *  } return jMenuItem16; }
	 */

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemCheatSheet() {
		if (jMenuItem7 == null) {
			jMenuItem7 = new javax.swing.JMenuItem();
			jMenuItem7.setBounds(68, 57, 74, 10);
			jMenuItem7.setName("CheatSheet");
			jMenuItem7.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenuItem7.setText("Cheat Sheet...");

			jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.cheatSheet();
						}
					});
				}
			});

		}
		return jMenuItem7;
	}

	protected String getSelectedRecipients() {
		int[] sel = getJTable().getSelectedRows();
		if (sel.length <= 0)
			return null;

		String[] names = new String[sel.length];

		for (int i = 0; i < sel.length; i++)
			names[i] = getTableDataModel().getRowAt(sel[i]).getName();

		return MsgUtils.getClientNames(names);
	}

	/**
	 * This method initializes jMenuItem1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemSendFile() {
		if (jMenuItem1 == null || jMenuItem21 == null) {
			jMenuItem1 = new javax.swing.JMenuItem();
			jMenuItem21 = new javax.swing.JMenuItem();

			jMenuItem1.setBounds(68, 57, 74, 10); jMenuItem21.setBounds(68, 57, 74, 10);

			jMenuItem1.setName("SendFile"); jMenuItem21.setName("SendFile");

			jMenuItem1.setMnemonic(java.awt.event.KeyEvent.VK_S);
			jMenuItem21.setMnemonic(java.awt.event.KeyEvent.VK_S);

			jMenuItem1.setText("Send File..."); jMenuItem21.setText("Send File...");

			jMenuItem1.setToolTipText("Sends a file to the selected player");
			jMenuItem21.setToolTipText("Sends a file to the selected player");

			jMenuItem1.setEnabled(false); jMenuItem21.setEnabled(false);

			jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.sendFiles();
						}
					});
				}
			});
			jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dashActions.sendFiles();
						}
					});
				}
			});

		}
		return jMenuItem1;
	}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelTotal() {
		if (jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("Total:");
			jLabel.setToolTipText("");
			jLabel.setName("Total");
			jLabel.setToolTipText("Total number of connections");
		}
		return jLabel;
	}

	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelCount() {
		if (jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setText("0");
			jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
			jLabel1.setToolTipText("Total number of connections");
			jLabel1.setPreferredSize(new java.awt.Dimension(23, 16));
		}
		return jLabel1;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneResponse() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new javax.swing.JScrollPane();
			jScrollPane1.setViewportView(getTextOutputPanel());
			jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null,
				"Response",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				null, null));
			
			jScrollPane1.setToolTipText("All responses received from other systems");
			jScrollPane1.setPreferredSize(new java.awt.Dimension(602, 86));
			jScrollPane1.setMinimumSize(new java.awt.Dimension(602, 86));
			jScrollPane1.setMaximumSize(new java.awt.Dimension(602, 86));
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes textOutputPanel
	 * 
	 * @return com.lmm.sched.gui.TextOutputPanel
	 */
	protected com.lmm.sched.gui.TextOutputPanel getTextOutputPanel() {
		if (textOutputPanel == null) {
			textOutputPanel = new com.lmm.sched.gui.TextOutputPanel();
			textOutputPanel.setEditable(false);
			textOutputPanel.setFont(new java.awt.Font("Arial",java.awt.Font.PLAIN, 10));
		}
		return textOutputPanel;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private javax.swing.JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new javax.swing.JSplitPane();
			jSplitPane.setTopComponent(getJScrollPane());
			jSplitPane.setBottomComponent(getJScrollPaneResponse());
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setPreferredSize(new java.awt.Dimension(601, 364));
			jSplitPane.setDividerLocation((int) (getHeight() * 0.60));
			jSplitPane.setResizeWeight(1.0);
		}
		return jSplitPane;
	}
} // @jve:visual-info decl-index=0 visual-constraint="10,10"
