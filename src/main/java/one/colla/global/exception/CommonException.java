package one.colla.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final int errorCode;
	private final String message;

	public CommonException(ExceptionCode ex) {
		this.httpStatus = ex.getHttpStatus();
		this.errorCode = ex.getErrorCode();
		this.message = ex.getMessage();
	}
}
