package com.schedch.mvp.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.FailedLoginException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerHandler {

    private final Gson gson;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        JsonObject errorJson = getErrorJson(errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(gson.toJson(errorJson));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
        JsonObject errorJson = getErrorJson(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(gson.toJson(errorJson));
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity handleIllegalAccessException(IllegalAccessException e) {
        JsonObject errorJson = getErrorJson(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(gson.toJson(errorJson));
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity handleUriSyntaxException(URISyntaxException e) {
        JsonObject errorJson = getErrorJson(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(gson.toJson(errorJson));
    }

    @ExceptionHandler(FailedLoginException.class)
    public ResponseEntity handleFailedLoginException(FailedLoginException e) {
        JsonObject errorJson = getErrorJson(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(gson.toJson(errorJson));
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity handleJsonProcessingException(JsonProcessingException e) {
        JsonObject errorJson = getErrorJson(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(gson.toJson(errorJson));
    }

    private JsonObject getErrorJson(String errorMessage) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", errorMessage);
        return jsonObject;
    }
}
