package com.revature.beans;


public enum Role {
	EMPLOYEE(1),
	FINANCE_MANAGER(2);
	
	private final int value;
	
	private Role(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

	public static Role getByValue(int value) {
		for(Role role : Role.values()) {
			if(role.value == value) return role;
		}
		return null;
	}
}
