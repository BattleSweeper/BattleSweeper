package dev.battlesweeper.network.body;

public class ResultPacket {

    public static final int RESULT_OK       = 200;
    public static final int RESULT_BAD_DATA = 406;
    public static final int RESULT_FAILURE  = 400;

    public String type;

    public int result;

    public String message;
}
