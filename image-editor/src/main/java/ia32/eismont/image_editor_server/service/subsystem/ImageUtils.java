package ia32.eismont.image_editor_server.service.subsystem;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Component
public class ImageUtils {

    public BufferedImage readImage(MultipartFile file) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    }

    public BufferedImage decodeBase64(String base64Image) throws IOException {
        String base64Data = base64Image.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }

    public String encodeToBase64(BufferedImage image) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Помилка конвертації зображення", e);
        }
    }
}