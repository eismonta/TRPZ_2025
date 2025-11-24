package ia32.eismont.image_editor_server.patterns.state;

public class ArchivedState implements ProjectState {
    @Override
    public boolean canEdit() {
        return false;
    }

    @Override
    public String getName() {
        return "Archived (Read-only)";
    }
}