package com.micmoe.simple_qr.Services;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.micmoe.simple_qr.Exceptions.QrException;
import com.micmoe.simple_qr.Models.DecodeResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
public class QrDecodeService {
    public DecodeResult decodeImageQr(MultipartFile file) {
        try {
            BufferedImage image = null;

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
                    throw new QrException("Only PNG and JPG images are allowed.");
                }
                image = ImageIO.read(file.getInputStream());
            }

            return decode(image);
        } catch (Exception e) {
            throw new QrException("Decoding failed: " + e.getMessage());
        }

    }

    public DecodeResult decodeBase64Image(String data) {
        try {
            BufferedImage image;
            byte[] imageBytes = Base64.getDecoder().decode(
                    data.replaceAll("^data:image/[^;]+;base64,", "")
            );
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(bis);

            return decode(image);
        } catch (Exception e) {
            throw new RuntimeException("Decoding failed: " + e.getMessage());
        }
    }

    private DecodeResult decode(BufferedImage image) {
        if (image == null) {
            throw new QrException("No valid image provided.");
        }

        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);

            return new DecodeResult(result.getBarcodeFormat().toString(), result.getText());
        } catch (Exception e) {
            throw new QrException(e.getMessage());
        }
    }
}
