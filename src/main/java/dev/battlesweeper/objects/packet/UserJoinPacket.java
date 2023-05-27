package dev.battlesweeper.objects.packet;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public final class UserJoinPacket extends Packet {

    public Long id;
    public String name;
}
