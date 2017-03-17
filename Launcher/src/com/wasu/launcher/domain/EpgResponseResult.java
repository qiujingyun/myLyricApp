package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class EpgResponseResult  implements Serializable{
	private WasuEpgBody body;
	private WasuEpgHead head;
	public WasuEpgBody getBody() {
		return body;
	}
	
	class WasuEpgHead implements Serializable{
		private String digest;
		private String resultCode;
		private String streamNo;
		private String systemId;
		private String timeStamp;
		
		public String getDigest() {
			return digest;
		}
		public String getResultCode() {
			return resultCode;
		}
		public String getStreamNo() {
			return streamNo;
		}
		public String getSystemId() {
			return systemId;
		}
		public String getTimeStamp() {
			return timeStamp;
		}
		
		
	}
	
}
