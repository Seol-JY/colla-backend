package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
	/* 401_UNAUTHORIZED */
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40182, "토큰이 포함되어 있지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, 40183, "사용기간이 만료된 토큰입니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, 40184, "비정상적인 토큰입니다."),
	TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, 40185, "서명이 조작된 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 40186, "지원하지 않는 토큰입니다."),

	/* 403_FORBIDDEN */
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40181, "토큰에 접근 권한이 없습니다."),

	/* 500_INTERNAL_SERVER_ERROR */
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 에러 입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}




