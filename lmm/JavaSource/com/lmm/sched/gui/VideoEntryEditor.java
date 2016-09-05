package com.lmm.sched.gui;


import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.lmm.sched.data.VideoEntry;
import com.lmm.sched.proc.LMMUtils;


public class VideoEntryEditor extends JDialog {

	private int response = JOptionPane.CANCEL_OPTION;
    private javax.swing.JPanel jContentPane = null;
    private PlayOrderCriteria plOrderCriteria = new PlayOrderCriteria();

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JLabel jLabel1 = null;
	private javax.swing.JLabel jLabel2 = null;
	private javax.swing.JLabel jLabel3 = null;
	private javax.swing.JLabel jLabel4 = null;
	private javax.swing.JLabel jLabel5 = null;
	private javax.swing.JLabel jLabel6 = null;
	private javax.swing.JTextField jTextFieldPlayOrder = null;
	private javax.swing.JLabel jLabel7 = null;
	private javax.swing.JComboBox jComboBoxDuration = null;
	private javax.swing.JLabel jLabel8 = null;
	private javax.swing.JTextField jTextFieldWebURL = null;
	private javax.swing.JTextField jTextFieldDayWindow = null;
	private javax.swing.JTextField jTextFieldTimeWindow = null;
	private javax.swing.JLabel jLabel9 = null;
	private javax.swing.JLabel jLabel10 = null;
	private javax.swing.JLabel jLabel11 = null;
	private javax.swing.JPanel jPanel = null;
	private javax.swing.JButton jButtonOk = null;
	private javax.swing.JButton jButtonCancel = null;

	private javax.swing.JLabel jLabel12 = null;
	private javax.swing.JLabel jLabel13 = null;
	private javax.swing.JComboBox jComboBoxChannel = null;
	private javax.swing.JComboBox jComboBoxThemes = null;
	
    /**
     * This is the default constructor
     */
    private VideoEntryEditor() {
        super();
        initialize();
    }

