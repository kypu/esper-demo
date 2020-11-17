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
import org.springframework.stereotype.Component;

@Component
public class InitCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EsperDemoApplication.class);

    private final Configuration esperConfig = new Configuration();
    private final EPCompiler esperCompiler = EPCompilerProvider.getCompiler();
    private EPRuntime esperRuntime;

    @Override
    public void run(String... args) {
        // default arguments
        int numberFailedLogins = 3;
        int seconds = 60;
        // get custom arguments if given
        if (args.length > 0 && Integer.parseInt(args[0]) > 0) {
            numberFailedLogins = Integer.parseInt(args[0]);
            logger.info("Using custom number of failed logins = " + numberFailedLogins);
        } else {
            logger.info("Using default number of failed logins = " + numberFailedLogins);
        }
        if (args.length > 1 && Integer.parseInt(args[1]) > 0) {
            seconds = Integer.parseInt(args[1]);
            logger.info("Using custom time window (in seconds) = " + seconds);
        } else {
            logger.info("Using default time window (in seconds) = " + seconds);
        }
        // use command line args to initialise esper
        initialiseEsper(numberFailedLogins, seconds);
        // start log reader
        SSHLogReader sshLogReader = new SSHLogReader(esperRuntime);
        sshLogReader.start();
    }

    /**
     * Adds event types and creates ep statements and listeners
     */
    private void initialiseEsper(int numberFailedLogins, int seconds) {
        // configure esper
        esperConfig.getCommon().addEventType(SSHLogMessage.class);
        esperConfig.getCommon().addEventType(SSHFailedLogMessage.class);
        esperConfig.getCommon().addEventType(SSHAlert.class);
        esperRuntime = EPRuntimeProvider.getDefaultRuntime(esperConfig);
        // create esper statements
        String rawStringA = "@name('statement-a') select * from SSHLogMessage where isFailedLogin = true";
        String rawStringB = "@name('statement-b') select * from SSHFailedLogMessage.win:time_length_batch(" + seconds + " sec, " + numberFailedLogins + ")";
        String rawStringC = "@name('statement-c') select * from SSHAlert";
        CompilerArguments compilerArgs = new CompilerArguments(esperConfig);
        EPStatement statementA = prepareEPStatement("statement-a", rawStringA, compilerArgs);
        EPStatement statementB = prepareEPStatement("statement-b", rawStringB, compilerArgs);
        EPStatement statementC = prepareEPStatement("statement-c", rawStringC, compilerArgs);
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

}
