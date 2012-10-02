package com.insta4j.instagram.exception;

import java.io.Serializable;

public class RequestArg implements Serializable {

	private static final long serialVersionUID = 3395597182941431351L;
	
	public String key;
	public String value;

	
	private RequestArg() {
		super();
	}


	public RequestArg(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

}
