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
    private List<GraphicComponent> components = new ArrayList<>();

    private String status = "ACTIVE"; 

    public Project() {}

    public Project(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addComponent(GraphicComponent component) {
        this.components.add(component);
    }

    public GraphicComponent getComponent(int index) {
        return components.get(index);
    }

    public List<GraphicComponent> getComponents() {
        return components;
    }

    public void setComponents(List<GraphicComponent> components) {
        this.components = components;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}