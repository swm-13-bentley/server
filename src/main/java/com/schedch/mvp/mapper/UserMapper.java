package com.schedch.mvp.mapper;

import com.schedch.mvp.dto.oauth.GoogleLoginDto;
import com.schedch.mvp.dto.oauth.KakaoProfileRes;
import com.schedch.mvp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "username", expression = "java(googleLoginDto.getName())")
    @Mapping(target = "email", expression = "java(googleLoginDto.getEmail())")
    @Mapping(target = "signInChannel", expression = "java(getGoogleString())")
    @Mapping(target = "scope", expression = "java(googleLoginDto.getScope())")
    User googleLoginDto2User(GoogleLoginDto googleLoginDto);


    @Mapping(target = "username", expression = "java(kakaoProfileRes.getNickname())")
    @Mapping(target = "email", expression = "java(kakaoProfileRes.getEmail())")
    @Mapping(target = "signInChannel", expression = "java(getKakaoString())")
    User kakaoProfileRes2User(KakaoProfileRes kakaoProfileRes);

    default String getGoogleString() {
        return "google";
    }

    default String getKakaoString() {
        return "kakao";
    }
}
