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

    // Зберігаємо картинку як масив байтів у базі
    @Lob 
    @Column(columnDefinition = "BLOB") // Для H2 бази даних
    private byte[] imageData;

    private boolean isVisible;

    // Потрібен порожній конструктор для JPA
    public Layer() {}

    public Layer(BufferedImage image) {
        setImage(image);
        this.isVisible = true;
    }

    // --- Магічні методи конвертації ---

    // Коли просимо картинку, перетворюємо байти назад у BufferedImage
    public BufferedImage getImage() {
        if (imageData == null) return null;
        try {
            return ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException e) {
            throw new RuntimeException("Помилка читання картинки з бази", e);
        }
    }

    // Коли зберігаємо картинку, перетворюємо її в байти
    public void setImage(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            this.imageData = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Помилка запису картинки в базу", e);
        }
    }

    // --- Standard Getters/Setters ---
    public Long getId() { return id; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
}