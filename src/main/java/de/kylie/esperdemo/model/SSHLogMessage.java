package de.kylie.esperdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.kylie.esperdemo.parser.SSHLogDeserializer;
import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SSHLogDeserializer.class)
public class SSHLogMessage {

    String message;
    Boolean isFailedLogin;
}
