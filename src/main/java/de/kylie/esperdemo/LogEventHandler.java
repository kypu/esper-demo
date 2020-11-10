package de.kylie.esperdemo;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;

public class LogEventHandler {

    EPCompiler compiler = EPCompilerProvider.getCompiler();

    Configuration configuration = new Configuration();
    configuration.
            CompilerArguments args = new CompilerArguments(configuration);

    EPCompiled epCompiled;
    try {
            epCompiled = compiler.compile("@name('my-statement') select name, age from PersonEvent", args);
        }
    catch (
    EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

}
