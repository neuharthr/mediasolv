package com.lmm.reports;

/**
 * An exception that is thrown, if a report could not be defined. This
 * encapsulates parse errors as well as runtime exceptions caused by invalid
 * setup code.
 * 
 */
public class ReportDefinitionException extends Exception {
	
	public ReportDefinitionException() {
	}

	public ReportDefinitionException(final String message, final Exception ex) {
		super(message, ex);
	}

	public ReportDefinitionException(final String message) {
		super(message);
	}
}
