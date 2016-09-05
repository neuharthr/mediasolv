/*
 * SBFBPanel.java
 *
 * Created on 20 mai 2006, 22:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingbeanformbuilder.gui.swing.form;

import java.awt.Window;

import swingbeanformbuilder.core.model.ClassModel;

/**
 * The simplest concretisation of AbstractSBFBForm (a simple panel).
 *
 * @author Simon OUALID
 */
public class SBFBForm extends AbstractSBFBForm {
    
    
    /** Creates a new instance of SBFBForm */
    public SBFBForm(Window parent, ClassModel cm) {
        this.parent = parent;
        this.classModel = cm;
    }
}
