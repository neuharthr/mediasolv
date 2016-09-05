package com.lmm.systray;

import java.io.File;

import com.lmm.db.DBCommon;
import com.lmm.gui.UIUtils;
import com.lmm.msg.MonitorMsg;
import com.lmm.sched.jobs.AutoUpdateJob;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.FormatUtils;
import com.lmm.tools.LMMLogger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import snoozesoft.systray4j.*;

/**
 * The main application that brings up the sys tray icon. All components and icons
 * used are created/found in here.
 * 
 */
public class SysTrayAlert implements SysTrayMenuListener, SystrayDefines, MonitorListener {
    
	private Thread iconCyclerThrd = null;
	private MonitorMsg lastMonitorMsg = MonitorMsg.DISCONNECTED_MONMSG;
    
    private SystrayFlasher flasher = null;
    private SystrayHandler sysMonitor = null;
    private SysTrayMenuItem menuItemExit = null;
    private SysTrayMenuItem menuItemAbout = null;
    private SysTrayMenuItem menuItemAcknowledge = null;
    private SysTrayMenuItem menuItemProperties = null;
    private SysTrayMenuItem menuItemDashboard = null;

    private final SysTrayMenu sysTrayMenu =
        new SysTrayMenu(ALL_ICONS[ICO_DISCON], MonitorMsg.DISCONNECTED_MONMSG.getMessage());

	public static final String CRLF = System.getProperties().getProperty("line.separator");

	
    public SysTrayAlert() {
        super();
    }

    public void show() {    	
		// don´t forget to assign listeners to the icons
		for (int i = 0; i < ALL_ICONS.length; i++)
			ALL_ICONS[i].addSysTrayMenuListener(this);

		// create the menu that is for the systray icon
		initComponents();
		
		//start our monitoring process
		getSysMonitor().startMonitoring();
    }
    
    private SystrayFlasher getSystrayFlasher() {
        if (flasher == null) {
            flasher = new SystrayFlasher(sysTrayMenu);
        }

        return flasher;
    }

    public void startCycleImages() {
        if (iconCyclerThrd == null || iconCyclerThrd.isInterrupted()) {
            iconCyclerThrd =
                new Thread(
                    Thread.currentThread().getThreadGroup(),
                    getSystrayFlasher(),
                    "IconCycler");

            iconCyclerThrd.setDaemon(true);
        }

        if (!iconCyclerThrd.isAlive())
            iconCyclerThrd.start();
    }
    
    private MonitorMsg getLastMonitorMsg() {
    	return lastMonitorMsg;
    }

    /** 
     * This method sets the tooltip flyover text AND it sets the corresponding
     * icon to use. Does not accept a NULL msg for a new state of this class.
     * 
     * @param msg
     */
    public void setMonitorMsg(final MonitorMsg msg) {

        //do not let this guy in my house!
    	if( msg == null ) return;

    	synchronized( lastMonitorMsg ) {
    		lastMonitorMsg = msg;
    	}

        sysTrayMenu.setToolTip(getLastMonitorMsg().getMessage());

        if( getLastMonitorMsg().getState() == MonitorMsg.MonitorStates.FROM_REMOTE ) {
            sysTrayMenu.setIcon(ALL_ICONS[ICO_CONN]);
            
            if( getLastMonitorMsg().getDownedPlayers() > 0 )
            	startCycleImages();        	
        }
        else if( getLastMonitorMsg().getState() == MonitorMsg.MonitorStates.CONNECTED ) {
            sysTrayMenu.setIcon(ALL_ICONS[ICO_CONN]);
        }
        else {
            stopCycleImages();
            sysTrayMenu.setIcon(ALL_ICONS[ICO_DISCON]);        	
        }

    }

    public void stopCycleImages() {
        if (iconCyclerThrd != null)
            iconCyclerThrd.interrupt();

        iconCyclerThrd = null;
    }

    public static void main(String[] args) {

		LMMLogger.info( "Starting MediaSOLV Alerter v. " + LMMUtils.VERSION );
		System.setProperty( DBCommon.DB_FILENAME_KEY, "db_systray.yap" );

        try {
			try {
				UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			}catch( Exception ex ) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
			if( new AutoUpdateJob().doUpdate() <= 0 ) {
				SysTrayAlert sysAlert = new SysTrayAlert();
				sysAlert.show();
			}
			else {
				LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
				UIUtils.showUpdateUI();
			}
			
        }
        catch (Exception e) {
            LMMLogger.error("Unable to start the application", e);
            System.exit(-1);
        }

    }

    private void exitApp() {
        System.exit(0);
    }

