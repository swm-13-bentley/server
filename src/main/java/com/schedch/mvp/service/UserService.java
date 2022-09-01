package com.schedch.mvp.service;

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
}
