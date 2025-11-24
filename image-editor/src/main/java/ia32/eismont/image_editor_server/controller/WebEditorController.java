package ia32.eismont.image_editor_server.controller;

import ia32.eismont.image_editor_server.entity.User;
import ia32.eismont.image_editor_server.service.AuthenticationService;
import ia32.eismont.image_editor_server.service.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebEditorController {

    @Autowired private AuthenticationService authService;
    @Autowired private EditorService editorService;

    @GetMapping("/hello")
    public String hello() { return "Server is running!"; }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> data) {
        User user = authService.login(data.get("email"), data.get("password"));
        return (user != null) ? "Login Success: " + user.getUsername() : "Login Failed";
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> data) {
        authService.register(data.get("username"), data.get("email"), data.get("password"));
        return "Registered!";
    }

    // Новий метод для тестування патерна State
    // Виклик: POST /api/state?status=archived
    @PostMapping("/state")
    public String setProjectState(@RequestParam String status) {
        editorService.changeState(status);
        return "Status changed to: " + status;
    }
    
    // Онови метод saveImage, щоб ловити помилку
    @PostMapping("/save")
    public String saveImage(@RequestBody Map<String, String> payload) {
        try {
            editorService.saveImage(payload.get("name"), payload.get("imageData"));
            return "Успіх: Зображення збережено!";
        } catch (IllegalStateException e) {
            // Якщо патерн State заборонив дію
            return "Помилка: " + e.getMessage();
        }
    }
}