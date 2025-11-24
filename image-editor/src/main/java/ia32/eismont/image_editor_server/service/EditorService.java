package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.*;
import ia32.eismont.image_editor_server.patterns.state.*;
import ia32.eismont.image_editor_server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditorService {
    @Autowired
    private ProjectRepository projectRepository;

    // ПАТЕРН STATE: Зберігаємо поточний стан
    private ProjectState currentState;

    public EditorService() {
        // За замовчуванням проєкт активний
        this.currentState = new ActiveState();
    }

    // Метод для зміни стану (викликається з контролера)
    public void changeState(String status) {
        if ("archived".equalsIgnoreCase(status)) {
            this.currentState = new ArchivedState();
        } else {
            this.currentState = new ActiveState();
        }
        System.out.println("--- Стан системи змінено на: " + currentState.getStateName() + " ---");
    }

    public void saveImage(String imageName, String imageData) {
        // 1. Перевіряємо через Патерн State, чи можна зберігати
        // Якщо стан Archived - тут вилетить помилка і код далі не піде
        currentState.saveChange(imageName);

        // 2. Якщо все ок - зберігаємо в БД (стара логіка)
        Project p = new Project(imageName, null);
        LayerGroup root = (LayerGroup) p.getRootLayer();
        root.addLayer(new RasterLayer("Canvas", imageData));
        projectRepository.save(p);
        
        System.out.println("Проєкт успішно збережено в БД.");
    }
}