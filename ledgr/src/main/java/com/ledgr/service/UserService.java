package com.ledgr.service;

import com.ledgr.entity.User;
import com.ledgr.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean emailTaken(String email) {
        return userRepo.existsByEmail(email);
    }

    public User register(String name, String email, String rawPassword) {
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User u = new User(name.trim(), email.trim().toLowerCase(), hashed);
        return userRepo.save(u);
    }

    public Optional<User> tryLogin(String email, String rawPassword) {
        Optional<User> maybeUser = userRepo.findByEmail(email.trim().toLowerCase());
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        User u = maybeUser.get();
        if (!BCrypt.checkpw(rawPassword, u.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(u);
    }

    public boolean checkPassword(User u, String rawPassword) {
        return BCrypt.checkpw(rawPassword, u.getPassword());
    }

    public User updateName(User u, String newName) {
        u.setName(newName.trim());
        return userRepo.save(u);
    }

    public void changePassword(User u, String newRawPassword) {
        u.setPassword(BCrypt.hashpw(newRawPassword, BCrypt.gensalt()));
        userRepo.save(u);
    }

    public void deleteAccount(User u) {
        userRepo.delete(u);
    }

    public User save(User u) {
        return userRepo.save(u);
    }
}
