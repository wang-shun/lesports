package com.lesports.monitor.cat.constant;

public enum BussinessCode {

	INVALIDE_PARAMS(1, "参数无效"), INTERFACE_ERROR(2, "接口调用出错"),

	NO_PERMISSION(3, "权限不足"), NO_RESPONSE(4, "返回值为空错误")

	;

	private int id;

	private String desc;

	private BussinessCode(int id, String desc) {

		this.id = id;
		this.desc = desc;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
