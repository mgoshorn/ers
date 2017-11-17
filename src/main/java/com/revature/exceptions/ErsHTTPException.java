package com.revature.exceptions;

import javax.xml.ws.http.HTTPException;

abstract public class ErsHTTPException extends HTTPException {
	private static final long serialVersionUID = 1L;

	public ErsHTTPException(int statusCode) {
		super(statusCode);
	}
	
	public abstract String getLocation();
	
}
