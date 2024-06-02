package one.colla.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import one.colla.global.exception.CommonException;

@Slf4j
@ControllerAdvice
public class WebSocketExceptionHandler {

	@MessageExceptionHandler(MethodArgumentNotValidException.class)
	@SendToUser("/queue/errors")
	public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		if (ex.getBindingResult() != null) {
			ex.getBindingResult().getAllErrors().forEach(error -> {
				String fieldName = ((FieldError)error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			});
		}

		log.error("WebSocket ValidationException 발생: {}", errors);
		return errors;
	}

	@MessageExceptionHandler(CommonException.class)
	@SendToUser("/queue/errors")
	public String handleCommonException(CommonException ex) {
		log.error("WebSocket CommonException 발생: {}", ex.getMessage(), ex);
		return ex.getMessage();
	}

	@MessageExceptionHandler(Exception.class)
	@SendToUser("/queue/errors")
	public String handleException(Exception ex) {
		log.error("Unexpected WebSocket error 발생: {}", ex.getMessage(), ex);
		return "Unexpected error occurred";
	}
}
