package com.revature.exceptions;

import javax.servlet.http.HttpServletResponse;

public class ResourceNotFoundException extends ErsHTTPException {
	private static final String location = "/static/error/404.html";
	private static final long serialVersionUID = -4373027551699457066L;
	
	public ResourceNotFoundException() {
		super(HttpServletResponse.SC_NOT_FOUND); //404
	}

	@Override
	public String getLocation() {
		return location;
	}

}
