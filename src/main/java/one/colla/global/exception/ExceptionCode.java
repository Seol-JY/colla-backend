package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
	/* 401_UNAUTHORIZED */
	EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 40182, "토큰이 포함되어있지 않습니다."),

	/* 403_FORBIDDEN */
	FORBIDDEN_ACCESS_TOKEN(HttpStatus.FORBIDDEN, 40181, "토큰에 접근 권한이 없습니다."),

	/* 500_INTERNAL_SERVER_ERROR */
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "서버 에러 입니다.");

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;
}
