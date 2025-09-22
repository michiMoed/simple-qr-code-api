package com.micmoe.simple_qr.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String field;
    private int status;

    public ErrorResponse(String message, String field, int status) {
        this.message = message;
        this.field = field;
        this.status = status;
    }
}

