package com.schedch.mvp.service.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.schedch.mvp.config.ErrorMessage;
import com.schedch.mvp.dto.oauth.GoogleLoginDto;
import com.schedch.mvp.mapper.UserMapper;
import com.schedch.mvp.model.Token;
import com.schedch.mvp.model.User;
import com.schedch.mvp.model.UserCalendar;
import com.schedch.mvp.repository.TokenRepository;
import com.schedch.mvp.repository.UserRepository;
import com.schedch.mvp.service.user.UserCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OAuthService {

    private final GoogleProfileService googleProfileService;
    private final KakaoProfileService kakaoProfileService;
    private final UserCalendarService userCalendarService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;

    public User googleSignIn(String authCode) throws FailedLoginException, JsonProcessingException {
        log.info("P: googleSignIn / authCode = {}", authCode);
        GoogleLoginDto googleLoginDto = googleProfileService.getGoogleProfile(authCode, true);
        User mappedUser = userMapper.googleLoginDto2User(googleLoginDto);

        String email = mappedUser.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()) { //existing user
            User user = userOptional.get();

            if(user.isSignedOut()) { //탈퇴했던 유저 정보라면 다시 복구
                user.setSignedOut(false);
            }

            int originalGrants = user.getScope().split(" ").length;
            int newGrants = googleLoginDto.getScope().split(" ").length;

            //권한 축소됨 (캘린더 권한 상실)
            if(newGrants < originalGrants) {
                List<UserCalendar> userCalendarList = user.getUserCalendarList();
                int idx = -1;
                for (int i = 0; i < userCalendarList.size(); i++) {
                    UserCalendar userCalendar = userCalendarList.get(i);
                    if (userCalendar.getCalendarEmail().equals(googleLoginDto.getEmail())) {
                        idx = i;
                    }
                }
                if (idx != -1) {
                    userCalendarList.remove(idx);
                }
                user.setScope(googleLoginDto.getScope()); //축소된 권한으로 저장
            }

            //권한 확대
            if(newGrants > originalGrants) {
                user.setScope(googleLoginDto.getScope());
                if(newGrants == 4) { //캘린더 권한까지 확보됨
                    user.setScope(googleLoginDto.getScope());
                    userCalendarService.addCalendarToUser(googleLoginDto, user);
                }
            }

            return user;
        }

        //user is new
        String[] scopes = googleLoginDto.getScope().split(" ");
        if (scopes.length == 4) {
            userCalendarService.addCalendarToUser(googleLoginDto, mappedUser);
        }

        User user = userRepository.save(mappedUser);
        log.info("S: googleSignIn / user signs in / userId = {}", user.getId());
        return user;
    }



    public String googleSignOut(String authCode) throws FailedLoginException, JsonProcessingException {
        log.info("P: googleSignOut / authCode = {}", authCode);

        GoogleLoginDto googleLoginDto = googleProfileService.getGoogleProfile(authCode, false);
        User mappedUser = userMapper.googleLoginDto2User(googleLoginDto);

        String email = mappedUser.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty() == true) { //user for this email doesn't exist
            log.warn("E: googleSignOut / this email is not signed in / userEmail = {}", email);
            throw new IllegalArgumentException(ErrorMessage.notSignedInEmail(email));
        }

        User user = userOptional.get();

        user.detachFromParticipant();
        userRepository.delete(user);
        tokenRepository.deleteByEmail(email);

        return email;
    }

//    public User kakaoSignIn(String authCode) throws JsonProcessingException, ParseException {
//        KakaoProfileRes kakaoProfileRes = kakaoProfileService.getKakaoProfileRes(authCode, true);
//        User mappedUser = userMapper.kakaoProfileRes2User(kakaoProfileRes);
//
//        String email = mappedUser.getEmail();
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if(userOptional.isEmpty() == false) { //user for this email exists
//            return userOptional.get();
//        }
//
//        User user = userRepository.save(mappedUser);
//        return user;
//    }
//
//    public String kakaoSignOut(String authCode) throws ParseException, JsonProcessingException {
//        KakaoProfileRes kakaoProfileRes = kakaoProfileService.getKakaoProfileRes(authCode, false);
//        User mappedUser = userMapper.kakaoProfileRes2User(kakaoProfileRes);
//
//        String email = mappedUser.getEmail();
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if(userOptional.isEmpty() == true) { //user for this email doesn't exist
//            throw new IllegalArgumentException(String.format("%s: 회원가입 되어있지 않은 이메일입니다.", email));
//        }
//
//        User user = userOptional.get();
//        user.detachFromParticipant();
//        userRepository.delete(user);
//
//        return email;
//    }

    public void saveToken(String email, String accessToken, String refreshToken) {
        Optional<Token> byEmail = tokenRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            Token token = new Token(email, accessToken, refreshToken);
            tokenRepository.save(token);
            return;
        }

        Token token = byEmail.get();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

    }

    public void deleteToken(String email) {
        Optional<Token> byEmail = tokenRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            Token token = byEmail.get();
            tokenRepository.delete(token);
        }
    }

}
