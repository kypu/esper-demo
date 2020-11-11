package de.kylie.esperdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SSHLogMessage {

    String message;
    String syslog_timestamp;
    String _hostname;

}
