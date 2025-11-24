package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LayerGroup extends Layer {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    private List<Layer> children = new ArrayList<>();

    public LayerGroup(String name) {
        super(name);
    }

    public void addLayer(Layer layer) {
        children.add(layer);
    }

    @Override
    public LayerGroup clone() {
        LayerGroup copy = new LayerGroup(this.name + "_copy");
        for (Layer child : children) {
            copy.addLayer(child.clone());
        }
        return copy;
    }
}