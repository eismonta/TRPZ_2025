package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
@NoArgsConstructor
public abstract class Layer implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    protected String name;
    protected boolean isVisible = true;
    protected double opacity = 1.0;

    public Layer(String name) {
        this.name = name;
    }

    @Override
    public abstract Layer clone();
}