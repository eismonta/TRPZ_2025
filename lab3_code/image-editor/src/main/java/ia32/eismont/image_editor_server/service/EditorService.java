package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.*;
import ia32.eismont.image_editor_server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditorService {
    @Autowired
    private ProjectRepository projectRepository;

    public void saveImage(String imageName, String imageData) {
        // Для демо зберігаємо "фіктивний" проєкт з 1 шаром
        Project p = new Project(imageName, null);
        LayerGroup root = (LayerGroup) p.getRootLayer();
        root.addLayer(new RasterLayer("Canvas", imageData));
        projectRepository.save(p);
        System.out.println("Проєкт збережено в БД: " + imageName);
    }
}