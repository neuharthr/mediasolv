package swingbeanformbuilder.gui.swing.document;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


/**
 * Document used to validate <code>Double</code> datas.
 * 
 * @author s-oualid
 */
public class SBFBDoubleDocument extends SBFBStandardDocument {
    
    private boolean alreadyContainDot = false;

    public SBFBDoubleDocument() {
        
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
    	if (str.length() > 1) {
    		try {
    			Double.parseDouble(str);
    		} catch (Exception e) {
    			return;
    		}
    	} else {
	        boolean isNumber = true;
	        boolean isDot = false;
	    	try {
	            Integer.parseInt(str);
	        } catch (Exception e) {
	        	isNumber = false;
	        }
	        if (".".equals(str) || ",".equals(str)) {
	        	isDot = true;
	        }
	        if ((!isNumber && !isDot)
	        		|| isDot && alreadyContainDot ) {
	        	return;
	        }
    	}
    	if (str.indexOf(",") != -1 || str.indexOf(".") != -1) {
    		alreadyContainDot = true;
    	}
        super.insertString(offs,str,a);
    }
    
    public void remove(int offs, int len) throws BadLocationException {
    	if (getText(offs,len).indexOf(",") != -1 || getText(offs,len).indexOf(".") != -1) {
    		alreadyContainDot = false;
    	}
    	super.remove(offs, len);
    }
    
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    	if (getText(offset,length).indexOf(",") != -1 || getText(offset,length).indexOf(".") != -1) {
    		alreadyContainDot = false;
    	}
    	super.replace(offset, length, text, attrs);
    }
    
}