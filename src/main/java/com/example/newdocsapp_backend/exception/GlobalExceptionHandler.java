package com.example.newdocsapp_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorReponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorReponse errorReponse = new ErrorReponse(400, e.getMessage());
        return new ResponseEntity<>(errorReponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorReponse> handleNullPointerException(NullPointerException e) {
        ErrorReponse errorReponse = new ErrorReponse(500, e.getMessage());
        return new ResponseEntity<>(errorReponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorReponse> handleRuntimeException(RuntimeException e) {
        ErrorReponse errorReponse = new ErrorReponse(500, e.getMessage());
        return new ResponseEntity<>(errorReponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorReponse> handleException(Exception e) {
        ErrorReponse errorReponse = new ErrorReponse(500, e.getMessage());
        return new ResponseEntity<>(errorReponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
