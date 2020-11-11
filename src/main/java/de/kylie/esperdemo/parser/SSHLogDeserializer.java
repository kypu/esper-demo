package de.kylie.esperdemo.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.kylie.esperdemo.model.SSHLogMessage;

import java.io.IOException;
import java.security.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

public class SSHLogDeserializer extends StdDeserializer<SSHLogMessage> {

    public SSHLogDeserializer() {
        this(null);
    }

    public SSHLogDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SSHLogMessage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // json node is Jackson's standard representation of json
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String message = node.get("message").asText();
        Long timestamp = node.get("__realtime_timestamp").asLong();
        return new SSHLogMessage(message, timestamp);
    }
}
