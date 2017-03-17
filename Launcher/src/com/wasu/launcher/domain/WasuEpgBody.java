package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class WasuEpgBody implements Serializable{
	private List<WasuScheduleList> scheduleList;

	public List<WasuScheduleList> getScheduleList() {
		return scheduleList;
	}
	

}
