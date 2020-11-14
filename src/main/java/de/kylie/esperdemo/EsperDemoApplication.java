package de.kylie.esperdemo;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import de.kylie.esperdemo.listener.FailedLogMessageListener;
import de.kylie.esperdemo.listener.LogMessageListener;
import de.kylie.esperdemo.listener.SSHAlertListener;
import de.kylie.esperdemo.model.SSHAlert;
import de.kylie.esperdemo.model.SSHFailedLogMessage;
import de.kylie.esperdemo.model.SSHLogMessage;
import de.kylie.esperdemo.parser.SSHLogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EsperDemoApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EsperDemoApplication.class);

    private final Configuration esperConfig = new Configuration();
    private final EPCompiler esperCompiler = EPCompilerProvider.getCompiler();
    private EPRuntime esperRuntime;

    public static void main(String[] args) {
        SpringApplication.run(EsperDemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        initialiseEsper();
        startLogReader();
    }

    /**
     * Adds event types and creates ep statements and listeners
     */
    private void initialiseEsper() {
        // configure esper
        esperConfig.getCommon().addEventType(SSHLogMessage.class);
        esperConfig.getCommon().addEventType(SSHFailedLogMessage.class);
        esperConfig.getCommon().addEventType(SSHAlert.class);
        esperRuntime = EPRuntimeProvider.getDefaultRuntime(esperConfig);
        // create esper statements
        CompilerArguments compilerArgs = new CompilerArguments(esperConfig);
        EPStatement statementA = prepareEPStatement("statement-a",
                "@name('statement-a') select * from SSHLogMessage where isFailedLogin = true", compilerArgs);
        EPStatement statementB = prepareEPStatement("statement-b",
                "@name('statement-b') select * from SSHFailedLogMessage#time_length_batch(1 min, 3)", compilerArgs);
        EPStatement statementC = prepareEPStatement("statement-c",
                "@name('statement-c') select * from SSHAlert", compilerArgs);
        // add listeners
        statementA.addListener(new LogMessageListener());
        statementB.addListener(new FailedLogMessageListener());
        statementC.addListener(new SSHAlertListener());
    }

    /**
     * Compiles, deploys and returns an EP Statement from a string
     * @param name the name of the statement needed to access it via the deployment service
     * @param statement string with statment in epl (event processing language)
     * @param compilerArgs created using the esper configuration object
     * @return the compiled and deployed EP Statement ready to have listeners added
     */
    private EPStatement prepareEPStatement(String name, String statement, CompilerArguments compilerArgs) {
        EPDeployment epDeployment;
        try {
            EPCompiled epCompiled = esperCompiler.compile(statement, compilerArgs);
            epDeployment = esperRuntime.getDeploymentService().deploy(epCompiled);
        } catch (EPCompileException | EPDeployException ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException();
        }
        return esperRuntime.getDeploymentService().getStatement(epDeployment.getDeploymentId(), name);
    }

    /**
     * Starts one thread to read from the ssh log, parse the logs and send the initial events.
     * In the future this could be a thread pool to increase performance.
     */
    private void startLogReader() {
        Runnable sshLogReader = new SSHLogReader(esperRuntime);
        Thread sshLogThread = new Thread(sshLogReader);
        sshLogThread.start();
    }
}
