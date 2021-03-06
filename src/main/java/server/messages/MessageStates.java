package server.messages;

public enum MessageStates {
    ALREADY_AUTHORIZED("user is already authorized"),
    NOT_ENOUGH_DATA("not enough data"),
    AUTHORIZED("successful authorize"),
    BAD_AUTHORIZE("invalid login or password"),
    EMAIL_ALREADY_EXISTS("this email already registered"),
    LOGIN_ALREADY_EXISTS("this login already registered"),
    BAD_PASSWORD("bad password"),
    REGISTERED("you are registered!!!"),
    PASSWORDS_DO_NOT_MATCH("passwords don't match"),
    UNAUTHORIZED("not authorized"),
    CHANGED_USER_DATA("user data has been changed"),
    BAD_DATA("bad data given"),
    SUCCESS_UPLOAD("you successfully uploaded file"),
    SUCCESS_UPDATE("successfully update"),
    MAP_NOT_FOUND("map not found"),
    LEVEL_NOT_FOUND("level not found"),
    NOT_UPDATED("nothing to update"),
    DATABASE_ERROR("database error");

    private String message;

    MessageStates(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}