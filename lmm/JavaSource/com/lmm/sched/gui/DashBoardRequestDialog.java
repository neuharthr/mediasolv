package com.lmm.sched.gui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import com.lmm.gui.DatePicker;
import com.lmm.tools.LMMLogger;

/**
 * Creates a dialog for retrieving "special" files from the players. The retrieval is done on players
 * that are selcted and visible throught the DashBoardActions class.
 *
 */
public class DashBoardRequestDialog extends JDialog implements ActionListener {

	private final ButtonGroup buttonTypeGroup = new ButtonGroup();
	private javax.swing.JRadioButton jRadioButton = null;
	private javax.swing.JRadioButton jRadioButton1 = null;
	private javax.swing.JPanel jPanel = null;
	private javax.swing.JButton jButton = null;
	private javax.swing.JButton jButton1 = null;

	private ObservingJButton startDateButton;
	private ObservingJButton endDateButton;
	private javax.swing.JLabel jLabelDate = null;
	private javax.swing.JLabel jLabelDateTo = null;

	private final DashBoardActions dBoardActions;

	private static final int DAYS_PAST = 120;
	private static final long DAYS_PAST_MILLIS = DAYS_PAST * 24L * 60L * 60L * 1000L;
	private static final String FRMT_DATE = "MM/dd/yyyy";
	
	
    /**
     * This is a constructor
     */
    public DashBoardRequestDialog( DashBoardActions dBoardActions, Frame parentFrame ) {
        super(parentFrame);
        if( dBoardActions == null )
        	throw new IllegalArgumentException("A non NULL dashboard action handler must be given");
        
        this.dBoardActions = dBoardActions;
        initialize();
    }
  
	class ObservingJButton extends JButton implements Observer {
		public void update(Observable o, Object arg) {
			Calendar calendar = (Calendar) arg;
			DatePicker dp = (DatePicker) o;
			setText(dp.formatDate(calendar, FRMT_DATE));
		}
	}

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setLayout(null);
        
        getJRadioButtonLogs().addActionListener(this);
        getJRadioButtonPOP().addActionListener(this);
        getJButtonCancel().addActionListener(this);
        getJButtonOk().addActionListener(this);

        buttonTypeGroup.add(getJRadioButtonPOP());
        buttonTypeGroup.add(getJRadioButtonLogs());
        getJRadioButtonPOP().doClick();	//default selection
        
        this.add(getJRadioButtonPOP(), null);
		this.add(getStartDateButton(), null);
		this.add(getEndDateButton(), null);

