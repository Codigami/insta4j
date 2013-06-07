package com.insta4j.instagram.exception;

public class InstagramException extends Exception {

	private static final long serialVersionUID = -5976082894590280746L;
	
	private InstagramError error;

  public InstagramException(int errorCode, String msg, String type, Exception exception) {
      super(msg, exception);
	  this.error = new InstagramError(errorCode, msg, type, null);
  }

  public InstagramException(InstagramError error) {
		super();
		this.error = error;
	}

	public InstagramError getError() {
		return error;
	}

	public void setError(InstagramError error) {
		this.error = error;
	}
	
	@Override
	public String getMessage() {
		return (this.getError() == null) ? super.getMessage() : this.getError().getErrorMsg();
	}
	
	@Override
	public String getLocalizedMessage() {
		return this.getMessage();
	}

}