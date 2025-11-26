package ia32.eismont.image_editor_server.patterns.memento;

import org.springframework.stereotype.Component;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@Component
public class HistoryCaretaker {

    private final Map<String, Deque<ProjectMemento>> history = new HashMap<>();

    public void saveState(String sessionId, ProjectMemento memento) {
        history.computeIfAbsent(sessionId, k -> new ArrayDeque<>()).push(memento);
        
        if (history.get(sessionId).size() > 10) {
            history.get(sessionId).removeLast();
        }
    }

    public ProjectMemento undo(String sessionId) {
        Deque<ProjectMemento> stack = history.get(sessionId);
        if (stack != null && !stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }
    
    public void clearHistory(String sessionId) {
        if (history.containsKey(sessionId)) {
            history.get(sessionId).clear();
        }
    }
}