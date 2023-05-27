package dev.battlesweeper.objects.packet;

import dev.battlesweeper.objects.Position;
import dev.battlesweeper.objects.UserInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor @ToString
public final class GameStartPacket extends Packet {

    public List<UserInfo> users;
    public Position boardSize;
    public Position[] mines;
}
