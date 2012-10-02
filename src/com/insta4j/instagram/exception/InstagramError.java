package com.insta4j.instagram.exception;

import java.io.Serializable;

public class InstagramError implements Serializable {

	private static final long serialVersionUID = -1467641404020908397L;
	
	private int errorCode;
	private String errorMsg;
	private RequestArg[] requestArgs;

	/* Keeping a no args constructor*/
	private InstagramError() {
		super();
	}

	public InstagramError(int errorCode, String errorMsg, RequestArg[] requestArgs) {
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.requestArgs = requestArgs;
	}



	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public RequestArg[] getRequestArgs() {
		return requestArgs;
	}

}
