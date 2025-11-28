package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("GROUP")
public class LayerGroup extends GraphicComponent {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<GraphicComponent> children = new ArrayList<>();

    public void add(GraphicComponent component) {
        children.add(component);
    }

    public void remove(GraphicComponent component) {
        children.remove(component);
    }

    public List<GraphicComponent> getChildren() {
        return children;
    }

    @Override
    public BufferedImage getImage() {
        if (children.isEmpty()) return null;

        BufferedImage base = children.get(0).getImage();
        if (base == null) return null;

        BufferedImage result = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        for (GraphicComponent child : children) {
            if (child.isVisible()) {
                BufferedImage childImg = child.getImage();
                if (childImg != null) {
                    g.drawImage(childImg, 0, 0, null);
                }
            }
        }
        g.dispose();
        return result;
    }

    @Override
    public void setImage(BufferedImage image) {
        throw new UnsupportedOperationException("Groups cannot have raw image data.");
    }

    @Override
    public GraphicComponent cloneComponent() {
        LayerGroup copyGroup = new LayerGroup();
        for (GraphicComponent child : children) {
            copyGroup.add(child.cloneComponent());
        }
        copyGroup.setVisible(this.isVisible());
        return copyGroup;
    }
}