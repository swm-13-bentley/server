package com.schedch.mvp.controller.user;

import com.google.gson.Gson;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.user.UsernamePatchReq;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final Gson gson;

    @PatchMapping("/user/username")
    public ResponseEntity patchUsername(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestBody UsernamePatchReq usernamePatchReq) {
        User user = principalDetails.getUser();
        String newUsername = usernamePatchReq.getNewUsername();
        log.info("P: patchUsername / userId = {}, newUsername = {}", user.getId(), newUsername);

        String newJwtToken = userService.changeUsername(user.getEmail(), newUsername);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("newJwtToken", newJwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(newJwtToken));

    }
}
