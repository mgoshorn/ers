package com.revature.beans;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.revature.input.objects.ReimbursementRequest;

public class Reimbursement {
	private BigDecimal amount;
	private Integer id;
	private LocalDateTime submitted;
	private LocalDateTime resolved;
	private String description;
	private User author;
	private User resolver;
	private String receipt;
	private ReimbursementStatus status;
	private ReimbursementType type;
	
	//Should blob be a part of the class?
	//Don't think so - no reason to store an image in memory long term, don't show images by default, but it would be better to have the DAO get them by
	//id when a user requests one
	//THOUGHT: Pop up image in new browser window?
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public LocalDateTime getSubmitted() {
		return submitted;
	}
	public void setSubmitted(LocalDateTime submitted) {
		this.submitted = submitted;
	}
	public LocalDateTime getResolved() {
		return resolved;
	}
	public void setResolved(LocalDateTime resolved) {
		this.resolved = resolved;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public User getResolver() {
		return resolver;
	}
	public void setResolver(User resolver) {
		this.resolver = resolver;
	}
	public ReimbursementStatus getStatus() {
		return status;
	}
	public void setStatus(ReimbursementStatus status) {
		this.status = status;
	}
	public ReimbursementType getType() {
		return type;
	}
	public void setType(ReimbursementType type) {
		this.type = type;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public Reimbursement(BigDecimal amount, Integer id, LocalDateTime submitted, LocalDateTime resolved, String description, User author,
			User resolver, ReimbursementStatus status, ReimbursementType type) {
		super();
		this.amount = amount;
		this.id = id;
		this.submitted = submitted;
		this.resolved = resolved;
		this.description = description;
		this.author = author;
		this.resolver = resolver;
		this.status = status;
		this.type = type;
	}
	
	public Reimbursement() {
		super();
	}
	
	public Reimbursement(User user, ReimbursementRequest reimbRequest) {
		this.setAmount(reimbRequest.getAmount());
		this.setAuthor(user);
		this.setDescription(reimbRequest.getDescription());
		this.setStatus(ReimbursementStatus.PENDING);
		this.setSubmitted(LocalDateTime.now());
		this.setType(ReimbursementType.valueOf(reimbRequest.getType().toUpperCase()));
		this.receipt = reimbRequest.getReceipt();
	}
	@Override
	public String toString() {
		return "Reimbursement [amount=" + amount + ", id=" + id + ", submitted=" + submitted + ", resolved=" + resolved
				+ ", description=" + description + ", author=" + author + ", resolver=" + resolver + ", status="
				+ status + ", type=" + type + "]";
	}

	
	
}
