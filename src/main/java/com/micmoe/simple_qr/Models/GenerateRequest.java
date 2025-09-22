package com.micmoe.simple_qr.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateRequest {
    private String data;
    private int size;
    private String fgColor;
    private String bgColor;

    public GenerateRequest(String data, int size, String fgColor, String bgColor) {
        this.data = data;
        this.size = size;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }
}
