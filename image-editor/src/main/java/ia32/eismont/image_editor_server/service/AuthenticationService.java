package ia32.eismont.image_editor_server.service;

import ia32.eismont.image_editor_server.entity.User;
import ia32.eismont.image_editor_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    public User register(String username, String email, String password) {
        return userRepository.save(new User(username, email, password));
    }

    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        return null;
    }
}