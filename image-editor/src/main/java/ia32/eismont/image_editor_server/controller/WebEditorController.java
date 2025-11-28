package ia32.eismont.image_editor_server.controller;

import ia32.eismont.image_editor_server.dto.EditorResponse;
import ia32.eismont.image_editor_server.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class WebEditorController {

    private final EditorService editorService;

    @Autowired
    public WebEditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<EditorResponse> upload(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            String result = editorService.uploadImage(session.getId(), file);
            return ResponseEntity.ok(new EditorResponse("Uploaded", result));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new EditorResponse("Error", "Upload failed: " + e.getMessage(), true));
        }
    }

    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<EditorResponse> updateLayer(@RequestBody Map<String, String> payload, HttpSession session) {
        try {
            editorService.updateDrawingLayer(session.getId(), payload.get("image"));
            return ResponseEntity.ok(new EditorResponse("Updated", null)); // Картинку назад не шлемо, тільки статус
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(new EditorResponse("Error", e.getMessage(), true));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new EditorResponse("Error", "Update failed", true));
        }
    }

    @PostMapping("/api/process/{action}")
    @ResponseBody
    public ResponseEntity<EditorResponse> process(@PathVariable String action, HttpSession session) {
        try {
            String result = editorService.applyFilter(session.getId(), action);
            return ResponseEntity.ok(new EditorResponse("Processed: " + action, result));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(new EditorResponse("Error", e.getMessage(), true));
        }
    }

    @PostMapping("/api/undo")
    @ResponseBody
    public ResponseEntity<EditorResponse> undo(HttpSession session) {
        try {
            String result = editorService.undoLastAction(session.getId());
            return ResponseEntity.ok(new EditorResponse("Undo Successful", result));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new EditorResponse("Error", e.getMessage(), true));
        }
    }

    @PostMapping("/api/archive")
    @ResponseBody
    public ResponseEntity<EditorResponse> archive(HttpSession session) {
        String msg = editorService.archiveProject(session.getId());
        return ResponseEntity.ok(new EditorResponse("Archived", msg, false)); // msg запишемо в errorMessage або створимо поле message, тут для простоти так
    }
}