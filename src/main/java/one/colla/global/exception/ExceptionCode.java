package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

	/* 401xx AUTH */
	INVALID_VERIFY_TOKEN(HttpStatus.UNAUTHORIZED, 40101, "인증번호가 일치하지 않거나 인증시간을 초과했습니다."),
	INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 40102, "이메일 또는 비밀번호가 일치하지 않습니다."),
	DUPLICATED_USER_EMAIL(HttpStatus.CONFLICT, 40104, "이미 가입한 메일입니다."),
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40182, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 40183, "사용기간이 만료된 토큰입니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40184, "비정상적인 토큰입니다."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40185, "서명이 조작된 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40186, "지원하지 않는 토큰입니다."),
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40181, "토큰에 접근 권한이 없습니다."),
	TAKEN_AWAY_TOKEN(HttpStatus.FORBIDDEN, 40187, "인증 불가, 관리자에게 문의하세요."),

	/* 499xx ETC */
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 49901, "해당 경로를 찾을 수 없습니다."),

	/* 500xx SERVER */
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 에러 입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}




