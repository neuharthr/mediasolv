/*
 * IntegerDocument.java
 *
 * Created on 20 mai 2006, 20:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingbeanformbuilder.gui.swing.document;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


/**
 * Document used to validate <code>Integer</code> datas.
 * 
 * @author s-oualid
 */
public class SBFBIntegerDocument extends SBFBStandardDocument {
    
    /** Creates a new instance of IntegerDocument */
    public SBFBIntegerDocument() {
        
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return;
        }
        super.insertString(offs,str,a);
    }
}