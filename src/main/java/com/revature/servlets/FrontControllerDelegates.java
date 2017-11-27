package com.revature.servlets;

public enum FrontControllerDelegates {
	STATIC,
	REIMBURSEMENT,
	USER,
	LOGOUT,
	RESOURCE_NOT_FOUND;
	
	public static FrontControllerDelegates getDelegate(String str) {
		if(str.startsWith("/static")) 		 return STATIC;
		if(str.startsWith("/reimbursement")) return REIMBURSEMENT;
		if(str.startsWith("/user")) 		 return USER;
		if(str.startsWith("/logout"))		 return LOGOUT;
		return RESOURCE_NOT_FOUND;
	}
}
