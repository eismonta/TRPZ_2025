package ia32.eismont.image_editor_server.patterns.prototype;

import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.LayerGroup;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageState implements Prototype<ImageState> {

    private LayerGroup layerGroup;

    public ImageState(LayerGroup layerGroup) {
        this.layerGroup = layerGroup;
    }

    public LayerGroup getLayerGroup() {
        return layerGroup;
    }

    @Override
    public ImageState clone() {
        LayerGroup newGroup = new LayerGroup();

        for (Layer layer : layerGroup.getLayers()) {
            BufferedImage original = layer.getImage();
            
            BufferedImage copy = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g = copy.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            newGroup.addLayer(new Layer(copy));
        }

        return new ImageState(newGroup);
    }
}