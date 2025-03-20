package aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdempotentException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIdempotentException(IdempotentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.toErrorResponse());
    }
}

