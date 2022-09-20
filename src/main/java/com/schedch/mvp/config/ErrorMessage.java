package com.schedch.mvp.config;


public class ErrorMessage {

    public static String roomNotFoundForUuid(String roomUuid) {
        return String.format("Room for uuid: %s not found", roomUuid);
    }

    public static String fullMemberForUuid(String roomUuid) {
        return String.format("Room is full for roomUuid: %s", roomUuid);
    }

    public static String userNotInRoom(String roomUuid) {
        return String.format("User is not in room for roomUuid: %s", roomUuid);
    }
}
