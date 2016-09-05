package com.lmm.enseo;


import java.io.File;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.lmm.tools.LMMLogger;

public class EnseoThemeParser extends DefaultHandler {

	private Vector neededResources = new Vector(8);
	private static HashSet<String> resourceSet = null;
	
	
	//elements that will have a resource attached to them
	public static final String[] RES_ELEMENTS = {
		".jpg", ".gif", ".swf", ".htm", ".html",  //web stuff
		".mpg", ".bmp", ".txt", ".csv"  //other stuff
	};

	//ignore any URL resources
	public static final String INVALID_RES_PREFIX =  "http://";


    public void startElement( String namespace, String localname, String qname, Attributes attributes)
        	throws SAXException {
    }
    
    public void endElement( String namespace,String localname, String qname)
    		throws org.xml.sax.SAXException {
    }



    public void characters( char[] ch, int start, int len ) {
        String text = new String(ch, start, len);
        String val = text.trim();
        
		int indx = -1;
        if( val.length() > 0  && (indx = val.lastIndexOf(".")) > 0 && !val.startsWith(INVALID_RES_PREFIX) ) {
        	if( isValidExtension(val.substring(indx, val.length())) ) {
	        	//only add the file name & not the path
	        	String elem = val.substring(val.lastIndexOf("\\")+1, val.length());
				if( !neededResources.contains(elem) )
					neededResources.add( elem );
        	}
        }
    }
    
    private boolean isValidExtension( String val ) {

		if( resourceSet.contains(val) ) {
			for( int i = 0; i < RES_ELEMENTS.length; i++ )
				if( RES_ELEMENTS[i].equalsIgnoreCase(val) )
					return true;
		}
		
		return false;
    }
    

    private static XMLReader makeXMLReader() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        
        //load our file types into an lookup table
        if( resourceSet == null ) {
        	resourceSet = new HashSet<String>(8);
			for( int i = 0; i < RES_ELEMENTS.length; i++ )
				resourceSet.add( RES_ELEMENTS[i] );
        }

        return saxParser.getXMLReader();
    }
    
    public String[] getNeededResources() {
    	String[] arr = new String[ neededResources.size() ];
    	return (String[])neededResources.toArray( arr );
    }
    
    public void parse( File file ) {
    	try {
			XMLReader reader = makeXMLReader();	        
			reader.setContentHandler( this );
			reader.parse( new InputSource(file.toURI().toString()) );
    	}
    	catch( Exception ex ) {
    		LMMLogger.error("Unable to parse XML file (" + file + ")", ex);
    	}

    }

    public static void main(final String[] args) throws Exception {
        XMLReader reader = makeXMLReader();
        
		EnseoThemeParser parser = new EnseoThemeParser();
        reader.setContentHandler( parser );
        reader.parse( new InputSource("C:/tricad/LMM/software/video/all.xml") );
 
		String[] s = parser.getNeededResources();
 		for( int i = 0; i < s.length; i++ )
			System.out.println( s[i] );       

    }


}