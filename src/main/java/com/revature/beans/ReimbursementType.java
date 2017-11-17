package com.revature.beans;

public enum ReimbursementType {
	LODGING(1),
	TRAVEL(2),
	FOOD(3),
	OTHER(4);
	
	private final int value;
	
	private ReimbursementType(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
	
	public static ReimbursementType getByValue(int value) {
		for(ReimbursementType type : ReimbursementType.values()) {
			if(type.value == value) return type;
		}
		return null;
	}
}
