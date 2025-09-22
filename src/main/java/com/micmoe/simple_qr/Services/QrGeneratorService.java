package com.micmoe.simple_qr.Services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.micmoe.simple_qr.Exceptions.QrException;
import com.micmoe.simple_qr.Models.GenerateRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static com.micmoe.simple_qr.Utils.ColorUtils.isValidHexColor;
import static com.micmoe.simple_qr.Utils.ColorUtils.parseColor;

@Service
public class QrGeneratorService {
    private final int minQrSize = 30;
    private final int maxQrSize = 3000;

    public BufferedImage generateQRCodeImage(GenerateRequest request) {
        if (request == null || request.getData() == null || request.getData().trim().isEmpty()) {
            throw new QrException("QR data must not be null or empty.");
        }
        if (request.getSize() < minQrSize || request.getSize() > maxQrSize) {
            throw new QrException("QR size has to be between " + minQrSize + " and " + maxQrSize);
        }
        if (!isValidHexColor(request.getFgColor())) {
            throw new QrException("Foreground color is not valid. Please provide a valid hex color.");
        }
        if (!isValidHexColor(request.getBgColor())) {
            throw new QrException("Background color is not valid. Please provide a valid hex color.");
        }

        int size = request.getSize();
        int fgColor = parseColor(request.getFgColor());
        int bgColor = parseColor(request.getBgColor());

        QRCodeWriter barcodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = barcodeWriter.encode(
                    request.getData(),
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );
            MatrixToImageConfig config = new MatrixToImageConfig(fgColor, bgColor);
            return MatrixToImageWriter.toBufferedImage(bitMatrix, config);

        } catch (WriterException e) {
            throw new QrException("Failed to generate QR Code: " + e.getMessage());
        } catch (Exception e) {
            throw new QrException("Unexpected error during QR Code generation.");
        }
    }

    public Map<String, String> generateBase64Image(GenerateRequest request) {
        BufferedImage image = generateQRCodeImage(request);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            baos.flush();
        } catch (IOException e) {
            throw new QrException("Failed to encode QR code image" + e.getMessage());
        }

        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        return Map.of("image", "data:image/png;base64," + base64Image);
    }
}
