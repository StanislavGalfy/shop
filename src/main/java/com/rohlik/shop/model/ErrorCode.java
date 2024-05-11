package com.rohlik.shop.model;

public class ErrorCode {

    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";

    public static final String BAD_REQUEST = "bad_request";

    public static final String PRODUCT_NOT_FOUND = "product_not_found";

    public static final String ORDER_NOT_FOUND = "order_not_found";

    public static final String ORDER_CANCELED = "order_canceled";

    public static final String ORDER_PAYED = "order_payed";

    private ErrorCode() {
        super();
    }
}
