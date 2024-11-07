package one.colla.infra.mail.provider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InviteCodeContentProvider implements MailContentProvider {
	private static final String TEMPLATE_PATH = "templates/invite-email-template.html";

	private static final String EMAIL_TEMPLATE = loadTemplate();

	private final String teamspaceName;
	private final String inviterName;
	private final String inviteCode;
	private final String teamspaceImageUrl;
	private final int memberCount;

	private static String loadTemplate() {
		try {
			var classLoader = InviteCodeContentProvider.class.getClassLoader();
			try (var inputStream = classLoader.getResourceAsStream(TEMPLATE_PATH)) {
				if (inputStream == null) {
					throw new IllegalStateException("이메일 템플릿 파일을 찾을 수 없습니다: " + TEMPLATE_PATH);
				}
				return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			throw new IllegalStateException("이메일 템플릿을 로드하는데 실패했습니다", e);
		}
	}

	public static InviteCodeContentProvider of(
		String teamspaceName,
		String inviterName,
		String inviteCode,
		String teamspaceImageUrl,
		int memberCount
	) {
		return new InviteCodeContentProvider(
			teamspaceName,
			inviterName,
			inviteCode,
			teamspaceImageUrl,
			memberCount
		);
	}

	@Override
	public String getSubject() {
		return "[Colla] 팀스페이스 초대";
	}

	private String getTeamspaceInitial() {
		return teamspaceName.substring(0, 1);
	}

	private String getWorkspaceIconHtml() {
		StringBuilder style = new StringBuilder();
		style.append("width: 52px;")
			.append("height: 48px;")
			.append("border-radius: 6px;")
			.append("text-align: center;")
			.append("vertical-align: middle;")
			.append("border: 1px solid #e5e5e5;");

		if (teamspaceImageUrl != null && !teamspaceImageUrl.isEmpty()) {
			String imgStyle = "width: 48px;"
				+ "height: 48px;"
				+ "border-radius: 6px;"
				+ "object-fit: cover;";

			return String.format("<td style=\"%s\"><img src=\"%s\" alt=\"팀스페이스 아이콘\" style=\"%s\"/></td>",
				style,
				teamspaceImageUrl,
				imgStyle);
		}

		style.append("background-color: #f1f3f5;")
			.append("color: #495057;")
			.append("font-weight: 500;")
			.append("font-size: 18px;");

		return String.format("<td style=\"%s\">%s</td>",
			style,
			getTeamspaceInitial());
	}

	@Override
	public String getContent() {
		return generateEmailContent();
	}

	private String generateEmailContent() {
		return String.format(
			EMAIL_TEMPLATE,
			inviterName,
			teamspaceName,
			inviteCode,
			getWorkspaceIconHtml(),
			teamspaceName,
			memberCount
		);
	}
}
