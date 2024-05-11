package com.rohlik.shop.controller;

import com.rohlik.shop.exception.ApiException;
import com.rohlik.shop.model.ErrorCode;
import com.rohlik.shop.model.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class ShopExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDTO> handleApiException(ApiException e) {
        log.warn("API Exception: {}", e.getMessage());
        val errorResponse = ErrorResponseDTO.builder()
                .error(e.getErrorCode())
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Method argument not not valid exception occurred: {}", e.getMessage());
        return ErrorResponseDTO.builder()
                .error(ErrorCode.BAD_REQUEST)
                .errorDescription("Request is invalid")
                .build();
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.warn("Handler method validation exception occurred: {}", e.getMessage());
        return ErrorResponseDTO.builder()
                .error(ErrorCode.BAD_REQUEST)
                .errorDescription("Request is invalid")
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleInternalServerError(Throwable e) {
        log.error("Internal server error occurred", e);
        return ErrorResponseDTO.builder()
                .error(ErrorCode.INTERNAL_SERVER_ERROR)
                .errorDescription("Internal server error")
                .build();
    }
}
