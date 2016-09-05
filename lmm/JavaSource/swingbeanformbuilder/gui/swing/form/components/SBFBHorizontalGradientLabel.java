/*
 * Created on 16 déc. 2004
 */
package swingbeanformbuilder.gui.swing.form.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * A beautifull gradient used as titled bars on forms.
 * 
 * @author s-oualid
 */
public class SBFBHorizontalGradientLabel extends JPanel
{

	private JLabel		label;
	private boolean		isEnabled	= false;

	final static Color	COLOR_DISABLED	= new Color( 107, 136, 204 );				/* light blue */
	final static Color	COLOR_ENABLED	= new Color( 255, 95, 95 );				/* light red */
	private Color		currentColor;
	private JPanel		toolsPanel	= new JPanel( new FlowLayout( FlowLayout.RIGHT ) );	;

	public SBFBHorizontalGradientLabel( String text )
	{
		this( null, text );
	}

	public SBFBHorizontalGradientLabel( ImageIcon icon, String text )
	{
		super( new GridBagLayout() );
		setOpaque( false );

		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1;
		gc.insets = new Insets( 5, 5, 5, 5 );

		label = null;
		if ( icon != null )
			label = new JLabel( icon );
		else
			label = new JLabel();

		label.setText( text );
		label.setFont( new Font( "Trebuchet MS", Font.PLAIN, 21 ) );
		label.setForeground( Color.white );
		label.setHorizontalAlignment( SwingConstants.LEFT );
		label.setVerticalAlignment( SwingConstants.CENTER );

		add( label, gc );

		gc.gridx++;
		gc.weightx = 1;
		toolsPanel.setOpaque( false );

		add( toolsPanel, gc );
		
		gc.gridy++;
		gc.gridx=0;
		gc.gridwidth=2;
		gc.insets = new Insets( 0, 0, 0, 0 );
		add(new JSeparator(),gc);
	}

	public void paint( Graphics g )
	{
		Graphics2D g2 = ( Graphics2D ) g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		if ( isEnabled() )
			currentColor = COLOR_ENABLED;
		else
			currentColor = COLOR_DISABLED;

		GradientPaint gradient = new GradientPaint( 0, 0, currentColor, ( float ) getSize().getWidth(), 0, Color.white );
		g2.setPaint( gradient );
		g2.fill( new RoundRectangle2D.Double( 0, 0, ( float ) getSize().getWidth(), ( float ) getSize().getHeight(), 0, 0 ) );

		super.paint( g2 );
	}

	public void setText( String text )
	{
		label.setText( text );
	}

	public String getText()
	{
		return label.getText();
	}

	public void setIcon( Icon icon )
	{
		label.setIcon( icon );
	}

	public Color getCurrentColor()
	{
		return currentColor;
	}

	public void setCurrentColor( Color currentColor )
	{
		this.currentColor = currentColor;
	}

	public boolean isEnabled()
	{
		return isEnabled;
	}

	public void setEnabled( boolean isEnabled )
	{
		this.isEnabled = isEnabled;
	}

	public void setEditable( boolean editable )
	{
		setEnabled( editable );
		repaint();
	}

	public boolean isEditable()
	{
		return isEnabled;
	}

	public JPanel getToolsPanel()
	{
		return toolsPanel;
	}

}
