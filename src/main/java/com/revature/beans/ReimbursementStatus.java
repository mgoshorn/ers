package com.revature.beans;

public enum ReimbursementStatus {
	PENDING(1),
	APPROVED(2),
	DENIED(3);
	
	private final int value;
	
	private ReimbursementStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}


	public static ReimbursementStatus getByValue(int value) {
		for(ReimbursementStatus status : ReimbursementStatus.values()) {
			if(status.value == value) return status;
		}
		return null;
	}
}
