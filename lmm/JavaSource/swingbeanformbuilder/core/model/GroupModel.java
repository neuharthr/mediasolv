package swingbeanformbuilder.core.model;

import swingbeanformbuilder.core.parser.SBFBConfigurationContentHandler;
import swingbeanformbuilder.core.services.ISBFBFormFactory;

/**
 * Represent a group of fields in the SBFB object model.
 * 
 * @author s-oualid
 */
public class GroupModel {

	private String name = null;

	private String label = null;

	private String icon = null;

	private String render = SBFBConfigurationContentHandler.RENDER_AS_TAB_VALUE;

	private String border = SBFBConfigurationContentHandler.ETCHED_BORDER_VALUE;

	private String fill = ISBFBFormFactory.FILL_HORIZONTAL;

	private String align = ISBFBFormFactory.FIELD_ALIGN_LEFT;

	private boolean lastOfLine = true;

	private boolean showBanner = true;

	public GroupModel() {
		super();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setShowBanner(boolean b) {
		this.showBanner = b;
	}

	public boolean isShowBanner() {
		return showBanner;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getRender() {
		return render;
	}

	public void setRender(String render) {
		this.render = render;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String anchor) {
		this.align = anchor;
	}

	public boolean isLastOfLine() {
		return lastOfLine;
	}

	public void setLastOfLine(boolean lastOfLine) {
		this.lastOfLine = lastOfLine;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

}
