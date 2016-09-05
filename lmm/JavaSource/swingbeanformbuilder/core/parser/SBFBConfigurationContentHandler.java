package swingbeanformbuilder.core.parser;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.exception.SBFBException;
import swingbeanformbuilder.core.model.AllowedValue;
import swingbeanformbuilder.core.model.ClassModel;
import swingbeanformbuilder.core.model.FieldModel;
import swingbeanformbuilder.core.model.GroupModel;

/**
 * Parse the XML configuration file and inject it in the SBFB object model.
 * 
 * @author Simon OUALID
 */
public class SBFBConfigurationContentHandler extends DefaultHandler {

	private int maxColumnSpanned = 1;

	private GroupModel g = null;

	public static final String TAG_FIELD = "field";

	public static final String TAG_CLASS = "class";

	public static final String TAG_GROUP = "group";

	public static final String TAG_DATE_FORMAT = "date-format";

	public static final String TAG_DEFAULT_BUILDER_CLASS = "default-builder";

	public static final String TAG_LABEL_NEW = "new-label";

	public static final String TAG_LABEL_MODIFY = "modify-label";

	public static final String TAG_LABEL_DELETE = "delete-label";

	public static final String TAG_ALLOWED_VALUE = "allowed-value";

	public static final String ATTRIBUTE_CLASS_NAME = "class-name";
	public static final String ATTRIBUTE_BORDER = "border";

	public static final String ATTRIBUTE_RENDER_CLASS_NAME = "render-class-name";

	public static final String ATTRIBUTE_ICON = "icon";

	public static final String ATTRIBUTE_SHOW_MENU = "show-menu";

	public static final String ATTRIBUTE_ALLOW_DELETE = "allow-delete";

	public static final String ATTRIBUTE_ALLOW_DIGGING = "allow-digging";

	public static final String ATTRIBUTE_SELECTION_MODE = "selection-mode";

	public static final String ATTRIBUTE_LABEL_COLOR = "label-color";

	public static final String ATTRIBUTE_BACKGROUND_COLOR = "background-color";

	public static final String ATTRIBUTE_LAST_OF_LINE = "last-of-line";

	public static final String ATTRIBUTE_VISIBLE = "visible";

	public static final String ATTRIBUTE_EDITABLE = "editable";

	public static final String ATTRIBUTE_BOLD_LABEL = "bold-label";

	public static final String ATTRIBUTE_COLUMN_SPANNED = "column-spanned";

	public static final String ATTRIBUTE_RENDER = "render";

	public static final String ATTRIBUTE_FILL = "fill";

	public static final String ATTRIBUTE_LABEL = "label";

	public static final String ATTRIBUTE_ALIGN = "align";

	public static final String ATTRIBUTE_TYPE = "type";

	public static final String ATTRIBUTE_MAXLENGTH = "maxlength";

	public static final String ATTRIBUTE_CUSTOM_COMPONENT = "custom-component";

	public static final String ATTRIBUTE_NAME = "name";

	public static final String ATTRIBUTE_VALUE = "value";

	private static final String ATTRIBUTE_SHOW_BANNER = "show-banner";

	public static final String ATTRIBUTE_PASSWORD = "password";
	public static final String ATTRIBUTE_TOOLTIP = "tool-tip";

	public static final String TRUE_VALUE = "true";

	public static final String FALSE_VALUE = "false";

	public static final String RENDER_AS_TAB_VALUE = "tab";

	public static final String RENDER_AS_INNER_PANEL_VALUE = "innerPanel";

	public static final String ETCHED_BORDER_VALUE = "etched";
	public static final String NO_BORDER_VALUE = "none";

	private ClassModel c = null;

	private FieldModel f = null;

