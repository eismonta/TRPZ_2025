package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Table(name = "layers")
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob 
    @Column(columnDefinition = "BLOB")
    private byte[] imageData;

    private boolean isVisible;

    public Layer() {}

    public Layer(BufferedImage image) {
        setImage(image);
        this.isVisible = true;
    }

    public BufferedImage getImage() {
        if (imageData == null) return null;
        try {
            return ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException e) {
            throw new RuntimeException("Помилка читання картинки з бази", e);
        }
    }

    public void setImage(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            this.imageData = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Помилка запису картинки в базу", e);
        }
    }

    public Long getId() { return id; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
}