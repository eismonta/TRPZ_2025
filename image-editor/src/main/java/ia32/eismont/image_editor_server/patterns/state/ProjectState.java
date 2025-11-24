package ia32.eismont.image_editor_server.patterns.state;

public interface ProjectState {
    void saveChange(String projectName);
    
    String getStateName();
}