package com.lmm.tools;

import java.text.DecimalFormat;


public class Timer {

	private long start = 0l;
	private String msg = null;
	private static DecimalFormat df = new DecimalFormat("##0.000");

	public Timer() {
		this( "  Timed event =" );
	}

	public Timer(String msg) {
		super();
		this.start = System.currentTimeMillis();
		this.msg = msg;
	}

	public String printTotal() {
		return msg + " " + df.format(System.currentTimeMillis() - start) + "  (millis)";	
	}
	
}
