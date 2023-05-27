package dev.battlesweeper.objects.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.battlesweeper.objects.Position;

import java.io.IOException;

public class PositionDeserializer extends JsonDeserializer<Position> {

    @Override
    public Position deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
        if (p.currentToken() != JsonToken.START_ARRAY)
            return null;

        var builder = Position.builder();
        p.nextToken();
        if (p.currentToken() != JsonToken.VALUE_NUMBER_INT)
            return null;
        builder.x(p.getValueAsInt());

        p.nextToken();
        if (p.currentToken() != JsonToken.VALUE_NUMBER_INT)
            return null;
        builder.y(p.getValueAsInt());

        return builder.build();
    }
}
