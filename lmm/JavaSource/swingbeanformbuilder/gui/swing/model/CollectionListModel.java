/*
 * CollectionListModel.java
 *
 * Created on 21 mai 2006, 02:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingbeanformbuilder.gui.swing.model;

import java.util.List;

import javax.swing.AbstractListModel;

/**
 * A simple table model that use a <code>List</code> to store
 * its data.
 * 
 * @author Simon OUALID
 */
public class CollectionListModel extends AbstractListModel {
    
    private List l = null;
    
    /** Creates a new instance of CollectionListModel */
    public CollectionListModel(List l) {
        this.l = l;
    }

    public int getSize() {
        return l.size();
    }

    public Object getElementAt(int index) {
        return l.get(index);
    }

    public void remove(int i) {
        l.remove(i);
        fireContentsChanged(this,0,l.size());
    }

    public void addAll(List list) {
        l.addAll(list);
        fireContentsChanged(this,0,l.size());
    }

    public void clear() {
        l.clear();
        fireContentsChanged(this,0,l.size());
    }

    public String toString() {
    	return l.toString();
    }

	public List getList() {
		return l;
	}

	public void add(Object o) {
		l.add(o);
		fireIntervalAdded(o,l.size()-1,l.size());
	}
}