	/** Creates a new instance of ConfigurationContentHandler */
	public SBFBConfigurationContentHandler() {
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (TAG_DATE_FORMAT.equals(qName)) {
			if (attributes.getIndex(ATTRIBUTE_VALUE) >= 0) {
				SBFBConfiguration.setDateFormat(new SimpleDateFormat(attributes.getValue(ATTRIBUTE_VALUE)));
				SBFBConfiguration.setDateFormatString(attributes.getValue(ATTRIBUTE_VALUE));
			}
		} else if (TAG_DEFAULT_BUILDER_CLASS.equals(qName)) {
			if (attributes.getIndex(ATTRIBUTE_CLASS_NAME) >= 0) {
				SBFBConfiguration.setDefaultBuilderClass(attributes.getValue(ATTRIBUTE_CLASS_NAME));
			}
		} else if (TAG_LABEL_NEW.equals(qName)) {
			if (attributes.getIndex(ATTRIBUTE_VALUE) >= 0) {
				SBFBConfiguration.setNewLabel(attributes.getValue(ATTRIBUTE_VALUE));
			}
		} else if (TAG_LABEL_MODIFY.equals(qName)) {
			if (attributes.getIndex(ATTRIBUTE_VALUE) >= 0) {
				SBFBConfiguration.setModifyLabel(attributes.getValue(ATTRIBUTE_VALUE));
			}
		} else if (TAG_LABEL_DELETE.equals(qName)) {
			if (attributes.getIndex(ATTRIBUTE_VALUE) >= 0) {
				SBFBConfiguration.setDeleteLabel(attributes.getValue(ATTRIBUTE_VALUE));
			}
		} else if (TAG_CLASS.equals(qName)) {
			c = new ClassModel();
			c.setClassName(attributes.getValue(ATTRIBUTE_NAME));
			if (SBFBConfiguration.getClasses().containsKey(c.getClassName())) {
				c = (ClassModel) SBFBConfiguration.getClasses().get(c.getClassName());
			}
			if (attributes.getIndex(ATTRIBUTE_LABEL) >= 0) {
				c.setLabel(attributes.getValue(ATTRIBUTE_LABEL));
			}
			if (attributes.getIndex(ATTRIBUTE_SHOW_BANNER) >= 0) {
				if (FALSE_VALUE.equals(attributes.getValue(ATTRIBUTE_SHOW_BANNER))) {
					c.setShowBanner(false);
				}
			}
			if (attributes.getIndex(ATTRIBUTE_ICON) >= 0) {
				c.setIcon(attributes.getValue(ATTRIBUTE_ICON));
			}
			if (attributes.getIndex(ATTRIBUTE_CUSTOM_COMPONENT) >= 0) {
				c.setCustomComponent(attributes.getValue(ATTRIBUTE_CUSTOM_COMPONENT));
			}
		} else if (c != null) {
			if (TAG_FIELD.equals(qName)) {
				f = new FieldModel(c);
				f.setName(attributes.getValue(ATTRIBUTE_NAME));
				if (c.getFields().containsKey(f.getName())) {
					f = (FieldModel) c.getFields().get(f.getName());
				}
				if (attributes.getIndex(ATTRIBUTE_MAXLENGTH) >= 0) {
					f.setMaxlength(new Integer(attributes.getValue(ATTRIBUTE_MAXLENGTH)));
				}
				if (attributes.getIndex(ATTRIBUTE_TYPE) >= 0) {
					f.setType(attributes.getValue(ATTRIBUTE_TYPE));
				}
				if (attributes.getIndex(ATTRIBUTE_LABEL) >= 0) {
					f.setLabel(attributes.getValue(ATTRIBUTE_LABEL));
				}
				
				if (attributes.getIndex(ATTRIBUTE_PASSWORD) >= 0) {
					f.setPassword(true);
				}
				if (attributes.getIndex(ATTRIBUTE_TOOLTIP) >= 0) {
					f.setToolTip(attributes.getValue(ATTRIBUTE_TOOLTIP));
				}
				
				if (attributes.getIndex(ATTRIBUTE_FILL) >= 0) {
					f.setFill(attributes.getValue(ATTRIBUTE_FILL));
				}
				if (attributes.getIndex(ATTRIBUTE_LAST_OF_LINE) >= 0) {
					if (FALSE_VALUE.equals(attributes.getValue(ATTRIBUTE_LAST_OF_LINE))) {
						f.setLastOfLine(false);
						maxColumnSpanned += 2;
					} else {
						maxColumnSpanned = 1;
					}
				} else {
					maxColumnSpanned = 1;
				}
				if (maxColumnSpanned > c.getMaxColumnSpanned()) {
					c.setMaxColumnSpanned(maxColumnSpanned);
				}
				if (attributes.getIndex(ATTRIBUTE_COLUMN_SPANNED) >= 0) {
					f.setColumnSpanned(Integer.parseInt(attributes.getValue(ATTRIBUTE_COLUMN_SPANNED)));
					if (f.getColumnSpanned() > c.getMaxColumnSpanned()) {
						c.setMaxColumnSpanned(f.getColumnSpanned());
					}
				}
				if (attributes.getIndex(ATTRIBUTE_CLASS_NAME) >= 0) {
					f.setClassName(attributes.getValue(ATTRIBUTE_CLASS_NAME));
				}
				if (attributes.getIndex(ATTRIBUTE_RENDER_CLASS_NAME) >= 0) {
					f.setRenderClassName(attributes.getValue(ATTRIBUTE_RENDER_CLASS_NAME));
				}
				if (attributes.getIndex(ATTRIBUTE_EDITABLE) >= 0) {
					if (TRUE_VALUE.equals(attributes.getValue(ATTRIBUTE_EDITABLE))) {
						f.setForceEditable(new Boolean(true));
					} else {
						f.setForceEditable(new Boolean(false));
					}
				}
				if (attributes.getIndex(ATTRIBUTE_VISIBLE) >= 0) {
					if (TRUE_VALUE.equals(attributes.getValue(ATTRIBUTE_VISIBLE))) {
						f.setVisible(true);
					} else {
						f.setVisible(false);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_SHOW_MENU) >= 0) {
					if (FALSE_VALUE.equals(attributes.getValue(ATTRIBUTE_SHOW_MENU))) {
						f.setShowMenu(false);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_SELECTION_MODE) >= 0) {
					f.setSelectionMode(attributes.getValue(ATTRIBUTE_SELECTION_MODE));
				}
				if (attributes.getIndex(ATTRIBUTE_RENDER) >= 0) {
					f.setRender(attributes.getValue(ATTRIBUTE_RENDER));
				}
				if (attributes.getIndex(ATTRIBUTE_LABEL_COLOR) >= 0) {
					String co = attributes.getValue(ATTRIBUTE_LABEL_COLOR);
					try {
						f.setLabelColor(createColorFromHexString(co));
					} catch (Exception e) {
						throw new SBFBException("Cannot parse label-color attribute, invalid value : " + co + " !", e);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_BACKGROUND_COLOR) >= 0) {
					String co = attributes.getValue(ATTRIBUTE_BACKGROUND_COLOR);
					try {
						f.setBackgroundColor(createColorFromHexString(co));
					} catch (Exception e) {
						throw new SBFBException("Cannot parse background-color attribute, invalid value : " + co + " !", e);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_ALLOW_DIGGING) >= 0) {
					if (TRUE_VALUE.equals(attributes.getValue(ATTRIBUTE_ALLOW_DIGGING))) {
						f.setAllowDigging(true);
					} else {
						f.setAllowDigging(false);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_BOLD_LABEL) >= 0) {
					if (TRUE_VALUE.equals(attributes.getValue(ATTRIBUTE_BOLD_LABEL))) {
						f.setBoldLabel(true);
					} else {
						f.setBoldLabel(false);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_ALLOW_DELETE) >= 0) {
					if (TRUE_VALUE.equals(attributes.getValue(ATTRIBUTE_ALLOW_DELETE))) {
						f.setAllowDelete(true);
					} else {
						f.setAllowDelete(false);
					}
				}
				if (attributes.getIndex(ATTRIBUTE_CUSTOM_COMPONENT) >= 0) {
					f.setCustomComponent(attributes.getValue(ATTRIBUTE_CUSTOM_COMPONENT));
				}
				if (attributes.getIndex(ATTRIBUTE_FILL) >= 0) {
					f.setFill(attributes.getValue(ATTRIBUTE_FILL));
				}
				// If we are in a group, we set a pointer on it into the current
				// field
				if (g != null) {
					f.setGroup(g);
				}

			}

		}
		if (f != null) {
			if (TAG_ALLOWED_VALUE.equals(qName)) {
				if (f.getAllowedValues() == null)
					f.setAllowedValues(new ArrayList());
				AllowedValue v = new AllowedValue();
				v.setLabel(attributes.getValue(ATTRIBUTE_LABEL));
				v.setValue(attributes.getValue(ATTRIBUTE_VALUE));
				f.getAllowedValues().add(v);
			}
		}
		if (TAG_GROUP.equals(qName)) {
			g = new GroupModel();
			g.setName(attributes.getValue(ATTRIBUTE_NAME));
			g.setLabel(attributes.getValue(ATTRIBUTE_LABEL));
			if (attributes.getIndex(ATTRIBUTE_SHOW_BANNER) >= 0) {
				if (FALSE_VALUE.equals(attributes.getValue(ATTRIBUTE_SHOW_BANNER))) {
					g.setShowBanner(false);
				}
			}
			if (attributes.getIndex(ATTRIBUTE_ICON) >= 0) {
				g.setIcon(attributes.getValue(ATTRIBUTE_ICON));
			}
			if (attributes.getIndex(ATTRIBUTE_RENDER) >= 0) {
				g.setRender(attributes.getValue(ATTRIBUTE_RENDER));
			}
			if (attributes.getIndex(ATTRIBUTE_FILL) >= 0) {
				g.setFill(attributes.getValue(ATTRIBUTE_FILL));
			}
			if (attributes.getIndex(ATTRIBUTE_ALIGN) >= 0) {
				g.setAlign(attributes.getValue(ATTRIBUTE_ALIGN));
			}
			if (attributes.getIndex(ATTRIBUTE_BORDER) >= 0) {
				g.setBorder(attributes.getValue(ATTRIBUTE_BORDER));
			}
			if (RENDER_AS_INNER_PANEL_VALUE.equals(g.getRender())) {
				if (attributes.getIndex(ATTRIBUTE_LAST_OF_LINE) >= 0) {
					g.setLastOfLine(false);
				}
			}
		}
	}

	private Color createColorFromHexString(String co) {
		int r = Integer.parseInt(co.substring(1, 3), 16);
		int g = Integer.parseInt(co.substring(3, 5), 16);
		int b = Integer.parseInt(co.substring(5, 7), 16);
		Color col = new Color(r, g, b);
		return col;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (TAG_CLASS.equals(qName)) {
			SBFBConfiguration.getClasses().put(c.getClassName(), c);
			c = null;
		} else if (TAG_FIELD.equals(qName)) {
			c.getFields().put(f.getName(), f);
			f = null;
		} else if (TAG_GROUP.equals(qName)) {
			g = null;
		}
	}

}
