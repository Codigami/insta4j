package com.insta4j.instagram.exception;

import java.io.Serializable;

public class GenericError implements Serializable {

	private static final long serialVersionUID = 6231581588988314593L;
	
	private String type;
	private String message;

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

}
