package com.rohlik.shop.exception;

import com.rohlik.shop.model.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String errorCode;

    private ApiException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public static ApiException productNotFound(Long productId) {
        return new ApiException("Product with ID '" + productId + "' was not found", HttpStatus.NOT_FOUND,
                ErrorCode.PRODUCT_NOT_FOUND);
    }

    public static ApiException orderNotFound(Long orderId) {
        return new ApiException("Order with ID '" + orderId + "' was not found", HttpStatus.NOT_FOUND,
                ErrorCode.ORDER_NOT_FOUND);
    }

    public static ApiException orderAlreadyCanceled(Long orderId) {
        return new ApiException("Error updating order '" + orderId + "'. Order is already canceled",
                HttpStatus.BAD_REQUEST, ErrorCode.ORDER_CANCELED);
    }

    public static ApiException orderAlreadyPayed(Long orderId) {
        return new ApiException("Error updating order '" + orderId + "'. Order is already payed",
                HttpStatus.BAD_REQUEST, ErrorCode.ORDER_PAYED);
    }
}
