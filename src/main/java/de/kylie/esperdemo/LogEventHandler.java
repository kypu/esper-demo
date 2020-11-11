package de.kylie.esperdemo;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEventHandler {

    EPCompiled epCompiled;

    /** Logger */
    private static Logger logger = LoggerFactory.getLogger(LogEventHandler.class);

    /** Esper service */
    private EPServiceProvider epService;
    private EPStatement logEventStatement;

    public void compileStatements() {

        EPCompiler compiler = EPCompilerProvider.getCompiler();
        Configuration configuration = new Configuration();


        CompilerArguments args = new CompilerArguments(configuration);

        try {
            epCompiled = compiler.compile("@name('my-statement') select message from SSHLogEvent", args);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }




    }

}
