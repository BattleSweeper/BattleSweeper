package dev.battlesweeper.objects.packet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor @Getter
@PacketType(type = "event_game_update")
public final class GameUpdatePacket extends Packet {

    public static final int STATE_OVER = -1;
    public static final int STATE_WIN  = 1;

    private int state;
}
