package dev.battlesweeper.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@JsonDeserialize(as = GameFoundPacket.class)
//@JsonSerialize(as = GameFoundPacket.class)
@AllArgsConstructor
@NoArgsConstructor
public final class GameFoundPacket extends Packet {

    public UUID roomID;
}