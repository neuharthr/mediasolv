package com.lmm.gui.files;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.lmm.enseo.EnseoThemeParser;
import com.lmm.tools.FileFilters;

/**
 * Adds the accessory JPanel to the given FileChooser & sets it file view accordingly.
 * 
 */
public class ChooserAccessory extends JPanel implements PropertyChangeListener {

	private JCheckBox themeJCheckBox = new JCheckBox("Send All Theme File(s)");
	private JList msgList = new JList();
	private String[] themeFiles = null;

	private static final ImageIcon IMG_THEME = new ImageIcon(ChooserAccessory.class.getResource("/file-miscdoc.png"));
	private static final ImageIcon IMG_VIDEO = new ImageIcon(ChooserAccessory.class.getResource("/file-video.png"));
	private static final ImageIcon IMG_IMAGE = new ImageIcon(ChooserAccessory.class.getResource("/file-image.png"));

	/**
	 * Maps FileFilters to a specific color for UI purposes.
	 */
	public static enum FilterColors {
		ImageFilter(FileFilters.ImageFileFilterGUI, IMG_IMAGE),
		MPGFilter(FileFilters.MPGFileFilterGUI,IMG_VIDEO),
		ThemeFilter(FileFilters.XMLFileFilterGUI, IMG_THEME);

		final FileFilter fileFilter;
		final ImageIcon imgIcon;
		private FilterColors(FileFilter ff, ImageIcon imgIcon ) {
			this.fileFilter = ff;
			this.imgIcon = imgIcon;
		}
		
		public FileFilter getFilter() { return this.fileFilter; }
		public ImageIcon getImageIcon() { return this.imgIcon; }
	}

	public ChooserAccessory( JFileChooser chooser ) {
		super( new FlowLayout(3) );
		init( chooser );
	}
	
	private void init( JFileChooser chooser ) {
		setBorder(BorderFactory.createTitledBorder("Theme Options"));

		themeJCheckBox.setToolTipText("Should the theme file and all of its resource files be sent now");
		themeJCheckBox.setEnabled( false );
		
		msgList.setBackground( getBackground() );

		msgList.setCellRenderer( new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
						JList list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
				
				Component ret = super.getListCellRendererComponent(
					list, value, index, false, false);

				setToolTipText( value.toString() );
				
				return ret;
			}
		});


		chooser.addPropertyChangeListener(this);
		setPreferredSize( new Dimension(180, 100) );

		add( themeJCheckBox );
		add( msgList );
		
		for( int i = 0; i < FilterColors.values().length; i++ ) {
			chooser.addChoosableFileFilter( FilterColors.values()[i].getFilter() );
		}
		
		chooser.setFileView( new ChooserView() );
	}


    public void propertyChange(PropertyChangeEvent evt) {

		String changeName = evt.getPropertyName();
		if( changeName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) ) {
	
			themeFiles = new String[0];

			File file = (File) evt.getNewValue();						
			if( file != null && FileFilters.XMLFileFilterGUI.accept(file) ) {
				
				EnseoThemeParser ensParser = new EnseoThemeParser();
				ensParser.parse( file );
				themeFiles = ensParser.getNeededResources();
				
				themeJCheckBox.setEnabled( true );
				themeJCheckBox.setSelected( true );
			}
			else {
				themeJCheckBox.setEnabled( false );			
				themeJCheckBox.setSelected( false );
				themeFiles = new String[0];
			}
			
			msgList.setListData( themeFiles );
		}
		
		
    }
    
    public String[] getThemeFiles() {
    	return themeFiles;
    }

	public boolean isUploadEntireTheme() {
		return themeJCheckBox.isSelected();
	}

}
