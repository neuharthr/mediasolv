package com.lmm.systray;

import com.lmm.msg.MonitorMsg;


public interface MonitorListener {

	void setMonitorMsg(MonitorMsg msg);
	
	void startCycleImages();
	void stopCycleImages();
	
}