        this.add(getJRadioButtonLogs(), null);
        this.add(getJPanelButton(), null);
        this.add(getJLabelDate(), null);
		this.add(getJLabelTo(), null);
        
		
		this.setSize(450, 240);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setName("dBoardDialog");
        this.setTitle("Choose file type");
		this.setModal(true);
		//this.setResizable(false);
		this.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);

		//fire this off once this dialog is displaying
		addWindowListener( new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				getJButtonCancel().requestFocusInWindow();
			}
		});
    }
	
    /**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButtonPOP() {
		if(jRadioButton == null) {
			jRadioButton = new javax.swing.JRadioButton();
			jRadioButton.setBounds(18, 5, 247, 21);
			jRadioButton.setName("POPReport");
			jRadioButton.setText("Proof of Performance Report (pdf)");
			jRadioButton.setToolTipText("Retrieves a Proof of Performance report as specified by the date range.");
		}
		return jRadioButton;
	}
	
	public void actionPerformed(ActionEvent ev) {
		
		if( ev.getSource() instanceof JRadioButton ) {
			getStartDateButton().setEnabled( ev.getSource() == getJRadioButtonPOP() );
			getEndDateButton().setEnabled( ev.getSource() == getJRadioButtonPOP() );
			getJLabelDate().setEnabled( ev.getSource() == getJRadioButtonPOP() );
			getJLabelTo().setEnabled( ev.getSource() == getJRadioButtonPOP() );
		}
		else if( ev.getSource() == getJButtonCancel() ) {
			dispose();
		}
		else if( ev.getSource() == getJButtonOk() ) {
			
			//first, validate our input
			String s = isInputValid();
			if( s != null ) {
				JOptionPane.showMessageDialog(DashBoardRequestDialog.this,
						s, "Invalid Input", JOptionPane.OK_OPTION);
				return;
			}
				
			
			//second, send the request
			if( getJRadioButtonPOP().isSelected() ) {
				DateFormat sd = new SimpleDateFormat(FRMT_DATE);
				try {
					final Date startDate = sd.parse(getStartDateButton().getText());
					final Date endDate = sd.parse(getEndDateButton().getText());
					
					dBoardActions.getPOPReport(
							startDate, endDate, null);
				}
				catch( ParseException pe ) {
					LMMLogger.error("Unable to parse the given dates from the given selection", pe);
				}
			}
			else if( getJRadioButtonLogs().isSelected() ) {
				dBoardActions.getLogFiles();
			}

			//success after the ok button closes the panel
			dispose();
		}

		
	}
		
	private ObservingJButton getStartDateButton() {
		if(startDateButton == null) {
			startDateButton = new ObservingJButton();
			startDateButton.setName("StartDateButton");
			startDateButton.setBounds(115, 29, 105, 21);
			startDateButton.setFont( new Font("Arial", Font.BOLD, 12) );

			//30 days ago
			final Date defStart = new Date( System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));
			final DatePicker dp = new DatePicker(startDateButton);
			dp.setSelectedDate(defStart);
			startDateButton.setText( dp.formatDate(defStart, FRMT_DATE) );
			
			startDateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Date selectedDate = dp.parseDate(getStartDateButton().getText());
					dp.setSelectedDate(selectedDate);
					dp.start(getStartDateButton());
				};
			});
		}
		return startDateButton;
	}

	private ObservingJButton getEndDateButton() {
		if(endDateButton == null) {
			endDateButton= new ObservingJButton();
			endDateButton.setName("EndDateButton");
			endDateButton.setBounds(255, 29, 105, 21);
			endDateButton.setFont( new Font("Arial", Font.BOLD, 12) );
			
			final Date defEnd = new Date();
			final DatePicker dp = new DatePicker(endDateButton);
			dp.setSelectedDate(defEnd);
			endDateButton.setText( dp.formatDate(defEnd, FRMT_DATE) );

			endDateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Date selectedDate = dp.parseDate(getEndDateButton().getText());
					dp.setSelectedDate(selectedDate);
					dp.start(getEndDateButton());
				};
			});
		}
		return endDateButton;
	}
	
	/**
	 * This method initializes jRadioButton1
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButtonLogs() {
		if(jRadioButton1 == null) {
			jRadioButton1 = new javax.swing.JRadioButton();
			jRadioButton1.setSize(247, 21);
			jRadioButton1.setLocation(17, 91);
			jRadioButton1.setName("LogFile");
			jRadioButton1.setText("Player log file (log)");
			jRadioButton1.setToolTipText("Retrieves the latest log file from the selected player(s), mainly used for trouble shooting.");
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelButton() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			jPanel.add(getJButtonOk(), null);
			jPanel.add(getJButtonCancel(), null);
			jPanel.setBounds(0, 158, 450, 36);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonOk() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setText("Ok");
			jButton.setName("OkButton");
		}
		return jButton;
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
			jButton1.setName("CancelButton");
		}
		return jButton1;
	}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelDate() {
		if(jLabelDate == null) {
			jLabelDate = new javax.swing.JLabel();
			jLabelDate.setBounds(41, 28, 76, 20);
			jLabelDate.setText("Date Range:");
			jLabelDate.setName("dateRange");
		}
		return jLabelDate;
	}

	private javax.swing.JLabel getJLabelTo() {
		if(jLabelDateTo == null) {
			jLabelDateTo = new javax.swing.JLabel();
			jLabelDateTo.setBounds(233, 28, 76, 20);
			jLabelDateTo.setText("to");
			jLabelDateTo.setName("dateRangeTo");
		}
		return jLabelDateTo;
	}

	public String isInputValid() {

		if( getJRadioButtonPOP().isSelected() ) {
			if( getStartDateButton().getText() == null || getEndDateButton().getText() == null )
				return "A valid start & end date must be given.";
			
			DateFormat sd = new SimpleDateFormat(FRMT_DATE);
			try {
				final Date startDate = sd.parse(getStartDateButton().getText());
				final Date endDate = sd.parse(getEndDateButton().getText());
	
				if( endDate.before(startDate) )
					return "The start date must not be before the end date, select a different date range.";
				else if( (endDate.getTime() - startDate.getTime()) > DAYS_PAST_MILLIS ) {
					return "The time window you select must be " + DAYS_PAST + " days or less, please try again.";
				}
			}
			catch( ParseException pe ) {
				return "The start date & end date entries must be valid date inputs, please try again.";
			}
		}

		//this form is valid, do not return an error string
		return null;
	}

	public void show() {
		//always show this dialog in the middle of the parent
		setLocation(
			getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2, 
			getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2 );

		super.show();
	}

	
	public static void main( String[] a ) {
		JFrame f  = new JFrame();
		f.setLocation( 200, 200 );
		f.setSize(400, 400);
		
		DashBoardRequestDialog d = new DashBoardRequestDialog(new DashBoardActions(null), f);
		//d.setLocation(200, 200);
		
		d.setVisible(true);
	}
}