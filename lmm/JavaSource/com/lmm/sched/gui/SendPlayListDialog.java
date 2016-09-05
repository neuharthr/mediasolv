package com.lmm.sched.gui;

import java.awt.Frame;


public class SendPlayListDialog extends PlayListDialog {

	private javax.swing.JButton jButton = null;
	private String selectedRecipients = null;

	/**
	 * This is the default constructor
	 */
	public SendPlayListDialog( Frame owner ) {
		super(owner);
		init();
	}
	
	private void init() {
		setName("frameSendPlayList");
		setModal(true);
		showPlayListSelection();

		//remove all listeners added by the parent
		for( int i = getJButtonSend().getActionListeners().length-1; i >= 0; i-- )
			getJButtonSend().removeActionListener( getJButtonSend().getActionListeners()[i] );

		//do what we need to do
		getJButtonSend().addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setOk( true );
				dispose();
			}
		});
		
	}


	private void showPlayListSelection() {	
		boolean val = true;

		getJComboBoxPlayLists().setVisible( val );
		getJLabelPlayLists().setVisible( val );
		
		getJButtonSend().setEnabled( !val );

		getJButtonSave().setVisible( !val );
		getJButtonNew().setVisible( !val );
		getJButtonEdit().setVisible( !val );
		getJButtonDelete().setVisible( !val );
	}

	public String getSelectedPlayList() {
		return (String)getJComboBoxPlayLists().getSelectedItem();
	}
	
    /**
     * @return
     */
    public String getSelectedRecipients() {
        return selectedRecipients;
    }

    /**
     * @param string
     */
    public void setSelectedRecipients(String string) {
        selectedRecipients = string;
    }


}
