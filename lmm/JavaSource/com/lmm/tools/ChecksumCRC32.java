package com.lmm.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class ChecksumCRC32 {

    public static String doChecksum(String fileName) {
        
    	long checksum = -1;
    	
    	try {
            CheckedInputStream cis = null;

            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(
                        new FileInputStream(fileName), new CRC32());
               
            }
            catch (FileNotFoundException e) {
                LMMLogger.error( "File not found.", e);
            }

            byte[] buf = new byte[128];
            while(cis.read(buf) >= 0) {}

            checksum = cis.getChecksum().getValue();

        } catch (IOException e) {
            LMMLogger.error("Error when calculating CRC", e);
        }


        return String.valueOf(checksum);
    }
    
    
    public static void main(String[] args) {
    	System.out.println("val = " + doChecksum("c:/temp/temp.txt"));
    }
}
