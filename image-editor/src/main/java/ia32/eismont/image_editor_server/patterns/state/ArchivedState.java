package ia32.eismont.image_editor_server.patterns.state;

public class ArchivedState implements ProjectState {
    @Override
    public void saveChange(String projectName) {
        System.out.println(">>> [Archived State] ПОМИЛКА: Проєкт '" + projectName + "' знаходиться в архіві! Зміни відхилено.");
        throw new IllegalStateException("Проєкт в архіві. Редагування заборонено.");
    }

    @Override
    public String getStateName() {
        return "Archived";
    }
}