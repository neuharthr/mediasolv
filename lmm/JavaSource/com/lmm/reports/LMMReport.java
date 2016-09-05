package com.lmm.reports;

import java.net.URL;
import org.jfree.report.flow.ReportJob;

public interface LMMReport {

	public URL getReportDefinitionSource();

	public ReportJob createReport() throws ReportDefinitionException;
}
