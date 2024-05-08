package one.colla.auth.application.dto.response;

public record LoginResponse(
	String accessToken,
	Long userId,
	boolean hasTeam
) {
	public static LoginResponse of(String accessToken, Long userId, boolean hasTeam) {
		return new LoginResponse(accessToken, userId, hasTeam);
	}
}
