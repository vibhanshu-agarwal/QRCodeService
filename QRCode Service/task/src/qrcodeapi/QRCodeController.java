package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QRCodeController {

  private static final String PNG = "png";
  private static final String JPEG = "jpeg";
  private static final String GIF = "gif";
  private static final List<String> SUPPORTED_TYPES = Arrays.asList(PNG, JPEG, GIF);

  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @GetMapping("/qrcode")
  public ResponseEntity<?> getQRCode(
      @RequestParam(name = "contents", required = true) String contents,
      @RequestParam(required = false, defaultValue = "250") Integer size,
      @RequestParam(required = false, defaultValue = "L") String correction,
      @RequestParam(required = false, defaultValue = "png") String type,
      HttpServletResponse response) {
    if (Objects.isNull(contents) || contents.trim().isEmpty()) {
      return new ResponseEntity<>(
          jsonError("Contents cannot be null or blank"), HttpStatus.BAD_REQUEST);
    }
    // Validate size
    if (size < 150 || size > 350) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(jsonError("Image size must be between 150 and 350 pixels"));
    }
    if (!Arrays.asList("L", "M", "Q", "H").contains(correction)) {
      return new ResponseEntity<>(
          jsonError("Permitted error correction levels are L, M, Q, H"), HttpStatus.BAD_REQUEST);
    }
    // Validate image type
    if (!SUPPORTED_TYPES.contains(type.toLowerCase())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(jsonError("Only png, jpeg and gif image types are supported"));
    }
    // Set content type
    response.setContentType("image/" + type);
    try {
      // Generate and send the QR code image
      byte[] qrCode =
          generateQRCode(
              contents, size, correction, type); // You have to implement generateQRCode method.
      response.getOutputStream().write(qrCode);
    } catch (IOException e) {
      // 500 error response
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(jsonError("Unable to generate QR code image."));
    }
    return ResponseEntity.ok().build();
  }

  private String jsonError(String error) {
    return String.format("{\"error\": \"%s\"}", error);
  }

  // Generate and return the QR code as a byte array
  private byte[] generateQRCode(String contents, int size, String correction, String type) {
    try (var baos = new ByteArrayOutputStream()) {
      QRCodeWriter writer = new QRCodeWriter();

      // Based on correction string set the ErrorCorrectionLevel
      try {
        Map<EncodeHintType, ?> hints =
            switch (correction) {
              case "L" -> Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
              case "M" -> Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
              case "Q" -> Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
              case "H" -> Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
              default -> throw new IllegalStateException("Unexpected value: " + correction);
            };
        BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ImageIO.write(bufferedImage, type, baos); // writing the image in the PNG format
        byte[] bytes = baos.toByteArray();
        return bytes;
      } catch (WriterException e) {
        // handle the WriterException
        // log the exception
        //        e.printStackTrace();
        throw new RuntimeException("Unable to generate QR code image.", e);
      }
    } catch (IOException e) {
      // handle the IOEexception
      // log the exception
      //      e.printStackTrace();
      throw new RuntimeException("Unable to generate QR code image.", e);
    }
  }
}
