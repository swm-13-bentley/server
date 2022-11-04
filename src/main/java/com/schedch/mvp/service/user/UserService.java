package com.schedch.mvp.service.user;

import com.schedch.mvp.config.JwtConfig;
import com.schedch.mvp.dto.user.mypage.MyEmailPatchReq;
import com.schedch.mvp.dto.user.mypage.MyEmailRes;
import com.schedch.mvp.model.Token;
import com.schedch.mvp.model.User;
import com.schedch.mvp.repository.TokenRepository;
import com.schedch.mvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtConfig jwtConfig;

    public User save(User user) {
        User saved = userRepository.save(user);
        return saved;
    }

    public void signOutUser(String userEmail) {
        User user = getUserByEmail(userEmail);

        user.detachFromParticipant();
        user.setSignedOut(true);
        tokenRepository.deleteByEmail(userEmail);
    }

    public String changeUsername(String userEmail, String newUsername) {
        User user = getUserByEmail(userEmail);
        user.setUsername(newUsername);

        String newAccessToken = jwtConfig.createAccessTokenByUser(user);
        Token userJwtToken = tokenRepository.findByEmail(userEmail).get();
        userJwtToken.setAccessToken(newAccessToken);

        return newAccessToken;
    }

    public List<MyEmailRes> getUserEmailList(String userEmail) {
        User user = getUserByEmail(userEmail);

        String alarmRegisteredEmail = user.getEmail();
        boolean receiveEmail = user.isReceiveEmail();

        List<MyEmailRes> myEmailResList = new ArrayList<>();
        MyEmailRes myEmailRes = new MyEmailRes(alarmRegisteredEmail, receiveEmail);

        myEmailResList.add(myEmailRes);
        return myEmailResList;
    }


    public void changeUserEmailReceiveStatus(String userEmail, MyEmailPatchReq myEmailPatchReq) {
        User user = getUserByEmail(userEmail);

        boolean receiveEmail = myEmailPatchReq.isReceiveEmail();
        user.setReceiveEmail(receiveEmail);
    }

    /**
     * should only call when user existence is already validated
     * ex_ user existence validated through jwt token check
     * @param userEmail
     * @return
     */
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).get();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).get();
    }


}
