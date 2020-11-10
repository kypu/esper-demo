package de.kylie.esperdemo;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogListener implements UpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(LogListener.class);

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {

        EventBean event = newEvents[0];
        logger.info(event.toString());

    }

}
