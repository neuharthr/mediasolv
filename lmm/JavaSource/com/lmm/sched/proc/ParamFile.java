package com.lmm.sched.proc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @deprecated not used
 */
public class ParamFile {
    private Vector parameterPairs = null;
    private final String EQUALS_DELIMITER = "=";
    private boolean parametersSuccessfull = true;
    private String fileName = null;

    public ParamFile(String parameterFileName) {
        super();
        fileName = parameterFileName;
        if( fileName == null )
        	throw new IllegalArgumentException("Need fileName parameter to be non null");

        init();
    }

    public int getParameterCount() {
        if (parameterPairs == null)
            return 0;
        else
            return parameterPairs.size();
    }

    private int getParameterLocation(String parameterName) throws IllegalArgumentException {
        if (parameterPairs == null)
            return -1;

        for (int i = 0; i < getParameterCount(); i++) {
            if (getToken(parameterPairs.elementAt(i).toString(), "NAME")
                .equalsIgnoreCase(parameterName))
                return i;
        }

        // parameter not found
        throw new IllegalArgumentException(
            "Unable to find file parameter: " + parameterName);
    }

    private String getParameterNameAt(int location) throws IllegalArgumentException {
        if (parameterPairs == null
            || location < 0
            || location >= getParameterCount())
            throw new IllegalArgumentException(
                "Unable to find file parameter name at location " + location);
        else
            return getToken(
                parameterPairs.elementAt(location).toString(),
                "NAME");
    }

    public String getParameterValue( String parameterName, String defaultValue) {
        if (parameterPairs == null)
            return defaultValue;

        for (int i = 0; i < getParameterCount(); i++) {
            if (getToken(parameterPairs.elementAt(i).toString(), "NAME")
                .equalsIgnoreCase(parameterName))
                return getToken(
                    parameterPairs.elementAt(i).toString(),
                    "VALUE");
        }

        return defaultValue;
    }

	public String getParameterValue( String parameterName ) {
		return getParameterValue( parameterName, null );
	}

    private void updateValue(String paramName_, String value_)
        throws IOException {
        boolean fnd = false;

        for (int i = 0; i < getParameterCount(); i++) {
            if (getParameterNameAt(i)
                .toString()
                .equalsIgnoreCase(paramName_)) {
                parameterPairs.setElementAt(
                    getParameterNameAt(i) + EQUALS_DELIMITER + value_,
                    i);

                fnd = true;
            }

        }

        if (!fnd) {
            parameterPairs.add(paramName_ + EQUALS_DELIMITER + value_);
        }

    }

    private String getParameterValueAt(int location, String defaultValue) {
        if (parameterPairs == null
            || location < 0
            || location >= getParameterCount())
            return defaultValue;
        else
            return getToken(
                parameterPairs.elementAt(location).toString(),
                "VALUE");
    }

    private Object[] getParameterValues() {
        if (parameterPairs == null)
            return null;

        return parameterPairs.toArray();
    }

    private String getToken(String value, String location) {
        if (location == null)
            return null;

        StringTokenizer tokenizer =
            new StringTokenizer(value, EQUALS_DELIMITER);

        try {
            if (location.equalsIgnoreCase("NAME")) {
                return tokenizer.nextToken();
            }
            else if (location.equalsIgnoreCase("VALUE")) {
                tokenizer.nextToken();
                return tokenizer.nextToken();
            }
            else
                return null;
        }
        catch (java.util.NoSuchElementException ex) {
            return "";
        }

    }

    private void init() {
        parametersSuccessfull = parseInputFile();
    }

    public boolean parametersExisted() {
        return parametersSuccessfull;
    }

    private boolean parseInputFile() {
        java.io.RandomAccessFile file = null;

        parameterPairs = new Vector(getParameterCount());

        java.io.File checkFile = new java.io.File(fileName);

        try {
            // open file		
            if (checkFile.exists()) {
                file = new java.io.RandomAccessFile(checkFile, "r");

                long filePointer = 0;
                long length = file.length();

                while (filePointer < length) // loop until the end of the file
                    {

                    String line = file.readLine(); // read a line in

                    if (line.indexOf(EQUALS_DELIMITER) <= 0)
                        return false;
                    //no delimiter found, this must be a corrupt file

                    parameterPairs.addElement(line);

                    // set our pointer to the new position in the file
                    filePointer = file.getFilePointer();
                }
            }
            else
                return false;

            // Close file
            file.close();
        }
        catch (IOException ex) {
            return false;
        }
        finally {
            try {
                if (checkFile.exists())
                    file.close();
            }
            catch (IOException ex) {
            }
        }

        return true;
    }

    public boolean updateValues(String[] names_, String[] values_) {
        boolean retValue = false;

        //ignore the bad ones
        if (names_ == null || values_ == null
            	|| (names_.length != values_.length))
            return false;

        try {
            FileWriter writer = new FileWriter(fileName);

            for (int i = 0; i < names_.length; i++)
                updateValue(names_[i], values_[i]);

            //write the values back to the file
            for (int i = 0; i < getParameterCount(); i++)
                writer.write(
                    parameterPairs.get(i).toString()
                        + System.getProperty("line.separator"));

            writer.close();
            retValue = true; // successfull
        }
        catch (IOException e) {
            e.printStackTrace( System.err );
        }

        return retValue; //success or not
    }

}
