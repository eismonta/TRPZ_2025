package ia32.eismont.image_editor_server.patterns.memento;

import ia32.eismont.image_editor_server.patterns.prototype.ImageState;

public class ProjectMemento {
    
    private final ImageState state;

    public ProjectMemento(ImageState state) {
        this.state = state.clone();
    }

    public ImageState getState() {
        return state.clone();
    }
}