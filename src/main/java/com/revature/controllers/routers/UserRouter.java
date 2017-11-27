package com.revature.controllers.routers;

public enum UserRouter {
	LOGIN,
	RESOURCE_NOT_FOUND,
	UPDATE_PASSWORD,
	UPDATE_EMAIL;
	
	public static UserRouter getDelegate(String str) {
		if(str.startsWith("/login")) 		 	return LOGIN;
		if(str.startsWith("/update/password")) 	return UPDATE_PASSWORD;
		if(str.startsWith("/update/email")) 	return UPDATE_EMAIL;
		return RESOURCE_NOT_FOUND;
	}
}
