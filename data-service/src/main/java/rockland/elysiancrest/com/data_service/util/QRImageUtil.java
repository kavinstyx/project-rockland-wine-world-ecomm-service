package rockland.elysiancrest.com.data_service.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class QRImageUtil {

    // Define constants for better maintainability
    private static final int QR_CODE_WIDTH = 200;
    private static final int QR_CODE_HEIGHT = 200;
    private static final String IMAGE_FORMAT = "PNG";

    /**
     * Generates a Base64 encoded QR code image for the provided content.
     *
     * @param content the content to encode in the QR code
     * @return a Base64 encoded string representing the QR code image
     * @throws WriterException if an error occurs while generating the QR code
     * @throws IOException     if an error occurs while writing the image
     */
    public static String generateQRCodeBase64(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        // Using try-with-resources to ensure ByteArrayOutputStream is properly released
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrCodeBytes);
        }
    }
}
