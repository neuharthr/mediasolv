package com.lmm.reports;

import java.net.URL;
import java.util.Date;

import org.jfree.report.flow.DefaultReportJob;
import org.jfree.report.flow.ReportJob;

import org.jfree.report.JFreeReport;
import org.jfree.report.TableReportDataFactory;
import org.jfree.util.ObjectUtilities;

/**
 * A report for creating POP rutimes for daily performance
 * 
 */
public class POPDailyMetricReport extends ReportBaseXMLHandler {

	private POPDailyMetricTableModel popMetricModel;

	public POPDailyMetricReport() {
		this( new Date(), null );
	}

	public POPDailyMetricReport( final Date start, final Date end ) {
		popMetricModel = new POPDailyMetricTableModel( start, end );
	}

	/**
	 * Creates the report. For XML reports, this will most likely call the
	 * ReportGenerator, while API reports may use this function to build and
	 * return a new, fully initialized report object.
	 * 
	 * @return the fully initialized JFreeReport object.
	 * @throws org.jfree.report.demo.util.ReportDefinitionException
	 *             if an error occured preventing the report definition.
	 */
	public ReportJob createReport() throws ReportDefinitionException {
		final JFreeReport report = parseReport();
		report.getInputParameters().put("PlayerName", "Plaza");
		
		DefaultReportJob job = new DefaultReportJob(report);
		final TableReportDataFactory dataFactory = new TableReportDataFactory(
				"default", popMetricModel);
		job.setDataFactory(dataFactory);
		return job;
	}


	/**
	 * Returns the URL of the XML definition for this report.
	 * 
	 * @return the URL of the report definition.
	 */
	public URL getReportDefinitionSource() {
		return ObjectUtilities.getResourceRelative("popDailyMetric.xml", POPDailyMetricReport.class);
	}

//	public static void main(String[] args) throws ReportDefinitionException {
//		JFreeReportDemoBoot.getInstance().start();
//		final WorldDemoHandler demoHandler = new WorldDemoHandler();
//		final ReportJob report = demoHandler.createReport();
//
//	}
}
