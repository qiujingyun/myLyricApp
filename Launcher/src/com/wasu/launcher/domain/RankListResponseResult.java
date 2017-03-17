package com.wasu.launcher.domain;

import java.io.Serializable;
import java.util.List;

public class RankListResponseResult  implements Serializable{
	private ResponseHead head;
	private RankListResponseBody body;
	public ResponseHead getHead() {
		return head;
	}
	public RankListResponseBody getBody() {
		return body;
	}
	
	
}
