package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RasterLayer extends Layer {
    
    @Lob
    @Column(length = 1000000) 
    private String imageData; 

    public RasterLayer(String name, String imageData) {
        super(name);
        this.imageData = imageData;
    }

    @Override
    public RasterLayer clone() {
        RasterLayer clone = new RasterLayer(this.name + "_copy", this.imageData);
        clone.setVisible(this.isVisible);
        clone.setOpacity(this.opacity);
        return clone;
    }
}