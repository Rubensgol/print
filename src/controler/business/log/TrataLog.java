package controler.business.log;

import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class TrataLog extends StreamHandler
{   
    @Override
    public void publish(LogRecord gravar)
    {
        //FileHandler fileHandler = new FileHandler(gravar.getLoggerName());

        super.publish(gravar);
    }

    @Override
    public void flush()
    {
        super.flush();
    }

    @Override
    public void close() throws SecurityException
    {
        super.close();
    }
}
