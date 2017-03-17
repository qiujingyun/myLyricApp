package com.wasu.launcher.onlinebusinesshall.business;

import java.io.Serializable;
import java.util.List;

public class BusinessResult implements Serializable {

	private int code;
	private List<BusinessData> data;

	// "description":"OK",
	// "identification":"f00d7138-720c-467d-b098-7fb473b7aecf",
	// "jsonP":false,
	// "properties":{},
	// "sourceId":0

	public class BusinessData implements Serializable {
		// "ageing": 31,
		// "autoPrice": 1,
		// "autoSub": 1,
		// "categoryBizId": "40814315811460000001283",
		// "description":
		// "包含电影“VIP专区”中所有影视内容\r\n1.订购服务开通即时扣费，服务即时生效；\r\n2.更多详询400-635-9988，最终解释权归华数所有。",
		// "logoPath": "http://s.wasu.tv/mams/pic/201412/31/08/VIP_ysyb.png",
		// "name": "VIP影视包月",
		// "planBizId": "40414189714680000002551",
		// "price": 1,
		// "priority": 1,
		// "siteId": "99999",
		// "type": 3
		private int ageing;
		private int autoPrice;
		private int autoSub;
		private int price;
		private int priority;
		private int type;
		private String categoryBizId;
		// private String description;
		private String logoPath;
		private String name;
		private String planBizId;
		private String siteId;

		public int getAgeing() {
			return ageing;
		}

		public void setAgeing(int ageing) {
			this.ageing = ageing;
		}

		public int getAutoPrice() {
			return autoPrice;
		}

		public void setAutoPrice(int autoPrice) {
			this.autoPrice = autoPrice;
		}

		public int getAutoSub() {
			return autoSub;
		}

		public void setAutoSub(int autoSub) {
			this.autoSub = autoSub;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getCategoryBizId() {
			return categoryBizId;
		}

		public void setCategoryBizId(String categoryBizId) {
			this.categoryBizId = categoryBizId;
		}

		//
		// public String getDescription() {
		// return description;
		// }
		//
		// public void setDescription(String description) {
		// this.description = description;
		// }

		public String getLogoPath() {
			return logoPath;
		}

		public void setLogoPath(String logoPath) {
			this.logoPath = logoPath;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPlanBizId() {
			return planBizId;
		}

		public void setPlanBizId(String planBizId) {
			this.planBizId = planBizId;
		}

		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}

	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<BusinessData> getData() {
		return data;
	}

	public void setData(List<BusinessData> data) {
		this.data = data;
	}

}
