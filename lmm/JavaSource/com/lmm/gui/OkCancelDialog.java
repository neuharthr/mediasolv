package com.lmm.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Generic Ok Cancel dialog used for housing panels
 * 
 */
public class OkCancelDialog extends javax.swing.JDialog implements ActionListener {

	public static final int OK_PRESSED = 0;
	public static final int CANCEL_PRESSED = 1;
	private int buttonPressed = CANCEL_PRESSED;
	
	private JButton jButtonCancel = null;
	private JButton jButtonOk = null;
	private JPanel jDialogContentPane = null;
	private JPanel jPanelMain = null;
	private JPanel jPanelSlot = null;

	/**
	 * OkCancelDialog constructor comment.
	 */
	public OkCancelDialog(JFrame owner, String title,
			boolean modal, JPanel displayPanel) {
		this(owner, title, modal);

		setDisplayPanel(displayPanel);
	}

	public OkCancelDialog(JFrame owner, String title, boolean modal) {
		super(owner, modal);

		initialize();

		setTitle(title);
	}

	/**
	 * Method to handle events for the ActionListener interface.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getJButtonOk())
			jButtonOk_ActionPerformed(e);
		if (e.getSource() == getJButtonCancel())
			jButtonCancel_ActionPerformed(e);
	}

	public int getButtonPressed() {
		return buttonPressed;
	}

	/**
	 * Return the JButtonCancel property value.
	 * 
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setName("JButtonCancel");
			jButtonCancel.setMnemonic('c');
			jButtonCancel.setText("Cancel");

			jButtonCancel.setMaximumSize(new java.awt.Dimension(80, 25));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(80, 25));
			jButtonCancel.setMinimumSize(new java.awt.Dimension(80, 25));
		}

		return jButtonCancel;
	}

	/**
	 * Return the JButtonOk property value.
	 * 
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton();
			jButtonOk.setName("JButtonOk");
			jButtonOk.setMnemonic('k');
			jButtonOk.setText("Ok");
			jButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			jButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			jButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));

			jButtonOk.setMaximumSize(new java.awt.Dimension(80, 25));
			jButtonOk.setPreferredSize(new java.awt.Dimension(80, 25));
			jButtonOk.setMinimumSize(new java.awt.Dimension(80, 25));
		}
		return jButtonOk;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * 
	 */
	private JPanel getJDialogContentPane() {
		if (jDialogContentPane == null) {
			jDialogContentPane = new JPanel();
			jDialogContentPane.setName("JDialogContentPane");
			
			jDialogContentPane.setLayout(new BorderLayout());
			getJDialogContentPane().add(getJPanelSlot(), BorderLayout.NORTH);
			getJDialogContentPane().add(getJPanel1(), BorderLayout.SOUTH);
		}

		return jDialogContentPane;
	}

	/**
	 * Return the JPanel1 property value.
	 */
	private JPanel getJPanel1() {
		if (jPanelMain == null) {
			jPanelMain = new JPanel();
			jPanelMain.setName("JPanel1");
			jPanelMain.setLayout(new java.awt.FlowLayout());
			getJPanel1().add(getJButtonOk(), getJButtonOk().getName());
			getJPanel1().add(getJButtonCancel(), getJButtonCancel().getName());
		}

		return jPanelMain;
	}

	/**
	 * Return the JPanelSlot property value.
	 * 
	 */
	protected JPanel getJPanelSlot() {
		if (jPanelSlot == null) {
			jPanelSlot = new JPanel();
			jPanelSlot.setName("JPanelSlot");
			jPanelSlot.setLayout(null);
		}

		return jPanelSlot;
	}

	/**
	 * Initializes connections
	 */
	private void initConnections() {
		getJButtonOk().addActionListener(this);
		getJButtonCancel().addActionListener(this);
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setName("OkCancelDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setSize(200, 213);
		setTitle("Ok Canel Option");
		setContentPane(getJDialogContentPane());
		initConnections();
	}

	/**
	 * Comment
	 */
	private void jButtonCancel_ActionPerformed(
			ActionEvent actionEvent) {
		buttonPressed = CANCEL_PRESSED;
		setVisible(false);
		return;
	}

	/**
	 * Comment
	 */
	private void jButtonOk_ActionPerformed(ActionEvent actionEvent) {
		buttonPressed = OK_PRESSED;
		setVisible(false);
		return;
	}

	public void setDisplayPanel(JPanel displayPanel) {
		if (displayPanel == null)
			throw new IllegalArgumentException(
					"*** Can not have a null panel in the constructor of : "
							+ this.getClass().getName());

		// just use the SpecificPanel object as a place and GridBagConstraint
		// holder
		if (jPanelSlot != null)
			getContentPane().remove(jPanelSlot);

		jPanelSlot = displayPanel;
		getContentPane().add(getJPanelSlot(), BorderLayout.NORTH);
	}

	public void setVisible(boolean val) {
		//always show this dialog in the middle of the parent
		setLocation(
			getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2, 
			getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2 );

		//super.show();
		super.setVisible(val);
	}

	/**
	 * Sets the OK to visible or not
	 * 
	 */
	public void setOKButtonVisible(boolean visible) {
		getJButtonOk().setVisible(visible);
	}

	/**
	 * Sets the CANCEL to visible or not
	 * 
	 */
	public void setCancelButtonVisible(boolean visible) {
		getJButtonCancel().setVisible(visible);
	}

	public void setOkButtonToolTip(String tip) {
		getJButtonOk().setToolTipText(tip);
	}

	public void setOkButtonText(String text) {
		getJButtonOk().setText(text);
	}

	public void addOkButtonListener( ActionListener lst ) {
		getJButtonOk().addActionListener( lst );
	}

}
