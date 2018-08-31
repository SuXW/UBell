package cn.ubia.bean;

import java.io.Serializable;

public class DeviceSearchResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String IP;
	public String UID;
	private int port;

	public DeviceSearchResult() {
	}

	public DeviceSearchResult(String uid, String ip, int port) {
		this.UID = uid;
		this.IP = ip;
		this.port = port;
	}
}