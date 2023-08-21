package ru.ptkom.dao;

import org.springframework.stereotype.Component;
import ru.ptkom.model.User;
import ru.ptkom.repository.UserRepository;

import java.util.Optional;

@Component
public class UserDAO {

    private final UserRepository userRepository;

    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        try {
            return userRepository.findById(id).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("Phone user with ID " + id + " not found.");
        }
    }

    public User getByUsername(String username) {
        try {
            return userRepository.findByUsername(username).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("User with username: " + username + " in not exist.");
        }
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAllByOrderByUsername();
    }

    public void saveUser(User user){
        userRepository.findByUsername(user.getUsername())
                .ifPresentOrElse(
                        existedUser -> {throw new RuntimeException("Can't save user " + existedUser.getUsername() + " to data base, user already exists.");},
                        () -> userRepository.save(user)
                );
    }

    public void updateUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(oldUser -> {
            if (oldUser.getId() == user.getId()) {
                userRepository.save(user);
            } else {
                throw new RuntimeException("Can't update, there is no user with name " + user.getUsername());
            }
        });
    }

    public void deleteUser(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Can't delete, id in null.");
        }
    }
}
