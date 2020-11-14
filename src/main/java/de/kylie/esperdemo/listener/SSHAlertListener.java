package de.kylie.esperdemo.listener;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSHAlertListener implements UpdateListener {

    Logger logger = LoggerFactory.getLogger(FailedLogMessageListener.class);

    @Override
    public void update(EventBean[] newData, EventBean[] oldData, EPStatement epStatement, EPRuntime epRuntime) {
        logger.info("ALERT: " + newData[0].toString());
    }
}
