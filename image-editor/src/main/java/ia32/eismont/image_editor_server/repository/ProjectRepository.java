package ia32.eismont.image_editor_server.repository;

import ia32.eismont.image_editor_server.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findBySessionId(String sessionId);
}