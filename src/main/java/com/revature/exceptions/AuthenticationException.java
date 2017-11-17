package com.revature.exceptions;

public class AuthenticationException extends ErsHTTPException {
	private static final String location = "/static/error/404.html";
	private static final long serialVersionUID = -4373027551699457066L;
	public static final int STATUS_CODE = 401;
	
	public AuthenticationException() {
		super(STATUS_CODE);
	}

	@Override
	public String getLocation() {
		return location;
	}
}
