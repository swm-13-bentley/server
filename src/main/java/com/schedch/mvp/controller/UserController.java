package com.schedch.mvp.controller;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final Gson gson;

    @GetMapping("/user/test")
    public ResponseEntity jwtTest() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gson.toJson("success"));
    }
}
