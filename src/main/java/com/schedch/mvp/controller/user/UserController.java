package com.schedch.mvp.controller.user;

import com.google.gson.Gson;
import com.schedch.mvp.config.auth.PrincipalDetails;
import com.schedch.mvp.dto.user.mypage.MyEmailPatchReq;
import com.schedch.mvp.dto.user.mypage.MyEmailRes;
import com.schedch.mvp.dto.user.username.UsernamePatchReq;
import com.schedch.mvp.dto.user.username.UsernamePatchRes;
import com.schedch.mvp.model.User;
import com.schedch.mvp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final Gson gson;

    @GetMapping("/user/alarmEmail/all")
    public ResponseEntity getAlarmEmail(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String userEmail = principalDetails.getUsername();
        log.info("P: getMyPageInfo / userEmail = {}", userEmail);

        List<MyEmailRes> userEmailList = userService.getUserEmailList(userEmail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(userEmailList));
    }

    @PatchMapping("/user/alarmEmail/status")
    public ResponseEntity patchAlarmEmailStatus(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestBody List<MyEmailPatchReq> myEmailPatchReqList) {
        String userEmail = principalDetails.getUsername();
        log.info("P: patchAlarmEmailStatus / userEmail = {}", userEmail);

        userService.changeUserEmailReceiveStatus(userEmail, myEmailPatchReqList);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }


    @PatchMapping("/user/username")
    public ResponseEntity patchUsername(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestBody UsernamePatchReq usernamePatchReq) {

        User user = principalDetails.getUser();
        String newUsername = usernamePatchReq.getNewUsername();
        log.info("P: patchUsername / userId = {}, newUsername = {}", user.getId(), newUsername);

        String newJwtToken = userService.changeUsername(user.getEmail(), newUsername);
        UsernamePatchRes res = new UsernamePatchRes(newJwtToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(gson.toJson(res));

    }
}
