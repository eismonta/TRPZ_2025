package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.GraphicComponent;
import ia32.eismont.image_editor_server.entity.Layer;
import ia32.eismont.image_editor_server.entity.Project;
import ia32.eismont.image_editor_server.patterns.memento.HistoryCaretaker;
import ia32.eismont.image_editor_server.patterns.memento.ProjectMemento;
import ia32.eismont.image_editor_server.patterns.prototype.ImageState;
import ia32.eismont.image_editor_server.repository.ProjectRepository;
import ia32.eismont.image_editor_server.service.subsystem.FilterEngine;
import ia32.eismont.image_editor_server.service.subsystem.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

@Service
public class EditorService {

    private final ProjectRepository projectRepository;
    private final HistoryCaretaker historyCaretaker;
    private final ImageUtils imageUtils;
    private final FilterEngine filterEngine;

    @Autowired
    public EditorService(ProjectRepository projectRepository, HistoryCaretaker historyCaretaker, ImageUtils imageUtils, FilterEngine filterEngine) {
        this.projectRepository = projectRepository;
        this.historyCaretaker = historyCaretaker;
        this.imageUtils = imageUtils;
        this.filterEngine = filterEngine;
    }

    @Transactional
    public String uploadImage(String sessionId, MultipartFile file) throws IOException {
        historyCaretaker.clearHistory(sessionId);
        Optional<Project> oldProject = projectRepository.findBySessionId(sessionId);
        oldProject.ifPresent(projectRepository::delete);

        Project project = new Project(sessionId);
        BufferedImage bgImage = imageUtils.readImage(file);
        

        project.addComponent(new Layer(bgImage)); 
        
        BufferedImage transparent = new BufferedImage(bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        project.addComponent(new Layer(transparent));

        projectRepository.save(project);
        return imageUtils.encodeToBase64(Compositor.merge(project.getComponents()));
    }

    @Transactional
    public void updateDrawingLayer(String sessionId, String base64Image) throws IOException {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);
        saveStateToHistory(sessionId, project);

        BufferedImage newDrawing = imageUtils.decodeBase64(base64Image);

        project.getComponent(1).setImage(newDrawing);
        
        projectRepository.save(project);
    }

    @Transactional
    public String applyFilter(String sessionId, String filterType) {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);
        saveStateToHistory(sessionId, project);

        BufferedImage bg = project.getComponent(0).getImage();
        filterEngine.applyFilter(bg, filterType);
        project.getComponent(0).setImage(bg);
        
        projectRepository.save(project);
        return imageUtils.encodeToBase64(bg);
    }

    @Transactional
    public String undoLastAction(String sessionId) {
        Project project = getProjectOrThrow(sessionId);
        checkEditable(project);

        ProjectMemento memento = historyCaretaker.undo(sessionId);
        if (memento == null) throw new IllegalStateException("Історія порожня");

        restoreStateFromMemento(project, memento);
        projectRepository.save(project);
        
        return imageUtils.encodeToBase64(Compositor.merge(project.getComponents()));
    }

    @Transactional
    public String archiveProject(String sessionId) {
        Project project = getProjectOrThrow(sessionId);
        project.setStatus("ARCHIVED");
        projectRepository.save(project);
        return "Проект архівовано.";
    }



    private void saveStateToHistory(String sessionId, Project project) {
        ImageState state = new ImageState(project.getComponents());
        historyCaretaker.saveState(sessionId, new ProjectMemento(state));
    }

    private void restoreStateFromMemento(Project project, ProjectMemento memento) {
        ImageState state = memento.getState();
        project.getComponents().clear();
        state.getComponents().forEach(project::addComponent);
    }

    private Project getProjectOrThrow(String sessionId) {
        return projectRepository.findBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Not found"));
    }

    private void checkEditable(Project project) {
        if ("ARCHIVED".equals(project.getStatus())) throw new IllegalStateException("Archived");
    }
}