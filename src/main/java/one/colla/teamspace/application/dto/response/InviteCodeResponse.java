package one.colla.teamspace.application.dto.response;

import one.colla.infra.redis.invite.InviteCode;

public record InviteCodeResponse(
	String inviteCode
) {
	public static InviteCodeResponse from(final InviteCode inviteCode) {
		return new InviteCodeResponse(inviteCode.getCode());
	}
}
