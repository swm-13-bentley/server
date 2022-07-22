package com.schedch.mvp.controller.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                .status(HttpStatus.BAD_REQUEST)
                .body(gson.toJson(errorJson));
    }


    private JsonObject getErrorJson(String errorMessage) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", errorMessage);
        return jsonObject;
    }
}
