package dev.battlesweeper.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonDeserialize(as = TestPacket.class)
@PacketType(type = "yeet")
@AllArgsConstructor
@NoArgsConstructor
public final class TestPacket extends Packet {

    public String hello;

    @Override
    public String toString() {
        return "TestPacket{" +
                "hello='" + hello + '\'' +
                '}';
    }
}