    public void menuItemSelected(SysTrayMenuEvent e) {
        if (e.getSource() == getMenuItemExit()) {
            sysTrayMenu.hideIcon();
            exitApp();
        }
        else if (e.getSource() == getMenuItemAbout()) {
            JOptionPane.showMessageDialog(
                null, "MeidaSOLV Alerter  (Version " + LMMUtils.VERSION + ")",
                "About Media Alerter", JOptionPane.INFORMATION_MESSAGE );
        }
        else if (e.getSource() == getMenuItemProperties()) {
            //do the properties action here
        	
        	String msg = "";
        	synchronized( lastMonitorMsg ) {	//dont let this ref change
	        	msg =
	        		"Last Update:  " + 
	        			(getLastMonitorMsg().isLocalMsg() ? "(none)" : FormatUtils.abvrDate(getLastMonitorMsg().getMsgDate())) + CRLF +
	        		CRLF +
	        		(getLastMonitorMsg().isLocalMsg() ? "-" : getLastMonitorMsg().getUpPlayers()) +
	        			"  Healthy Players" + CRLF +
	        		(getLastMonitorMsg().isLocalMsg() ? "-" : getLastMonitorMsg().getDownedPlayers()) +
	        			"  Down Players" + CRLF +
	        		(getLastMonitorMsg().isLocalMsg() ? "-" : getLastMonitorMsg().getQuestionablePlayers()) +
	        			"  Questionable Players" + CRLF +
	    			(getLastMonitorMsg().isLocalMsg() ? "-" : getLastMonitorMsg().getIdlePlayers()) +
	    				"  Idle Players" + CRLF;
        	}

            JOptionPane.showMessageDialog(
            		null, msg,
            		"MeidaSOLV Status", JOptionPane.INFORMATION_MESSAGE );
        }
        else if (e.getSource() == getMenuItemAcknowledge()) {
        	stopCycleImages();
        }
		else if(e.getSource() == getMenuItemDashboard()) {
			try {
				File appDir = new File( LMMUtils.getAppsDir() );
				File batFile = new File( LMMUtils.getAppsDir() + SystrayDefines.EXEC_DASHBOARD );
				
				if( batFile.exists() && batFile.isFile() && appDir.exists() && appDir.isDirectory() ) {
					Runtime.getRuntime().exec(
							SystrayDefines.EXEC_CMD +
							appDir + " " +
							SystrayDefines.EXEC_DASHBOARD );
				}
				else
					throw new IllegalStateException(
							"Unable to find startup file or one of its components, " + batFile.getAbsolutePath() );
			}
			catch( Exception ex ) {
				LMMLogger.error( "Unable to start Dashboard application", ex );
			}
		}
        

    }
    
    public void iconLeftClicked(SysTrayMenuEvent e) {
    }

    public void iconLeftDoubleClicked(SysTrayMenuEvent e) {
    	//show the properties window when double clicked
    	menuItemSelected(
    			new SysTrayMenuEvent(getMenuItemProperties(), "showProps") );
    }

    private void initComponents() {
        LMMLogger.info("Creating systray items...");


        // insert items
        sysTrayMenu.addItem(getMenuItemExit());
        sysTrayMenu.addItem(getMenuItemAbout());
        sysTrayMenu.addSeparator();
        sysTrayMenu.addItem(getMenuItemProperties());
        sysTrayMenu.addItem(getMenuItemDashboard());
        sysTrayMenu.addItem(getMenuItemAcknowledge()); //top component

        initConnections();

        //start trying to connect for alarms immediately
    }

    private void initConnections() {
        getMenuItemExit().addSysTrayMenuListener(this);
        getMenuItemAbout().addSysTrayMenuListener(this);

        getMenuItemAcknowledge().addSysTrayMenuListener(this);
        getMenuItemProperties().addSysTrayMenuListener(this);
        getMenuItemDashboard().addSysTrayMenuListener(this);
    }

    private SysTrayMenuItem getMenuItemProperties() {
        if (menuItemProperties == null) {
            menuItemProperties =
                new SysTrayMenuItem("Properties...", "properties");
        }

        return menuItemProperties;
    }

    private SysTrayMenuItem getMenuItemExit() {
        if (menuItemExit == null) {
            menuItemExit = new SysTrayMenuItem("Exit", "exit");
        }

        return menuItemExit;
    }

    private SysTrayMenuItem getMenuItemAbout() {
        if (menuItemAbout == null) {
            menuItemAbout = new SysTrayMenuItem("About...", "about");
        }

        return menuItemAbout;
    }

    private SysTrayMenuItem getMenuItemAcknowledge() {
        if (menuItemAcknowledge == null) {
        	menuItemAcknowledge = new SysTrayMenuItem("Acknowledge", "acknowledge");
        }

        return menuItemAcknowledge;
    }
    
    private SysTrayMenuItem getMenuItemDashboard() {
        if (menuItemDashboard == null) {
        	menuItemDashboard = new SysTrayMenuItem("Start Dashboard...", "startDash");
        }

        return menuItemDashboard;
    }
    

    /**
     * @return
     */
    private SystrayHandler getSysMonitor() {
    	if( sysMonitor == null )
			sysMonitor = new SystrayHandler(this);
        return sysMonitor;
    }

}
