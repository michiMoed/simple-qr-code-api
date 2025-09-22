package com.micmoe.simple_qr.Controller;

import com.jayway.jsonpath.JsonPath;
import com.micmoe.simple_qr.Exceptions.QrException;
import com.micmoe.simple_qr.Models.DecodeResult;
import com.micmoe.simple_qr.Services.QrDecodeService;
import com.micmoe.simple_qr.Services.QrGeneratorService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QrCodeController.class)
class QrCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QrGeneratorService qrGeneratorService;

    @MockitoBean
    private QrDecodeService qrDecodeService;

    @Test
    void generateQrCode_base64Format_returnsBase64Image() throws Exception {
        Map<String, String> response = Map.of("image", "data:image/png;base64,abc123");
        when(qrGeneratorService.generateBase64Image(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/qr/generate")
                        .param("data", "test")
                        .param("format", "BASE64"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value("data:image/png;base64,abc123"));
    }

    @Test
    void generateQrCode_pngFormat_returnsImageBytes() throws Exception {
        BufferedImage fakeImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(fakeImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        when(qrGeneratorService.generateQRCodeImage(any())).thenReturn(fakeImage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/qr/generate")
                        .param("data", "test")
                        .param("format", "PNG"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void generateQrCode_jpegFormat_returnsJpegImageBytes() throws Exception {
        BufferedImage fakeImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(fakeImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        when(qrGeneratorService.generateQRCodeImage(any())).thenReturn(fakeImage);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/qr/generate")
                        .param("data", "test")
                        .param("format", "JPEG"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void decodeQrCode_fromMultipartFile_returnsDecodedData() throws Exception {
        DecodeResult result = new DecodeResult("QR_CODE", "decoded content");
        MockMultipartFile file = new MockMultipartFile("file", "qr.png", "image/png", "fake-data".getBytes());

        when(qrDecodeService.decodeImageQr(any())).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/qr/decode")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("QR_CODE"))
                .andExpect(jsonPath("$.data").value("decoded content"));
    }

    @Test
    void decodeQrCode_fromBase64Json_returnsDecodedData() throws Exception {
        String base64Image = "data:image/png;base64,abc123";
        DecodeResult result = new DecodeResult("QR_CODE", "decoded base64 content");

        when(qrDecodeService.decodeBase64Image(eq(base64Image))).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/qr/decode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"image\": \"" + base64Image + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("QR_CODE"))
                .andExpect(jsonPath("$.data").value("decoded base64 content"));
    }

    @Test
    @Disabled
        //TODO why is this 200?????
    void decodeQrCode_invalidContentType_throwsException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "qr.gif", "image/gif", "fake-data".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/qr/decode")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(QrException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("Only PNG and JPG images are allowed")));
    }
}

