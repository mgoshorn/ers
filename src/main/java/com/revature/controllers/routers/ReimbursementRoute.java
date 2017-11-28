package com.revature.controllers.routers;

public enum ReimbursementRoute {
	PENDING,
	CREATE,
	HISTORY,
	DENY,
	APPROVE,
	RECEIPT,
	RESOURCE_NOT_FOUND;
	
	public static ReimbursementRoute getDelegate(String str) {
		if(str.startsWith("/pending")) return PENDING;
		if(str.startsWith("/create")) return CREATE;
		if(str.startsWith("/history")) return HISTORY;
		if(str.startsWith("/deny")) return DENY;
		if(str.startsWith("/approve")) return APPROVE;
		if(str.startsWith("/receipt")) return RECEIPT;
		if(str.equals("") || str.equals("/")) return HISTORY;
		return RESOURCE_NOT_FOUND;
	}
}
