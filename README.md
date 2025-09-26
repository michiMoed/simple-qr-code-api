# Simple QR Code API

A lightweight REST API for generating and decoding QR codes.  
Built with Spring Boot and exposed via Swagger UI for easy exploration.

## 🚀 Features
- Generate QR codes in **PNG**, **JPEG**, or **Base64** format
- Customize **size**, **foreground color**, and **background color**
- Decode QR codes from **uploaded image files** or **Base64 strings**
- Explore endpoints via integrated **Swagger UI**

## 📖 API Documentation
Swagger UI is available at:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🔧 Endpoints

### 1. Generate QR Code
`GET /api/qr/generate`

**Parameters (query):**
- `data` *(required)* → Text or URL to encode
- `size` *(optional, default=200)* → Image size in pixels
- `fgColor` *(optional, default=#000000)* → Foreground color (hex)
- `bgColor` *(optional, default=#FFFFFF)* → Background color (hex)
- `format` *(optional, default=PNG)* → Output format: `PNG`, `JPEG`, or `BASE64`

**Responses:**
- `image/png` or `image/jpeg` → QR code image
- `application/json` → Base64-encoded string (if `format=BASE64`)

**Example Request (Base64 output):**
```bash
    curl -X POST "http://localhost:8080/api/qr/generate?data=HelloWorld&size=300&format=BASE64"
```

### 2. Decode QR Code (File Upload)
`POST /api/qr/decode`  
Consumes: `multipart/form-data`

**Form Data:**
- `file` *(required)* → QR code image file (`.png` or `.jpg`)

**Response:**
```json
{
  "type": "string",
  "data": "string"
}
```
**Example Request (Base64 output):**
```bash
    curl -X POST "http://localhost:8080/api/qr/decode" \
    -F "file=@qrcode.png"
```

### 3. Decode QR Code (Base64 JSON)
`POST /api/qr/decode`  
Consumes: `application/json`

**Request Body:**
```json
{
  "image": "BASE64_ENCODED_IMAGE"
}
```
**Example Request (Base64 output):**
```bash
    curl -X POST "http://localhost:8080/api/qr/decode" \
      -H "Content-Type: application/json" \
      -d '{"image": "BASE64_ENCODED_IMAGE"}'

```

## 🛠️ Tech Stack
- **Java 17+**
- **Spring Boot**
- **Swagger UI** for API documentation
---

The API will be available at:
👉 http://localhost:8080

Swagger UI:
👉 http://localhost:8080/swagger-ui/index.html