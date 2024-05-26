package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

	/* 401xx AUTH */
	INVALID_VERIFY_TOKEN(HttpStatus.UNAUTHORIZED, 40101, "인증번호가 일치하지 않거나 인증시간을 초과했습니다."),
	INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40102, "이메일 또는 비밀번호가 일치하지 않습니다."),
	UNAUTHORIZED_OR_EXPIRED_VERIFY_TOKEN(HttpStatus.UNAUTHORIZED, 40103, "인증된 메일이 아니거나 인증 정보가 만료됐습니다."),
	DUPLICATED_USER_EMAIL(HttpStatus.CONFLICT, 40104, "이미 가입한 메일입니다."),
	INVALID_OAUTH_PROVIDER(HttpStatus.NOT_FOUND, 40105, "지원하지 않는 OAuth 공급자 입니다."),
	INVALID_AUTHORIZATION_CODE(HttpStatus.NOT_FOUND, 40106, "Authorization code가 올바르지 않습니다."),
	SOCIAL_EMAIL_ALREADY_REGISTERED(HttpStatus.UNAUTHORIZED, 40107, "소셜 로그인으로 가입한 이메일 입니다."),
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40181, "토큰에 접근 권한이 없습니다."),
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40182, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 40183, "사용기간이 만료된 토큰입니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40184, "비정상적인 토큰입니다."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40185, "서명이 조작된 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40186, "지원하지 않는 토큰입니다."),
	TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, 40187, "인증 불가, 관리자에게 문의하세요."),

	/* 402xx USER */
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40201, "사용자를 찾을 수 없습니다."),

	/* 403xx TEAMSPACE */
	TEAMSPACE_FULL(HttpStatus.FORBIDDEN, 40301, "팀스페이스 인원이 가득 차있습니다."),
	INVALID_OR_EXPIRED_INVITATION_CODE(HttpStatus.NOT_FOUND, 40302, "유효하지 않거나 만료된 초대코드입니다."),
	ONLY_LEADER_ACCESS(HttpStatus.FORBIDDEN, 40304, "해당 팀스페이스에 대해 관리자 권한이 없습니다."),
	CONFLICT_TAGS(HttpStatus.CONFLICT, 40305, "이미 동일한 이름을 가진 역할이 존재합니다."),
	FAIL_CHANGE_USERTAG(HttpStatus.NOT_FOUND, 40306, "사용자 역할 수정에 실패했습니다."),
	FORBIDDEN_TEAMSPACE(HttpStatus.FORBIDDEN, 40307, "접근 권한이 없거나 존재하지 않는 팀 스페이스입니다."),
	ALREADY_PARTICIPATED(HttpStatus.CONFLICT, 40308, "이미 참가한 사용자입니다."),

	/* FEED-COMMON */
	NOT_FOUND_FEED(HttpStatus.NOT_FOUND, 47101, "접근 권한이 없거나 존재하지 않는 피드입니다."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),
	METHOD_FORBIDDEN(HttpStatus.METHOD_NOT_ALLOWED, 49902, "지원하지 않는 HTTP 메서드를 사용합니다."),

	/* 500xx SERVER */
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 에러 입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}




