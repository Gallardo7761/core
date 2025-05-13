package net.miarma.api.huertos.services;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import net.miarma.api.common.Constants.HuertosUserStatus;
import net.miarma.api.common.exceptions.UnauthorizedException;
import net.miarma.api.common.security.JWTManager;
import net.miarma.api.huertos.entities.ProfileDTO;

public class ProfileService {
	private final MemberService memberService;
	private final RequestService requestService;
	private final IncomeService incomeService;
	
	public ProfileService(Pool pool) {
		this.memberService = new MemberService(pool);
		this.requestService = new RequestService(pool);
		this.incomeService = new IncomeService(pool);
	}
	
	public Future<ProfileDTO> getProfile(String token) {
		Integer userId = JWTManager.getInstance().getUserId(token);
    	ProfileDTO dto = new ProfileDTO();
    	
    	return memberService.getById(userId).compose(member -> {
			if (member.getStatus() == HuertosUserStatus.INACTIVE) {
				return Future.failedFuture(new UnauthorizedException("Member is inactive"));
			}
			
			dto.setMember(member);
			
			return Future.all(
				requestService.getMyRequests(token),
				incomeService.getMyIncomes(token),
				memberService.hasCollaborator(token),
				requestService.hasCollaboratorRequest(token),
				memberService.hasGreenHouse(token),
				requestService.hasGreenHouseRequest(token)
			).map(f -> {
				dto.setRequests(f.resultAt(0));
				dto.setPayments(f.resultAt(1));
				dto.setHasCollaborator(f.resultAt(2));
				dto.setHasCollaboratorRequest(f.resultAt(3));
				dto.setHasGreenHouse(f.resultAt(4));
				dto.setHasGreenHouseRequest(f.resultAt(5));
				return dto;
			});
			
		});
	}
}
