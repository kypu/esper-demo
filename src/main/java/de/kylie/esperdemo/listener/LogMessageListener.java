package de.kylie.esperdemo.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import de.kylie.esperdemo.model.SSHFailedLogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMessageListener implements UpdateListener {

    Logger logger = LoggerFactory.getLogger(LogMessageListener.class);

    @Override
    public void update(EventBean[] newData, EventBean[] oldData, EPStatement epStatement, EPRuntime epRuntime) {
        // logger.info("failed log message received: " + newData[0].toString());
        String failedMessage = newData[0].get("message").toString();
        String ipAddress = failedMessage.split(" ")[5];
        epRuntime.getEventService().sendEventBean(new SSHFailedLogMessage(ipAddress), "SSHFailedLogMessage");
    }
}
