package com.micmoe.simple_qr.Controller;

import com.micmoe.simple_qr.Services.QrDecodeService;
import com.micmoe.simple_qr.Services.QrGeneratorService;
import com.micmoe.simple_qr.Models.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/qr")
public class QrCodeController {
    private final QrGeneratorService qrGeneratorService;
    private final QrDecodeService qrDecodeService;

    public QrCodeController(QrGeneratorService qrGeneratorService, QrDecodeService qrDecodeService) {
        this.qrGeneratorService = qrGeneratorService;
        this.qrDecodeService = qrDecodeService;
    }

    @GetMapping(value = "/generate")
    public ResponseEntity<?> generate(
            @RequestParam String data,
            @RequestParam(required = false, defaultValue = "200") int size,
            @RequestParam(required = false, defaultValue = "#000000") String fgColor,
            @RequestParam(required = false, defaultValue = "#FFFFFF") String bgColor,
            @RequestParam(required = false, defaultValue = "PNG") OutputFormat format
    ) {
        GenerateRequest request = new GenerateRequest(data, size, fgColor, bgColor);
        if (format == OutputFormat.BASE64) {
            return ResponseEntity.ok(qrGeneratorService.generateBase64Image(request));
        }

        return ResponseEntity
                .ok()
                .contentType(format == OutputFormat.JPEG ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG)
                .body(qrGeneratorService.generateQRCodeImage(request));
    }

    @PostMapping(value = "/decode", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DecodeResult> decode(
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(qrDecodeService.decodeImageQr(file));
    }

    @PostMapping(value = "/decode", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DecodeResult> decode(
            @RequestBody DecodeRequest decodeRequest
    ) {
        return ResponseEntity.ok(qrDecodeService.decodeBase64Image(decodeRequest.getImage()));
    }
}
