package ia32.eismont.image_editor_server.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Layer> layers = new ArrayList<>();

    private String status = "ACTIVE"; 

    public Project() {}

    public Project(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
    }

    public Layer getLayer(int index) {
        return layers.get(index);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}