package net.miarma.api.microservices.huertos.entities;

import java.util.List;

public class ProfileDTO {
	private MemberEntity member;
	private List<RequestEntity> requests;
	private List<IncomeEntity> payments;
	private boolean hasCollaborator;
	private boolean hasCollaboratorRequest;
	private boolean hasGreenHouse;
	private boolean hasGreenHouseRequest;
	public MemberEntity getMember() {
		return member;
	}
	public void setMember(MemberEntity member) {
		this.member = member;
	}
	public List<RequestEntity> getRequests() {
		return requests;
	}
	public void setRequests(List<RequestEntity> requests) {
		this.requests = requests;
	}
	public List<IncomeEntity> getPayments() {
		return payments;
	}
	public void setPayments(List<IncomeEntity> payments) {
		this.payments = payments;
	}
	public boolean isHasCollaborator() {
		return hasCollaborator;
	}
	public void setHasCollaborator(boolean hasCollaborator) {
		this.hasCollaborator = hasCollaborator;
	}
	public boolean isHasCollaboratorRequest() {
		return hasCollaboratorRequest;
	}
	public void setHasCollaboratorRequest(boolean hasCollaboratorRequest) {
		this.hasCollaboratorRequest = hasCollaboratorRequest;
	}
	public boolean isHasGreenHouse() {
		return hasGreenHouse;
	}
	public void setHasGreenHouse(boolean hasGreenHouse) {
		this.hasGreenHouse = hasGreenHouse;
	}
	public boolean getHasGreenHouseRequest() {
		return hasGreenHouseRequest;
	}
        public void setHasGreenHouseRequest(boolean hasGreenHouseRequest) {
                this.hasGreenHouseRequest = hasGreenHouseRequest;
        }
	
	
}
