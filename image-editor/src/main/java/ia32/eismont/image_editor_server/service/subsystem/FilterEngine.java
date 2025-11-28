package ia32.eismont.image_editor_server.service.subsystem;

import org.springframework.stereotype.Component;
import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class FilterEngine {

    public void applyFilter(BufferedImage img, String filterType) {
        if (img == null) return;

        switch (filterType) {
            case "grayscale":
                applyGrayscale(img);
                break;
            case "invert":
                applyInvert(img);
                break;
        }
    }

    private void applyGrayscale(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                Color c = new Color(rgb);
                int gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
            }
        }
    }

    private void applyInvert(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));
                img.setRGB(x, y, new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
            }
        }
    }
}