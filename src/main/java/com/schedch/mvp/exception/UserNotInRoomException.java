package com.schedch.mvp.exception;

public class UserNotInRoomException extends RuntimeException {

    public UserNotInRoomException() {

    }

    public UserNotInRoomException(String message) {
        super(message);
    }

}
