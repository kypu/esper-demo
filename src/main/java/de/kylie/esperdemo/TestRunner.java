package de.kylie.esperdemo;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    @Override
    public void run(String... args) {

        Configuration configuration = new Configuration();
        configuration.addEventType(LogEvent.class);


        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(configuration);
        EPStatement statement = epService.getEPAdministrator().createEPL("select * from LogEvent where type = 'error' ");

        LogListener listener = new LogListener();
        statement.addListener(listener);

        logger.info("Sending test events");

        LogEvent event = new LogEvent("test log sshd", "normal");
        LogEvent event2 = new LogEvent("test log error", "error");
        epService.getEPRuntime().sendEvent(event);
        epService.getEPRuntime().sendEvent(event2);
    }
}