package ia32.eismont.image_editor_server.patterns.state;

public class ActiveState implements ProjectState {
    @Override
    public void saveChange(String projectName) {
        System.out.println(">>> [Active State] Зміни для проєкту '" + projectName + "' дозволені. Зберігаємо...");
    }

    @Override
    public String getStateName() {
        return "Active";
    }
}