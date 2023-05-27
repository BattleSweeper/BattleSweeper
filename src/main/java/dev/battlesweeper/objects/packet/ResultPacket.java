package dev.battlesweeper.objects.packet;

public final class ResultPacket extends Packet {

    public static final int RESULT_OK       = 200;
    public static final int RESULT_BAD_DATA = 406;
    public static final int RESULT_FAILURE  = 400;

    public int result;

    public String message;
}
