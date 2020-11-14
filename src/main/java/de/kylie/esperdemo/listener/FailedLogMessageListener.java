package de.kylie.esperdemo.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import de.kylie.esperdemo.model.SSHAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FailedLogMessageListener implements UpdateListener {

    Logger logger = LoggerFactory.getLogger(FailedLogMessageListener.class);

    @Override
    public void update(EventBean[] newData, EventBean[] oldData, EPStatement epStatement, EPRuntime epRuntime) {
        logger.info("failed log messages received: " + newData.toString());
        Map<String, Long> attemptsByIpAd = Arrays.stream(newData)
                .map(event -> event.get("ipAddress").toString())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        epRuntime.getEventService().sendEventBean(new SSHAlert(attemptsByIpAd), "SSHAlert");
    }
}
