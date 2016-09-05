package com.lmm.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;


/**
 * Write this message, then immediately write the file content. This process chunks it up
 * so we can write files larger Integer.MAX_INT size
 */
public class FileMsg extends BaseMsg {
	
	private File file = null;
	private byte[] content = null;
	private boolean request = false;
	
	private FileMsg[] multiParts = null;
	private int lastChunkSz = 0;
	private int chunkOffset = 0;
	private int chunkCurr = 0;

	public static final int MAX_FILE_SIZE = 55000000;  // 55MB
	public static final int MAX_CHUNK = LMMUtils.getMsgFileChunkSizeMB()*1000000;  //Megabytes
	public static final int CHUNK_TIMEOUT_SECS = LMMUtils.getMsgFileTimeoutSecs(); //seconds


	
	/* ***
	 *  Start of specific headers that may be found withing this message
	 ****/

	//chunk number Integers start at 1
	//key points to a data type of Integer
	public static final String HEADER_CHUNK_KEY = "FileChunk";
	//key points to a data type of Integer
	public static final String HEADER_CHUNK_TOTAL_KEY = "FileChunkTotal";

	//key points to a data type of File[]
	public static final String HEADER_FILE_REF = "FileRef";
	
	//key points to a data type of String[]
	public static final String HEADER_THEME_LIST = "ThemeFileList";


    /**
     * @return
     */
    public File getFile() {
        return file;
    }
    
    private void setContent() throws IOException {
    	
		// Get the size of the file
		long length = file.length();
		if( length > MAX_CHUNK )
			return; //don't bother, we will need to multi this
		
		// Create the byte array to hold the data
		content = new byte[ (int)length ];

		int offset = 0;
		int numRead = 0;

		FileInputStream is = null;
		try {
			// Read in the bytes
			is = new FileInputStream( getFile() );
			while (offset < content.length
				   && (numRead=is.read(content, offset, content.length-offset)) >= 0) {
				offset += numRead;
			}
		}
		finally {
			is.close();
		}

		// Ensure all the bytes have been read in
		if (offset < content.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

    }
    
	private synchronized void setContent( int offset, int size ) throws IOException {
    	
		// Create the byte array to hold the data
		content = new byte[ (int)size ];

		RandomAccessFile is = null;
		try {
			// Read in the bytes
			is = new RandomAccessFile( getFile(), "r" );
			is.seek( offset );
			is.read( content, 0, content.length );			
		}
		finally {
			is.close();
		}

	}    

    /**
     * @param file
     */
    public void setFile(File file, boolean createContent ) {
        this.file = file;
        
        try {
	        if( createContent )
	        	setContent();
        }
        catch ( IOException ioe ) {
        	file = null;
        	LMMLogger.error( "Unable to create FileMsg instance", ioe );
        }

    }
    
	public void setFile(File file) {
		setFile( file, true );
	}


	public boolean isFileMultiPart() throws IllegalStateException {    

		if( getFile() == null )
			return false;

		if( getFile().length() > MAX_FILE_SIZE)
			throw new IllegalStateException("File to large, file size = " + getFile().length()
				+ " and that is > " + MAX_FILE_SIZE );


		long length = getFile().length();
		if( length >= MAX_CHUNK ) {
			
			int sz = (int)length / MAX_CHUNK;
			lastChunkSz = (int)length % MAX_CHUNK;
			if( lastChunkSz > 0 )
				sz++;

			multiParts = new FileMsg[ sz ];
			for( int i = 0; i < multiParts.length; i++ ) {
				multiParts[i] = new FileMsg();
				multiParts[i].addHeader( HEADER_CHUNK_KEY, new Integer(i+1) );
				multiParts[i].addHeader( HEADER_CHUNK_TOTAL_KEY, new Integer(multiParts.length) );
				multiParts[i].setFile( getFile(), false );				
			}
			
			return true;
		}
		else
			return false;
	}

	public int getTotalParts() {
		if( getFile() != null && multiParts != null ) {
			return multiParts.length;
		}
		else
			return 0;
	}
	
	
	public FileMsg nextFileMsg() throws IOException {

		if( chunkCurr >= multiParts.length ) {

			multiParts = null;
			lastChunkSz = 0;
			chunkOffset = 0;
			chunkCurr = 0;			

			return null;
		} 
		
		FileMsg chunkMsg = multiParts[ chunkCurr ];
		if( chunkCurr == (multiParts.length -1) ) { //last chunk, special read			
			chunkMsg.setContent( chunkOffset, lastChunkSz );			
		}
		else 
			chunkMsg.setContent( chunkOffset, MAX_CHUNK );


		chunkCurr++;
		chunkOffset += MAX_CHUNK;
		return chunkMsg;	
		
	}
				


    /**
     * @return
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @return
     */
    public boolean isRequest() {
        return request;
    }

    /**
     * @param b
     */
    public void setRequest(boolean b) {
        request = b;
    }

}
