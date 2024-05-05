package one.colla.infra.mail.provider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InviteCodeContentProvider implements MailContentProvider {
	final String teamspaceName;
	final String inviterName;
	final String inviteCode;

	public static InviteCodeContentProvider of(String teamspaceName, String inviterName, String inviteCode) {
		return new InviteCodeContentProvider(teamspaceName, inviterName, inviteCode);
	}

	@Override
	public String getSubject() {
		return "[Colla] 팀스페이스 초대";
	}

	@Override
	public String getContent() {
		return String.format("%s님이 나를 %s 팀스페이스에 초대했습니다.%n초대 링크: https://colla.one/invite?code=%s",
			inviterName, teamspaceName, inviteCode);
	}
}
