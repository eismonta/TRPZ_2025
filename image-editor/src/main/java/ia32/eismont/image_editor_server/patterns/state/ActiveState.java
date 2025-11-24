package ia32.eismont.image_editor_server.patterns.state;

public class ActiveState implements ProjectState {
    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public String getName() {
        return "Active (Editable)";
    }
}