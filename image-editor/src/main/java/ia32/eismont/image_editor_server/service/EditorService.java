package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.LayerGroup;
import ia32.eismont.image_editor_server.entity.Project;
import ia32.eismont.image_editor_server.patterns.memento.HistoryCaretaker;
import ia32.eismont.image_editor_server.patterns.memento.ProjectMemento;
import ia32.eismont.image_editor_server.patterns.prototype.ImageState;
import ia32.eismont.image_editor_server.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final HistoryCaretaker historyCaretaker;

    @Autowired
    public EditorService(ProjectRepository projectRepository, HistoryCaretaker historyCaretaker) {
        this.projectRepository = projectRepository;
        this.historyCaretaker = historyCaretaker;
    }

    @Transactional
    public String uploadImage(String sessionId, MultipartFile file) throws IOException {
        historyCaretaker.clearHistory(sessionId);

        Optional<Project> oldProject = projectRepository.findBySessionId(sessionId);
        oldProject.ifPresent(projectRepository::delete);

        Project project = new Project(sessionId);
        BufferedImage bgImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        
        project.addLayer(new Layer(bgImage));
        BufferedImage transparent = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        project.addLayer(new Layer(transparent));

        projectRepository.save(project);
        return imageToBase64(mergeProjectLayers(project));
    }

    @Transactional
    public void updateDrawingLayer(String sessionId, String base64Image) throws IOException {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        saveStateToHistory(sessionId, project);

        String base64Data = base64Image.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        BufferedImage newDrawing = ImageIO.read(new ByteArrayInputStream(imageBytes));

        project.getLayer(1).setImage(newDrawing);
        projectRepository.save(project);
    }

    @Transactional
    public String applyFilter(String sessionId, String filterType) {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        saveStateToHistory(sessionId, project);

        BufferedImage bg = project.getLayer(0).getImage();
        
        processImage(bg, filterType);
        
        project.getLayer(0).setImage(bg);
        projectRepository.save(project);


        return imageToBase64(bg); 
    }
    
    @Transactional
    public String undoLastAction(String sessionId) {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        ProjectMemento memento = historyCaretaker.undo(sessionId);
        
        if (memento == null) {
            throw new IllegalStateException("Історія порожня, скасування неможливе");
        }

        restoreStateFromMemento(project, memento);
        projectRepository.save(project);
        
        return imageToBase64(mergeProjectLayers(project));
    }

    @Transactional
    public String archiveProject(String sessionId) {
        Project project = getProjectOrThrow(sessionId);
        project.setStatus("ARCHIVED");
        projectRepository.save(project);
        return "Проект архівовано.";
    }


    private void saveStateToHistory(String sessionId, Project project) {
        LayerGroup group = new LayerGroup();
        project.getLayers().forEach(group::addLayer);
        ImageState currentState = new ImageState(group);
        ProjectMemento memento = new ProjectMemento(currentState);
        historyCaretaker.saveState(sessionId, memento);
    }

    private void restoreStateFromMemento(Project project, ProjectMemento memento) {
        ImageState state = memento.getState();
        project.getLayers().clear();
        state.getLayerGroup().getLayers().forEach(project::addLayer);
    }

    private Project getProjectOrThrow(String sessionId) {
        return projectRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private void checkEditable(Project project) {
        if ("ARCHIVED".equals(project.getStatus())) {
            throw new IllegalStateException("Проект в архіві (State Pattern)");
        }
    }

    private BufferedImage mergeProjectLayers(Project project) {
        LayerGroup group = new LayerGroup();
        project.getLayers().forEach(group::addLayer);
        return Compositor.merge(group);
    }

    private void processImage(BufferedImage img, String filterType) {
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