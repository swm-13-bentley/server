package com.schedch.mvp.service.user;

import com.schedch.mvp.model.User;
import com.schedch.mvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        User saved = userRepository.save(user);
        return saved;
    }

    /**
     * should only call when user existence is already validated
     * ex) user existence validated through jwt token check
     * @param userEmail
     * @return
     */
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).get();
    }
}
