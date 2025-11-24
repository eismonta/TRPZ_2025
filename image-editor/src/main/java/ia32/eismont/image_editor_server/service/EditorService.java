package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.LayerGroup;
import ia32.eismont.image_editor_server.entity.Project;
import ia32.eismont.image_editor_server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
public class EditorService {

    private final ProjectRepository projectRepository;

    @Autowired
    public EditorService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // Завантаження: Створюємо запис у БД
    @Transactional
    public String uploadImage(String sessionId, MultipartFile file) throws IOException {
        // Видаляємо старий проект цієї сесії, якщо був
        Optional<Project> oldProject = projectRepository.findBySessionId(sessionId);
        oldProject.ifPresent(projectRepository::delete);

        // Створюємо новий
        Project project = new Project(sessionId);
        
        BufferedImage bgImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        
        // Шар 0: Фон
        project.addLayer(new Layer(bgImage));
        
        // Шар 1: Прозорий для малювання
        BufferedImage transparent = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        project.addLayer(new Layer(transparent));

        // Зберігаємо в базу!
        projectRepository.save(project);

        return imageToBase64(mergeProjectLayers(project));
    }

    @Transactional
    public void updateDrawingLayer(String sessionId, String base64Image) throws IOException {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        String base64Data = base64Image.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        BufferedImage newDrawing = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // Оновлюємо Шар 1 і зберігаємо зміни в базу
        project.getLayer(1).setImage(newDrawing);
        projectRepository.save(project);
    }

    @Transactional
    public String applyFilter(String sessionId, String filterType) {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        // Беремо фон (Шар 0)
        BufferedImage bg = project.getLayer(0).getImage();
        
        // Застосовуємо фільтр
        processImage(bg, filterType);
        
        // Зберігаємо змінений фон назад у базу
        project.getLayer(0).setImage(bg);
        projectRepository.save(project);

        return imageToBase64(mergeProjectLayers(project));
    }

    @Transactional
    public String archiveProject(String sessionId) {
        Project project = getProjectOrThrow(sessionId);
        project.setStatus("ARCHIVED");
        projectRepository.save(project);
        return "Проект збережено в базу і архівовано.";
    }

    // --- Допоміжні методи ---

    private Project getProjectOrThrow(String sessionId) {
        return projectRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private void checkEditable(Project project) {
        if ("ARCHIVED".equals(project.getStatus())) {
            throw new IllegalStateException("Проект в архіві (Database)");
        }
    }

    // Це наш аналог Compositor, тільки прямо тут для зручності
    private BufferedImage mergeProjectLayers(Project project) {
        // Конвертуємо Entity Layers у LayerGroup для сумісності з логікою Compositor
        LayerGroup group = new LayerGroup();
        project.getLayers().forEach(group::addLayer);
        return Compositor.merge(group);
    }

    private void processImage(BufferedImage img, String filterType) {
        // Стандартна логіка фільтрів (grayscale/invert) з минулого разу
        switch (filterType) {
            case "grayscale":
                for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        int rgb = img.getRGB(x, y);
                        Color color = new Color(rgb);
                        int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
                    }
                }
                break;
            case "invert":
                 for (int x = 0; x < img.getWidth(); x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        int rgb = img.getRGB(x, y);
                        Color color = new Color(rgb);
                        img.setRGB(x, y, new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB());
                    }
                }
                break;
        }
    }

    private String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) { throw new RuntimeException(e); }
    }
}