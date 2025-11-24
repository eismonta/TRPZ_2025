package ia32.eismont.image_editor_server.entity;

import java.util.ArrayList;
import java.util.List;

public class LayerGroup {
    private List<Layer> layers;

    public LayerGroup() {
        this.layers = new ArrayList<>();
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public Layer getLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            return layers.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid layer index");
    }

    public List<Layer> getLayers() {
        return layers;
    }
}