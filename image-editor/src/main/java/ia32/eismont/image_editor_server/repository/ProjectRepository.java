package ia32.eismont.image_editor_server.repository;

import ia32.eismont.image_editor_server.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Знайти проект по ID сесії браузера
    Optional<Project> findBySessionId(String sessionId);
}