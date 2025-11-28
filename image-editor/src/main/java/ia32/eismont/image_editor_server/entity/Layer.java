package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@DiscriminatorValue("LEAF")
public class Layer extends GraphicComponent {

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] imageData;

    public Layer() {}

    public Layer(BufferedImage image) {
        setImage(image);
    }

    @Override
    public BufferedImage getImage() {
        if (imageData == null) return null;
        try {
            return ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException e) {
            throw new RuntimeException("Error reading image", e);
        }
    }

    @Override
    public void setImage(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            this.imageData = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }

    @Override
    public GraphicComponent cloneComponent() {
        Layer copy = new Layer();
        if (this.imageData != null) {
            copy.imageData = this.imageData.clone();
        }
        copy.setVisible(this.isVisible());
        return copy;
    }
}