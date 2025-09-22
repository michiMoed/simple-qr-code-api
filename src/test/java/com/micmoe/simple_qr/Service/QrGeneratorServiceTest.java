package com.micmoe.simple_qr.Service;

import com.micmoe.simple_qr.Exceptions.QrException;
import com.micmoe.simple_qr.Models.GenerateRequest;
import com.micmoe.simple_qr.Services.QrGeneratorService;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QrGeneratorServiceTest {

    private final QrGeneratorService qrGeneratorService = new QrGeneratorService();

    @Test
    void generateQRCodeImage_validRequest_returnsImage() {
        GenerateRequest request = new GenerateRequest("hello world", 250, "#000000", "#FFFFFF");
        BufferedImage image = qrGeneratorService.generateQRCodeImage(request);

        assertNotNull(image);
        assertEquals(250, image.getWidth());
        assertEquals(250, image.getHeight());
    }

    @Test
    void generateQRCodeImage_nullData_throwsException() {
        GenerateRequest request = new GenerateRequest(null, 250, "#000000", "#FFFFFF");

        QrException ex = assertThrows(QrException.class, () -> {
            qrGeneratorService.generateQRCodeImage(request);
        });

        assertEquals("QR data must not be null or empty.", ex.getMessage());
    }

    @Test
    void generateQRCodeImage_invalidSizeTooSmall_throwsException() {
        GenerateRequest request = new GenerateRequest("test", 10, "#000000", "#FFFFFF");

        QrException ex = assertThrows(QrException.class, () -> {
            qrGeneratorService.generateQRCodeImage(request);
        });

        assertTrue(ex.getMessage().contains("QR size has to be between"));
    }

    @Test
    void generateQRCodeImage_invalidSizeTooBig_throwsException() {
        GenerateRequest request = new GenerateRequest("test", 4000, "#000000", "#FFFFFF");

        QrException ex = assertThrows(QrException.class, () -> {
            qrGeneratorService.generateQRCodeImage(request);
        });

        assertTrue(ex.getMessage().contains("QR size has to be between"));
    }

    @Test
    void generateQRCodeImage_invalidFgColor_throwsException() {
        GenerateRequest request = new GenerateRequest("test", 200, "INVALID", "#FFFFFF");

        QrException ex = assertThrows(QrException.class, () -> {
            qrGeneratorService.generateQRCodeImage(request);
        });

        assertEquals("Foreground color is not valid. Please provide a valid hex color.", ex.getMessage());
    }

    @Test
    void generateQRCodeImage_invalidBgColor_throwsException() {
        GenerateRequest request = new GenerateRequest("test", 200, "#000000", "123456");

        QrException ex = assertThrows(QrException.class, () -> {
            qrGeneratorService.generateQRCodeImage(request);
        });

        assertEquals("Background color is not valid. Please provide a valid hex color.", ex.getMessage());
    }

    @Test
    void generateBase64Image_validRequest_returnsBase64Image() throws Exception {
        GenerateRequest request = new GenerateRequest("hello base64", 250, "#000000", "#FFFFFF");

        Map<String, String> result = qrGeneratorService.generateBase64Image(request);

        assertTrue(result.containsKey("image"));
        String base64Data = result.get("image");
        assertTrue(base64Data.startsWith("data:image/png;base64,"));

        // Decode and verify image content
        String base64 = base64Data.replace("data:image/png;base64,", "");
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        assertNotNull(img);
        assertEquals(250, img.getWidth());
        assertEquals(250, img.getHeight());
    }
}

