package swingbeanformbuilder.gui.swing.form.components;

/**
 * Implement this interface to create custom components for SBFB, implemented classes <u>must</u> inherit <code>java.awt.Component</code>.
 * 
 * @author s-oualid
 */
public interface ISBFBCustomComponent
{

	public abstract Object getValue();

	public abstract void setValue( Object o );

	public void setEditable( boolean editable );
}
