package de.kylie.esperdemo;

import de.kylie.esperdemo.parser.SSHLogReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EsperDemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EsperDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Runnable sshLogReader = new SSHLogReader();
        Thread inputThread = new Thread(sshLogReader);
        inputThread.start();

    }
}
