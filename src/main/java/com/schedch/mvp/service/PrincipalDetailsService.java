package com.schedch.mvp.service;

import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.model.User;
import com.schedch.mvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * This project uses email to verify user, instead of username
     * @param email the email identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(
                () -> new NotFoundException(String.format("%s: 회원가입 되어있지 않은 이메일입니다."))
        );

        return new PrincipalDetails(user);
    }
}
