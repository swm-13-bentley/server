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

    public static String userCalendarNotFound(Long userCalendarId) {
        return String.format("UserCalendar is not found for userCalendarId = %s", userCalendarId.toString());
    }

    public static String cannotDeleteMainCalendar() {
        return String.format("You cannot delete main calendar");
    }

    public static String cannotChangeMainCalendarError() {
        return String.format("Cannot change main calendar due to internal server error");
    }

    public static String cannotFindMainCalendarError() {
        return String.format("Cannot find main calendar");
    }
}
