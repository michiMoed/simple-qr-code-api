package com.micmoe.simple_qr.Models;

import lombok.Getter;

@Getter
public class DecodeResult {
    String type;
    String data;

    public DecodeResult(String type, String data) {
        this.type = type;
        this.data = data;
    }
}
