package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.GraphicComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Compositor {

    public static BufferedImage merge(List<GraphicComponent> components) {
        if (components == null || components.isEmpty()) return null;

        BufferedImage base = components.get(0).getImage();
        if (base == null) return null;

        BufferedImage result = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        for (GraphicComponent comp : components) {
            if (comp.isVisible()) {
                BufferedImage img = comp.getImage();
                if (img != null) {
                    g.drawImage(img, 0, 0, null);
                }
            }
        }
        g.dispose();
        return result;
    }
}