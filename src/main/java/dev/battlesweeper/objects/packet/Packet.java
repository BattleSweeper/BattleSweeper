package dev.battlesweeper.objects.packet;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonDeserialize(using = PacketDeserializer.class)
//@JsonSerialize(using = PacketSerializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract sealed class Packet permits GameFoundPacket, GameStartPacket, GameUpdatePacket, PlayerUpdatePacket, ResultPacket, TestPacket, TileUpdatePacket, UserJoinPacket {}
