package com.lmm.reports;

import java.io.FileOutputStream;
import java.net.URL;

import org.jfree.layouting.modules.output.pdf.PdfOutputProcessor;
import org.jfree.report.JFreeReport;
import org.jfree.report.JFreeReportBoot;
import org.jfree.report.flow.ReportJob;
import org.jfree.report.flow.streaming.StreamingReportProcessor;
import org.jfree.resourceloader.ResourceManager;
import org.jfree.resourceloader.Resource;

/**
 * Contains some common reporting methods for all reports. For now, this class will be the
 * main entry into JFreeReport that is why the constructor init the JFreeReport Boot instance.
 */
abstract class ReportBaseXMLHandler implements LMMReport {
	
	public ReportBaseXMLHandler() {
		super();
		JFreeReportBoot.getInstance().start();
	}

	protected JFreeReport parseReport() throws ReportDefinitionException {
		final URL in = getReportDefinitionSource();
		if (in == null) {
			throw new ReportDefinitionException("ReportDefinition Source is invalid");
		}

		try {
			ResourceManager manager = new ResourceManager();
			manager.registerDefaults();
			Resource res = manager.createDirectly(in, JFreeReport.class);
			return (JFreeReport) res.getResource();
		} catch (Exception e) {
			throw new ReportDefinitionException("Parsing failed", e);
		}
	}
	
	public void savePDF(String fileName) {

		try {
			ReportJob job = createReport();
			
			final FileOutputStream fout = new FileOutputStream(fileName);
		    final StreamingReportProcessor sp = new StreamingReportProcessor();
		    final PdfOutputProcessor outputProcessor =
		        new PdfOutputProcessor(job.getConfiguration(), fout);
		    sp.setOutputProcessor(outputProcessor);
		    sp.processReport(job);
		    job.close();
		}
	    catch( Exception e ) {
	    	e.printStackTrace( System.out );
	    	//throw new ReportDefinitionException("Unable to save the report as a PDF file", e);
	    }
	}
	
//	protected JComponent createDefaultTable(final TableModel data) {
//		final JTable table = new JTable(data);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//
//		for (int columnIndex = 0; columnIndex < data.getColumnCount(); columnIndex++) {
//			final TableColumn column = table.getColumnModel().getColumn(columnIndex);
//			column.setMinWidth(50);
//			
//			final Class c = data.getColumnClass(columnIndex);
//			if (c.equals(Number.class)) {
//				column.setCellRenderer(new NumberCellRenderer());
//			}
//		}
//
//		return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//	}


}