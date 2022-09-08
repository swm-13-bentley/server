package com.schedch.mvp.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.schedch.mvp.config.JwtConfig;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.model.Token;
import com.schedch.mvp.model.User;
import com.schedch.mvp.repository.TokenRepository;
import com.schedch.mvp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.webjars.NotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Optional;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JwtConfig jwtConfig;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, TokenRepository tokenRepository, JwtConfig jwtConfig) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(jwtConfig.getHEADER_STRING());
        if (header == null || !header.startsWith(jwtConfig.getTOKEN_PREFIX())) {
            chain.doFilter(request, response);
            return;
        }

        String inputAccessToken = request.getHeader(jwtConfig.getHEADER_STRING())
                .replace(jwtConfig.getTOKEN_PREFIX(), "").trim();

        // verify token validity
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC512(jwtConfig.getSECRET())).build().verify(inputAccessToken);
            String email = verify.getClaim("email").asString();
            if (email != null) {
                User user = userRepository.findByEmail(email).orElseThrow( //해당 이메일에 대응하는 유저가 없음
                        () -> new NotFoundException("회원가입되지 않은 이메일입니다.")
                );

                // 인증은 토큰 검증시 끝. 인증을 하기 위해서가 아닌 스프링 시큐리티가 수행해주는 권한 처리를 위해
                // 아래와 같이 토큰을 만들어서 Authentication 객체를 강제로 만들고 그걸 세션에 저장!
                PrincipalDetails principalDetails = new PrincipalDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        principalDetails, // 나중에 컨트롤러에서 DI해서 쓸 때 사용하기 편함.
                        null, // 패스워드는 모르니까 null 처리, 어차피 지금 인증하는게 아니니까!!
                        principalDetails.getAuthorities());

                // 강제로 시큐리티의 세션에 접근하여 값 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenExpiredException e) {
            //check if it has refresh token
            Optional<Token> tokenOptional = tokenRepository.findByAccessToken(inputAccessToken);
            if (tokenOptional.isEmpty()) { //if no refresh token, need to log-in again
                throw new InvalidParameterException("다시 로그인을 진행해 주세요");
            }

            DecodedJWT decodedJWT = JWT.decode(inputAccessToken);
            String email = decodedJWT.getClaim("email").asString();
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (email != null) {
                User user = userRepository.findByEmail(email).orElseThrow( //해당 이메일에 대응하는 유저가 없음
                        () -> new NotFoundException("회원가입되지 않은 이메일입니다.")
                );
            }
            User user = byEmail.get();
            String accessToken = jwtConfig.createAccessTokenByUser(user);
            String refreshToken = jwtConfig.createRefreshToken();

            Token token = tokenOptional.get();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);

            throw new TokenExpiredException(String.format("new-access-token=%s", accessToken));
        }

        chain.doFilter(request, response);
    }
}
