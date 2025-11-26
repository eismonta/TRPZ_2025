package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.LayerGroup;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Compositor {

    public static BufferedImage merge(LayerGroup group) {
        List<Layer> layers = group.getLayers();
        if (layers.isEmpty()) return null;

        int width = layers.get(0).getImage().getWidth();
        int height = layers.get(0).getImage().getHeight();

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        for (Layer layer : layers) {
            if (layer.isVisible() && layer.getImage() != null) {
                g.drawImage(layer.getImage(), 0, 0, null);
            }
        }
        g.dispose();
        return combined;
    }
}