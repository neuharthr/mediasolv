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
import javax.swing.text.PlainDocument;


/**
 * Document used to validate user input length in texfields.
 * 
 * @author s-oualid
 */
public class SBFBStandardDocument extends PlainDocument {
    
    private int maxLength = 0;
    
    /** Creates a new instance of IntegerDocument */
    public SBFBStandardDocument() {
        
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (maxLength > 0) {
            if (offs > maxLength) return;
            if (offs + str.length() > maxLength) {
                str = str.substring(offs + str.length() - maxLength);
            }
        }
        super.insertString(offs,str,a);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}