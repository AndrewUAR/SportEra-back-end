package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.Map;

@Data
public class HttpResponse {

    private int status;

    private String message;

    private Map<String, String> validationErrors;

    public HttpResponse(int httpStatusCode, String message) {
        this.status = httpStatusCode;
        this.message = message;
    }
}
