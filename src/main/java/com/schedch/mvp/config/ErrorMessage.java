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

    public static String userDoesNotOwnUserCalendar(Long userId, Long userCalendarId) {
        return String.format("User=%s does not own userCalendar=%s", userId.toString(), userCalendarId.toString());
    }

    public static String participantNameNotInRoom(String participantName, String roomUuid) {
        return String.format("No participant found / participant name = %s, roomId = %s", participantName, roomUuid);
    }

    public static String passwordIsWrong(String participantName, String password, String roomUuid) {
        return String.format("Password is wrong / participantName = %s, password = %s, roomUuid = %s", participantName, password, roomUuid);
    }

    public static String notSignedInEmail(String email) {
        return String.format("This email is not signed in / email = %s", email);
    }

    public static String alreadyExistingParticipantName(String participantName) {
        return String.format("Already existing participantName / participantName = %s", participantName);

    }

    public static String noParticipantForId(Long participantId) {
        return String.format("No participant found for id / participantId = %s", participantId);
    }

    public static String participantIsSignedIn(String participantName, String roomUuid) {
        return String.format("This participant is signed in / participantId = %s, roomUuid = %s", participantName, roomUuid);
    }
}
