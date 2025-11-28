package ia32.eismont.image_editor_server.patterns.prototype;

import ia32.eismont.image_editor_server.entity.GraphicComponent;
import java.util.ArrayList;
import java.util.List;

public class ImageState implements Prototype<ImageState> {

    private List<GraphicComponent> components;

    public ImageState(List<GraphicComponent> components) {
        this.components = components;
    }

    public List<GraphicComponent> getComponents() {
        return components;
    }

    @Override
    public ImageState clone() {
        List<GraphicComponent> deepCopyList = new ArrayList<>();
        
        for (GraphicComponent comp : components) {
            deepCopyList.add(comp.cloneComponent());
        }
        
        return new ImageState(deepCopyList);
    }
}