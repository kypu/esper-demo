package de.kylie.esperdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kylie.esperdemo.model.SSHLogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SSHLogReader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SSHLogReader.class);

    ObjectMapper jsonMapper = new ObjectMapper();
    String jsonString;
    Process process;

    @Override
    public void run() {
        try {
            process = Runtime.getRuntime().exec("journalctl -u ssh.service -o json");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            jsonString = br.readLine();
            while (jsonString != null) {
                logger.info(jsonString);
                mapToSSHMessage(jsonString.toLowerCase());
                jsonString = br.readLine();
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private void mapToSSHMessage(String jsonString) {
        try {
            SSHLogMessage newMessage = jsonMapper.readValue(jsonString, SSHLogMessage.class);
            logger.info("Mapped json to " + newMessage);
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage());
        }
    }

}
