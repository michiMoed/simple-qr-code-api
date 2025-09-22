package com.micmoe.simple_qr.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.micmoe.simple_qr.Exceptions.QrException;
import com.micmoe.simple_qr.Models.DecodeResult;
import com.micmoe.simple_qr.Services.QrDecodeService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class QrDecodeServiceTest {

    private final QrDecodeService qrDecodeService = new QrDecodeService();

    private MockMultipartFile createQrCodeImageFile(String text) throws Exception {
        // Generate QR code using ZXing (simplified for test)
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return new MockMultipartFile("file", "qr.png", "image/png", imageBytes);
    }

    private String createBase64QrCode(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        return "data:image/png;base64," + base64;
    }

    @Test
    void decodeImageQr_validPng_returnsDecodedResult() throws Exception {
        MockMultipartFile file = createQrCodeImageFile("Hello PNG");

        DecodeResult result = qrDecodeService.decodeImageQr(file);

        assertNotNull(result);
        assertEquals("Hello PNG", result.getData());
        assertEquals("QR_CODE", result.getType());
    }

    @Test
    void decodeBase64Image_validBase64_returnsDecodedResult() throws Exception {
        String base64 = createBase64QrCode("Hello Base64");

        DecodeResult result = qrDecodeService.decodeBase64Image(base64);

        assertNotNull(result);
        assertEquals("Hello Base64", result.getData());
        assertEquals("QR_CODE", result.getType());
    }

    @Test
    void decodeImageQr_invalidContentType_throwsException() {
        MockMultipartFile file = new MockMultipartFile("file", "image.gif", "image/gif", new byte[]{1, 2, 3});

        QrException ex = assertThrows(QrException.class, () -> {
            qrDecodeService.decodeImageQr(file);
        });

        assertTrue(ex.getMessage().contains("Only PNG and JPG images are allowed"));
    }

    @Test
    void decodeBase64Image_invalidBase64_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            qrDecodeService.decodeBase64Image("this is not base64");
        });

        assertTrue(ex.getMessage().contains("Decoding failed"));
    }
}