	/**
	 * This is the default constructor
	 */
	public VideoEntryEditor( JDialog owner ) {
		super(owner);
		initialize();
	}
	
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(518, 404);
        this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        this.setResizable(false);
		this.setTitle("Video Entry Editor");
		this.setModal(true);
    }
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJLabel(), null);
            jContentPane.add(getJLabel1(), null);
            jContentPane.add(getJLabel2(), null);
            jContentPane.add(getJLabel3(), null);
            jContentPane.add(getJLabel4(), null);
            jContentPane.add(getJLabel5(), null);
            jContentPane.add(getJLabel6(), null);
            jContentPane.add(getJTextFieldPlayOrder(), null);
            jContentPane.add(getJLabel7(), null);
            jContentPane.add(getJComboBoxDuration(), null);
            jContentPane.add(getJLabel8(), null);
            jContentPane.add(getJTextFieldWebURL(), null);
            jContentPane.add(getJTextFieldDayWindow(), null);
            jContentPane.add(getJTextFieldTimeWindow(), null);
            jContentPane.add(getJLabel9(), null);
            jContentPane.add(getJLabel10(), null);
            jContentPane.add(getJLabel11(), null);
            jContentPane.add(getJPanel(), null);
            jContentPane.add(getJLabel12(), null);
            jContentPane.add(getJLabel13(), null);
            jContentPane.add(getJComboBoxChannel(), null);
            jContentPane.add(getJComboBoxThemes(), null);
        }
        return jContentPane;
    }
    
    public void setVideoEntry( VideoEntry ve ) {

		if( ve != null ) {
			getJComboBoxThemes().setSelectedItem( ve.getFileName() );
			getJTextFieldPlayOrder().setText( LMMUtils.arrayAsString(ve.getPlayOrder(), ",") );
			getJComboBoxDuration().setSelectedItem( new Integer(ve.getPlayDuration().intValue() / 1000) );
			getJTextFieldWebURL().setText( ve.getUrl() );
			getJTextFieldDayWindow().setText( ve.getDayWindow() );
			getJTextFieldTimeWindow().setText( ve.getTimeWindow() );
		}
    }
    
    /**
     * NOTE: reuturns NULL for success
     */
    private String validateInput() {
    	
    	if( getJComboBoxThemes().getSelectedItem() == null || getJComboBoxThemes().getSelectedItem().toString().length() <= 0 )
    		return "A theme file is required";

		if( getJTextFieldPlayOrder().getText() == null || getJTextFieldPlayOrder().getText().length() <= 0 )
			return "Play Order is a required field";

		if( getJTextFieldPlayOrder().getText() != null ) {
			if( !getJTextFieldPlayOrder().getText().matches(
					"\\d{1,2}(,\\d{1,2})*") ) //8,5,6,3,1,4
				return "Invalid entry for the Play Order field, try again.";

			String[] ords = getJTextFieldPlayOrder().getText().split(",");
			for( String strOrd : ords ) {
				Integer currOrder = new Integer(strOrd);
				if( plOrderCriteria.getInvalidOrders().contains(currOrder) )
					return "The play order '" + strOrd + "' is already used on a different theme, please choose something else";
			
				if( currOrder > plOrderCriteria.getMaxOrder() || currOrder < plOrderCriteria.getMinOrder() )
					return "The play order '" + strOrd + "' is not between " 
						+ plOrderCriteria.getMinOrder() + " and "
						+ plOrderCriteria.getMaxOrder() + ", please choose something else";
			}

		}

		if( getJTextFieldDayWindow().getText() != null && getJTextFieldDayWindow().getText().length() > 0 ) {
			if( !getJTextFieldDayWindow().getText().matches("M?T?W?R?F?S?U?") ) //MTWRFSU
				return "Invalid entry for the Day Window field, try again.";			
		}
		
		if( getJTextFieldTimeWindow().getText() != null && getJTextFieldTimeWindow().getText().length() > 0 ) {
			if( !getJTextFieldTimeWindow().getText().matches(
					"\\d{2}:\\d{2}\\-\\d{2}:\\d{2}") ) //04:12-09:15  or  16:00-22:32
				return "Invalid entry for the Time Window field, try again.";
		}
    	
    	//success
    	return null;
    }


	public VideoEntry getVideoEntry() {

		return new VideoEntry(
			null, //assign this later
			getJComboBoxThemes().getSelectedItem().toString(),
			getJTextFieldWebURL().getText(),
			LMMUtils.parseIntegers( getJTextFieldPlayOrder().getText() ),
			new Integer( ((Integer)getJComboBoxDuration().getSelectedItem()).intValue() * 1000 ),
			getJTextFieldTimeWindow().getText(),
			getJTextFieldDayWindow().getText(),
			new Integer( ((Integer)getJComboBoxChannel().getSelectedItem()).intValue() )
		);
	}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(11, 24, 96, 23);
			jLabel.setText("Theme file (xml):");
			jLabel.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel.setToolTipText("The file that conains the video entry design");
			jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new javax.swing.JLabel();
			jLabel1.setSize(96, 23);
			jLabel1.setText("Play Order(s):");
			jLabel1.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel1.setLocation(11, 74);
			jLabel1.setToolTipText("The order this entry will be played at (more than one ordering number is allowed)");
			jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel1;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new javax.swing.JLabel();
			jLabel2.setSize(96, 23);
			jLabel2.setText("Duration:");
			jLabel2.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel2.setLocation(11, 123);
			jLabel2.setToolTipText("Total number of seconds this entry will run for");
			jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel2;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new javax.swing.JLabel();
			jLabel3.setSize(96, 23);
			jLabel3.setText("Web URL:");
			jLabel3.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel3.setLocation(11, 156);
			jLabel3.setToolTipText("Only used if the content in the design file is from thw web");
			jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel3;
	}
	/**
	 * This method initializes jLabel4
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new javax.swing.JLabel();
			jLabel4.setSize(96, 23);
			jLabel4.setText("Day Window:");
			jLabel4.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel4.setLocation(11, 186);
			jLabel4.setToolTipText("The day of week this entry will run on (leave blank for every day)");
			jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel4;
	}
	/**
	 * This method initializes jLabel5
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new javax.swing.JLabel();
			jLabel5.setBounds(11, 253, 97, 25);
			jLabel5.setText("Time Window:");
			jLabel5.setToolTipText("The daily time this entry should play (blank for all day)");
			jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jLabel5;
	}
	/**
	 * This method initializes jLabel6
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new javax.swing.JLabel();
			jLabel6.setBounds(110, 45, 195, 17);
			jLabel6.setText("Used / for path separators (ex: video.xml)");
			jLabel6.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
			jLabel6.setPreferredSize(new java.awt.Dimension(253,17));
		}
		return jLabel6;
	}
	/**
	 * This method initializes jTextFieldPlayOrder
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldPlayOrder() {
		if(jTextFieldPlayOrder == null) {
			jTextFieldPlayOrder = new javax.swing.JTextField();
			jTextFieldPlayOrder.setSize(390, 20);
			jTextFieldPlayOrder.setPreferredSize(new java.awt.Dimension(257,20));
			jTextFieldPlayOrder.setLocation(110, 75);
		}
		return jTextFieldPlayOrder;
	}
	/**
	 * This method initializes jLabel7
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new javax.swing.JLabel();
			jLabel7.setSize(253, 17);
			jLabel7.setText("Use , (commas) to separate orders (ex: 1,3,5 or 2,8)");
			jLabel7.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel7.setLocation(110, 95);
			jLabel7.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
		}
		return jLabel7;
	}
	/**
	 * This method initializes jComboBoxDuration
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxDuration() {
		if(jComboBoxDuration == null) {
			jComboBoxDuration = new javax.swing.JComboBox();
			jComboBoxDuration.setBounds(110, 124, 60, 20);
			
			for( int i = 10; i <= 90; i++ )
				jComboBoxDuration.addItem( new Integer(i) );

			//some reasonable default
			jComboBoxDuration.setSelectedItem( new Integer(30) );
		}
		return jComboBoxDuration;
	}
	/**
	 * This method initializes jLabel8
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new javax.swing.JLabel();
			jLabel8.setSize(69, 17);
			jLabel8.setText("seconds");
			jLabel8.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel8.setLocation(172, 126);
			jLabel8.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
		}
		return jLabel8;
	}
	/**
	 * This method initializes jTextFieldWebURL
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldWebURL() {
		if(jTextFieldWebURL == null) {
			jTextFieldWebURL = new javax.swing.JTextField();
			jTextFieldWebURL.setSize(390, 20);
			jTextFieldWebURL.setPreferredSize(new java.awt.Dimension(257,20));
			jTextFieldWebURL.setLocation(110, 157);
		}
		return jTextFieldWebURL;
	}
	/**
	 * This method initializes jTextFieldDayWindow
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldDayWindow() {
		if(jTextFieldDayWindow == null) {
			jTextFieldDayWindow = new javax.swing.JTextField();
			jTextFieldDayWindow.setBounds(110, 187, 107, 20);
			jTextFieldDayWindow.setPreferredSize(new java.awt.Dimension(107,20));
		}
		return jTextFieldDayWindow;
	}
	/**
	 * This method initializes jTextFieldTimeWindow
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldTimeWindow() {
		if(jTextFieldTimeWindow == null) {
			jTextFieldTimeWindow = new javax.swing.JTextField();
			jTextFieldTimeWindow.setSize(107, 20);
			jTextFieldTimeWindow.setPreferredSize(new java.awt.Dimension(107,20));
			jTextFieldTimeWindow.setLocation(110, 255);
		}
		return jTextFieldTimeWindow;
	}
	/**
	 * This method initializes jLabel9
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel9() {
		if(jLabel9 == null) {
			jLabel9 = new javax.swing.JLabel();
			jLabel9.setSize(253, 17);
			jLabel9.setText("M=Monday, T=Tuesday, W=Wednesday");
			jLabel9.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel9.setLocation(111, 208);
			jLabel9.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
			jLabel9.setToolTipText("");
		}
		return jLabel9;
	}
	/**
	 * This method initializes jLabel10
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel10() {
		if(jLabel10 == null) {
			jLabel10 = new javax.swing.JLabel();
			jLabel10.setSize(253, 17);
			jLabel10.setText("R=Thursday, F=Friday, S=Saturday, U=Sunday");
			jLabel10.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel10.setLocation(111, 224);
			jLabel10.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
		}
		return jLabel10;
	}
	/**
	 * This method initializes jLabel11
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel11() {
		if(jLabel11 == null) {
			jLabel11 = new javax.swing.JLabel();
			jLabel11.setSize(253, 17);
			jLabel11.setText("Example:  09:00-17:00 or 13:00-22:35");
			jLabel11.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel11.setLocation(110, 275);
			jLabel11.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
		}
		return jLabel11;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			jPanel.add(getJButtonOk(), null);
			jPanel.add(getJButtonCancel(), null);
			jPanel.setBounds(1, 333, 512, 34);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButtonOk
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonOk() {
		if(jButtonOk == null) {
			jButtonOk = new javax.swing.JButton();
			jButtonOk.setText("Ok");
			jButtonOk.setMnemonic(java.awt.event.KeyEvent.VK_K);
			jButtonOk.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String errMsg = validateInput();
					if( errMsg == null ) {
						response = JOptionPane.OK_OPTION;
						dispose();
					}
					else {
						JOptionPane.showMessageDialog(
							VideoEntryEditor.this,
							errMsg,
							"Invalid Entry", JOptionPane.OK_OPTION );
					}
				}
			});
		}
		return jButtonOk;
	}
	/**
	 * This method initializes jButtonCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButtonCancel() {
		if(jButtonCancel == null) {
			jButtonCancel = new javax.swing.JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.setMnemonic(java.awt.event.KeyEvent.VK_C);
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					response = JOptionPane.CANCEL_OPTION;    
					dispose();
				}
			});
		}
		return jButtonCancel;
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
    public int getResponse() {
        return response;
    }

	/**
	 * This method initializes jLabel12
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel12() {
		if(jLabel12 == null) {
			jLabel12 = new javax.swing.JLabel();
			jLabel12.setSize(244, 17);
			jLabel12.setText("Note: Mouse over each label for quick help");
			jLabel12.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 10));
			jLabel12.setPreferredSize(new java.awt.Dimension(253,17));
			jLabel12.setLocation(11, 304);
		}
		return jLabel12;
	}
	/**
	 * This method initializes jLabel13
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel13() {
		if(jLabel13 == null) {
			jLabel13 = new javax.swing.JLabel();
			jLabel13.setSize(96, 23);
			jLabel13.setText("Play Channel:");
			jLabel13.setPreferredSize(new java.awt.Dimension(103,23));
			jLabel13.setLocation(335, 123);
			jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			jLabel13.setToolTipText("The channel that this theme will play on inside the video player");
		}
		return jLabel13;
	}
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBoxChannel() {
		if(jComboBoxChannel == null) {
			jComboBoxChannel = new javax.swing.JComboBox();
			jComboBoxChannel.setSize(60, 20);
			jComboBoxChannel.setLocation(433, 124);

			for( int i = 0; i <= 3; i++ )
				jComboBoxChannel.addItem( new Integer(i) );
		}
		return jComboBoxChannel;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JComboBox getJComboBoxThemes() {
		if(jComboBoxThemes == null) {
			jComboBoxThemes = new javax.swing.JComboBox();
			jComboBoxThemes.setSize(390, 20);
			jComboBoxThemes.setPreferredSize(new java.awt.Dimension(257,20));
			jComboBoxThemes.setLocation(110, 25);
			jComboBoxThemes.setEditable( true );
			
			jComboBoxThemes.setMaximumRowCount(14);	//make this a little bigger
			jComboBoxThemes.setToolTipText("A listing of all the theme files on the video player");

		}
		return jComboBoxThemes;
	}
	
	public void setThemeFiles( String[] themeFiles ) {
		
		getJComboBoxThemes().removeAllItems();

		getJComboBoxThemes().addItem("");
		for( String fileName : themeFiles ) {
			getJComboBoxThemes().addItem( fileName );
		}
	}

	public void setPlayOrderCriteria(PlayOrderCriteria prdCriteria ) {
		this.plOrderCriteria = prdCriteria;
		if( this.plOrderCriteria == null )	//do not allow this to ever be null
			this.plOrderCriteria = new PlayOrderCriteria();
	}

}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
