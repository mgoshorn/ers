package com.revature.exceptions;

public class ForbiddenException extends ErsHTTPException{
	private static final long serialVersionUID = -1227924989929627635L;
	private static final int STATUS_CODE = 403;
	private static final String location = "/static/error/403.html";
	
	public ForbiddenException() {
		super(STATUS_CODE);
	}

	@Override
	public String getLocation() {
		return location;
	}
}
