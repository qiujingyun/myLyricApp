package com.wasu.launcher.entity;


import java.util.List;
public class ExampleBean{
	private Body body;

	public Body getBody() {
		return this.body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public ExampleBean() {}

	public ExampleBean(Body body){
		super();
		this.body = body;
	}
	public class Body{
		private List<CountyList> countyList;
		private List<ProviceList> proviceList;
		private String desc;
		private String defaultAreaCode;
		private List<CityList> cityList;
		private long result;

		public List<CountyList> getCountyList() {
			return this.countyList;
		}

		public void setCountyList(List<CountyList> countyList) {
			this.countyList = countyList;
		}

		public List<ProviceList> getProviceList() {
			return this.proviceList;
		}

		public void setProviceList(List<ProviceList> proviceList) {
			this.proviceList = proviceList;
		}

		public String getDesc() {
			return this.desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getDefaultAreaCode() {
			return this.defaultAreaCode;
		}

		public void setDefaultAreaCode(String defaultAreaCode) {
			this.defaultAreaCode = defaultAreaCode;
		}

		public List<CityList> getCityList() {
			return this.cityList;
		}

		public void setCityList(List<CityList> cityList) {
			this.cityList = cityList;
		}

		public long getResult() {
			return this.result;
		}

		public void setResult(long result) {
			this.result = result;
		}

		public Body() {}

		public Body(List<CountyList> countyList, List<ProviceList> proviceList, String desc, String defaultAreaCode, List<CityList> cityList, long result){
			super();
			this.countyList = countyList;
			this.proviceList = proviceList;
			this.desc = desc;
			this.defaultAreaCode = defaultAreaCode;
			this.cityList = cityList;
			this.result = result;
		}
		public class CityList{
			private long orderBy;
			private String areaCode;
			private String areaParentId;
			private String areaName;
			private String areaKey;

			public long getOrderBy() {
				return this.orderBy;
			}

			public void setOrderBy(long orderBy) {
				this.orderBy = orderBy;
			}

			public String getAreaCode() {
				return this.areaCode;
			}

			public void setAreaCode(String areaCode) {
				this.areaCode = areaCode;
			}

			public String getAreaParentId() {
				return this.areaParentId;
			}

			public void setAreaParentId(String areaParentId) {
				this.areaParentId = areaParentId;
			}

			public String getAreaName() {
				return this.areaName;
			}

			public void setAreaName(String areaName) {
				this.areaName = areaName;
			}

			public String getAreaKey() {
				return this.areaKey;
			}

			public void setAreaKey(String areaKey) {
				this.areaKey = areaKey;
			}

			public CityList() {}

			public CityList(long orderBy, String areaCode, String areaParentId, String areaName, String areaKey){
				super();
				this.orderBy = orderBy;
				this.areaCode = areaCode;
				this.areaParentId = areaParentId;
				this.areaName = areaName;
				this.areaKey = areaKey;
			}

			public String toString() {
				return "CityList [orderBy = " + orderBy + ", areaCode = " + areaCode + ", areaParentId = " + areaParentId + ", areaName = " + areaName + ", areaKey = " + areaKey + "]";
			}
		}
		public class ProviceList{
			private long orderBy;
			private String areaCode;
			private String areaParentId;
			private String areaName;
			private String areaKey;

			public long getOrderBy() {
				return this.orderBy;
			}

			public void setOrderBy(long orderBy) {
				this.orderBy = orderBy;
			}

			public String getAreaCode() {
				return this.areaCode;
			}

			public void setAreaCode(String areaCode) {
				this.areaCode = areaCode;
			}

			public String getAreaParentId() {
				return this.areaParentId;
			}

			public void setAreaParentId(String areaParentId) {
				this.areaParentId = areaParentId;
			}

			public String getAreaName() {
				return this.areaName;
			}

			public void setAreaName(String areaName) {
				this.areaName = areaName;
			}

			public String getAreaKey() {
				return this.areaKey;
			}

			public void setAreaKey(String areaKey) {
				this.areaKey = areaKey;
			}

			public ProviceList() {}

			public ProviceList(long orderBy, String areaCode, String areaParentId, String areaName, String areaKey){
				super();
				this.orderBy = orderBy;
				this.areaCode = areaCode;
				this.areaParentId = areaParentId;
				this.areaName = areaName;
				this.areaKey = areaKey;
			}

			public String toString() {
				return "ProviceList [orderBy = " + orderBy + ", areaCode = " + areaCode + ", areaParentId = " + areaParentId + ", areaName = " + areaName + ", areaKey = " + areaKey + "]";
			}
		}
		public class CountyList{
			private String bossAreaCode;
			private String areaCode;
			private String areaName;
			private String userProfile;
			private String orderBy;
			private String areaKey;
			private String areaParentId;

			public String getBossAreaCode() {
				return this.bossAreaCode;
			}

			public void setBossAreaCode(String bossAreaCode) {
				this.bossAreaCode = bossAreaCode;
			}

			public String getAreaCode() {
				return this.areaCode;
			}

			public void setAreaCode(String areaCode) {
				this.areaCode = areaCode;
			}

			public String getAreaName() {
				return this.areaName;
			}

			public void setAreaName(String areaName) {
				this.areaName = areaName;
			}

			public String getUserProfile() {
				return this.userProfile;
			}

			public void setUserProfile(String userProfile) {
				this.userProfile = userProfile;
			}

			public String getOrderBy() {
				return this.orderBy;
			}

			public void setOrderBy(String orderBy) {
				this.orderBy = orderBy;
			}

			public String getAreaKey() {
				return this.areaKey;
			}

			public void setAreaKey(String areaKey) {
				this.areaKey = areaKey;
			}

			public String getAreaParentId() {
				return this.areaParentId;
			}

			public void setAreaParentId(String areaParentId) {
				this.areaParentId = areaParentId;
			}

			public CountyList() {}

			public CountyList(String bossAreaCode, String areaCode, String areaName, String userProfile, String orderBy, String areaKey, String areaParentId){
				super();
				this.bossAreaCode = bossAreaCode;
				this.areaCode = areaCode;
				this.areaName = areaName;
				this.userProfile = userProfile;
				this.orderBy = orderBy;
				this.areaKey = areaKey;
				this.areaParentId = areaParentId;
			}

			public String toString() {
				return "CountyList [bossAreaCode = " + bossAreaCode + ", areaCode = " + areaCode + ", areaName = " + areaName + ", userProfile = " + userProfile + ", orderBy = " + orderBy + ", areaKey = " + areaKey + ", areaParentId = " + areaParentId + "]";
			}
		}

		public String toString() {
			return "Body [countyList = " + countyList + ", proviceList = " + proviceList + ", desc = " + desc + ", defaultAreaCode = " + defaultAreaCode + ", cityList = " + cityList + ", result = " + result + "]";
		}
	}

	public String toString() {
		return "ExampleBean [body = " + body + "]";
	}
}


