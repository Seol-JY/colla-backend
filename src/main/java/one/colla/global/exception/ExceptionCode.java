package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
	/* 401_UNAUTHORIZED */
	INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40102, "이메일 또는 비밀번호가 일치하지 않습니다."),
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40182, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 40183, "사용기간이 만료된 토큰입니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40184, "비정상적인 토큰입니다."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40185, "서명이 조작된 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40186, "지원하지 않는 토큰입니다."),

	INVALID_OR_EXPIRED_INVITATION_CODE(HttpStatus.NOT_FOUND, 40302, "유효하지 않거나 만료된 초대코드입니다."),

	/* 403_FORBIDDEN */
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40181, "토큰에 접근 권한이 없습니다."),
	TAKEN_AWAY_TOKEN(HttpStatus.UNAUTHORIZED, 40187, "인증 불가, 관리자에게 문의하세요."),

	/* 500_INTERNAL_SERVER_ERROR */
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 에러 입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}




