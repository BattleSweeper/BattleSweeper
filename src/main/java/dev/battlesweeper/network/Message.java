package dev.battlesweeper.network;

public final class Message {
    private Message() {}

    public static final String NAME_EXISTS       = "NAME_EXISTS";
    public static final String UNKNOWN_TYPE      = "UNKNOWN_TYPE";
    public static final String NOT_FOUND         = "NOT_FOUND";
    public static final String ENCRYPT_FAILURE   = "ENCRYPT_FAILURE";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String TOKEN_EXPIRED     = "TOKEN_EXPIRED";
    public static final String TOKEN_INVALID     = "TOKEN_INVALID";
}
