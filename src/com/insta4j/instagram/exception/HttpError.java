package com.insta4j.instagram.exception;

import java.io.Serializable;

public class HttpError implements Serializable{

	private static final long serialVersionUID = 8466069092420823708L;
	
	private GenericError error;

	public GenericError getError() {
		return error;
	}
	
}
