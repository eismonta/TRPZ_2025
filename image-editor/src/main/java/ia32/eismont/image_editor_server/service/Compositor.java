package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.LayerGroup;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Compositor {

    // Статичний метод, що приймає Групу Шарів і повертає спільну картинку
    public static BufferedImage merge(LayerGroup group) {
        List<Layer> layers = group.getLayers();
        if (layers.isEmpty()) return null;

        // Розмір беремо по першому шару (фону)
        int width = layers.get(0).getImage().getWidth();
        int height = layers.get(0).getImage().getHeight();

        // Створюємо порожнє полотно
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        // Накладаємо шари по черзі
        for (Layer layer : layers) {
            if (layer.isVisible() && layer.getImage() != null) {
                g.drawImage(layer.getImage(), 0, 0, null);
            }
        }
        g.dispose();
        return combined;
    }
}