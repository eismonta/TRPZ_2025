package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import java.awt.image.BufferedImage;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "component_type")
@Table(name = "components")
public abstract class GraphicComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isVisible = true;

    public abstract BufferedImage getImage();
    
    public abstract void setImage(BufferedImage image);

    public abstract GraphicComponent cloneComponent();

    public Long getId() { return id; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
}