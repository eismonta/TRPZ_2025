package ia32.eismont.image_editor_server.controller;

import ia32.eismont.image_editor_server.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
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
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            String result = editorService.uploadImage(session.getId(), file);
            return ResponseEntity.ok(Collections.singletonMap("image", result));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Upload failed"));
        }
    }

    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<String> updateLayer(@RequestBody Map<String, String> payload, HttpSession session) {
        try {
            editorService.updateDrawingLayer(session.getId(), payload.get("image"));
            return ResponseEntity.ok("Layer updated");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Update failed");
        }
    }

    @PostMapping("/api/process/{action}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> process(@PathVariable String action, HttpSession session) {
        try {
            String result = editorService.applyFilter(session.getId(), action);
            return ResponseEntity.ok(Collections.singletonMap("image", result));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/api/undo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> undo(HttpSession session) {
        try {
            String result = editorService.undoLastAction(session.getId());
            return ResponseEntity.ok(Collections.singletonMap("image", result));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/api/archive")
    @ResponseBody
    public ResponseEntity<String> archive(HttpSession session) {
        String msg = editorService.archiveProject(session.getId());
        return ResponseEntity.ok(msg);
    }
}