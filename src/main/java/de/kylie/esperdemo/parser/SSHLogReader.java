package de.kylie.esperdemo.parser;

import com.espertech.esper.runtime.client.EPRuntime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kylie.esperdemo.model.SSHLogMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@AllArgsConstructor
public class SSHLogReader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SSHLogReader.class);
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final EPRuntime esperRuntime;

    @Override
    public void run() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("journalctl -u ssh.service -o json -f");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String jsonString;
            while (process.isAlive()) {
                jsonString = br.readLine();
                if (jsonString != null) {
                    sendSSHLogMessage(jsonString.toLowerCase());
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException();
        }
    }

    private void sendSSHLogMessage(String jsonString) {
        try {
            SSHLogMessage newMessage = jsonMapper.readValue(jsonString, SSHLogMessage.class);
            esperRuntime.getEventService().sendEventBean(newMessage, "SSHLogMessage");
            logger.info("Mapped and sent: " + newMessage);
        } catch (JsonProcessingException ex) {
            logger.warn(ex.getMessage());
        }
    }

}
