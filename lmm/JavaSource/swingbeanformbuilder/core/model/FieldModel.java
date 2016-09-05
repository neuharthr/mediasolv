/*
 * FieldModel.java
 * 
 * Created on 20 mai 2006, 10:13
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */

package swingbeanformbuilder.core.model;

import java.awt.Color;
import java.util.List;

import swingbeanformbuilder.core.services.ISBFBFormFactory;

/**
 * Represents a class' field in the SBFB object model, it contains customisation
 * informations used when rendering this field in a form.
 * 
 * @author Simon OUALID (symon@tatouage.fr)
 */
public class FieldModel implements Comparable {

	private String customComponent = null;

	private String name = "";

	private String label = null;

	private Color labelColor = null;

	private Color backgroundColor = null;

	private boolean boldLabel = false;

	private boolean getter = false;

	private boolean setter = false;

	private Integer maxlength = null;

	private Integer order = null;

	private String type = null;

	private String fill = null;

	private int columnSpanned = 1;

	private Boolean forceEditable = null;

	private boolean lastOfLine = true;

	private List allowedValues = null;

	private boolean visible = true;

	private boolean allowDigging = true;

	private boolean allowDelete = true;

	private boolean showMenu = true;

	private boolean primitive = false;

	private String render = ISBFBFormFactory.FIELD_RENDER_LIST;

	private String selectionMode = null;

	private ClassModel classModel = null;

	private String className = null;

	private String renderClassName = null;

	private GroupModel group = null;

	private String align = ISBFBFormFactory.FIELD_ALIGN_LEFT;
	
	private boolean password = false;
	
	private String toolTip = null;
	

	/** Creates a new instance of FieldModel */
	public FieldModel(ClassModel c) {
		this.classModel = c;
		order = new Integer(c.getFields().size() + 1);
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGetter() {
		return getter;
	}

	public boolean isSetter() {
		return setter;
	}

	public void setGetter(boolean getter) {
		this.getter = getter;
	}

	public void setSetter(boolean setter) {
		this.setter = setter;
	}

	public boolean equals(Object obj) {
		if (obj instanceof FieldModel) {
			return ((FieldModel) obj).getClassModel().equals(getClassModel()) && ((FieldModel) obj).getName().equals(getName());
		}
		return false;
	}

	public ClassModel getClassModel() {
		return classModel;
	}

	public int hashCode() {
		return getClassModel().hashCode() + getName().hashCode();
	}

	public Integer getMaxlength() {
		return maxlength;
	}

	public void setMaxlength(Integer maxlength) {
		this.maxlength = maxlength;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public int compareTo(Object o) {
		if (o instanceof FieldModel) {
			if (((FieldModel) o).getOrder().intValue() > getOrder().intValue()) {
				return -1;
			} else if (((FieldModel) o).getOrder().intValue() < getOrder().intValue()) {
				return 1;
			} else {
				return ((FieldModel) o).getName().compareTo(getName());
			}
		}
		return 0;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean isForceEditable() {
		return forceEditable;
	}

	public void setForceEditable(Boolean forceEditable) {
		this.forceEditable = forceEditable;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public List getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(List allowedValues) {
		this.allowedValues = allowedValues;
	}

	public void setLastOfLine(boolean lastOfLine) {
		this.lastOfLine = lastOfLine;
	}

	public boolean isLastOfLine() {
		return lastOfLine;
	}

	public void setColumnSpanned(int columnSpanned) {
		this.columnSpanned = columnSpanned;
	}

	public int getColumnSpanned() {
		return columnSpanned;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isAllowDigging() {
		return allowDigging;
	}

	public void setAllowDigging(boolean allowDigging) {
		this.allowDigging = allowDigging;
	}

	public boolean isAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public String toString() {
		return getName();
	}

	public String getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	public boolean isShowMenu() {
		return showMenu;
	}

	public void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public GroupModel getGroup() {
		return group;
	}

	public void setGroup(GroupModel group) {
		this.group = group;
	}

	public void setRender(String value) {
		this.render = value;
	}

	/**
	 * @return Returns the render.
	 */
	public String getRender() {
		return render;
	}

	/**
	 * @return Returns the labelColor.
	 */
	public Color getLabelColor() {
		return labelColor;
	}

	/**
	 * @param labelColor
	 *            The labelColor to set.
	 */
	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	/**
	 * @return Returns the boldLabel.
	 */
	public boolean isBoldLabel() {
		return boldLabel;
	}

	/**
	 * @param boldLabel
	 *            The boldLabel to set.
	 */
	public void setBoldLabel(boolean boldLabel) {
		this.boldLabel = boldLabel;
	}

	public boolean isPrimitive() {
		return primitive;
	}

	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}

	/**
	 * @return Returns the backgroundColor.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor
	 *            The backgroundColor to set.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getCustomComponent() {
		return customComponent;
	}

	public void setCustomComponent(String customRenderer) {
		this.customComponent = customRenderer;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String anchor) {
		this.align = anchor;
	}

	public String getRenderClassName() {
		return renderClassName;
	}

	public void setRenderClassName(String renderClassName) {
		this.renderClassName = renderClassName;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
}
