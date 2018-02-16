package th.in.ffc.airsync.logfile;

import th.in.ffc.airsync.logfile.filter.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Controller {
    private ReadLog readlogmodule;
    private CsvLogWriter csvwritemodule;
    List<Filters> filters = Arrays.asList(
            new GetTimeFilter(Config.timePattern),
            new QueryFilter(Config.logpattern),
            new NowFilter(),
            new CreateHash()
    );

    interface onLogFileException {
        void ioException(IOException ex);
    }

    interface onCsvFileException {
        void ioException(IOException ex);
    }

    private onLogFileException onLogFileExceptionListener;
    private onCsvFileException onCsvFileExceptionListener;

    public Controller(onLogFileException onLogFileExceptionListener, onCsvFileException onCsvFileExceptionListener, String logfilepath, String csvfilepath, boolean realtime) {
        this.onLogFileExceptionListener = onLogFileExceptionListener;
        this.onCsvFileExceptionListener = onCsvFileExceptionListener;
        readlogmodule = new ReadLog(logfilepath, realtime);
        try {
            csvwritemodule = new CsvLogWriter(csvfilepath);
        } catch (IOException e) {
            e.printStackTrace();
            onCsvFileExceptionListener.ioException(e);
        }
        readlogmodule.setListener(record -> {

            try {
                csvwritemodule.write(record);
            } catch (IOException e) {
                e.printStackTrace();
                onCsvFileExceptionListener.ioException(e);
            }

        });
    }

    public Controller() {
        this(ex -> {
        }, ex -> {
        }, Config.logfilepath, Config.csvfilepath, true);
    }

    public Controller(String logfilepath, String csvfilepath, boolean realtime) {
        this(ex -> {
        }, ex -> {
        }, logfilepath, csvfilepath, realtime);
    }


    public void process() {
        Thread thread = new Thread(() -> {
            try {
                //readlogmodule.run();
                ReadTextFile readTextFile = new ReadTextFile(Config.logfilepath);
                readTextFile.setListener(record -> {

                    for(Filters filter : filters){
                        filter.process(record);
                        if(record.getLog().equals(""))break;
                    }




                    try {
                        if (!record.getLog().equals(""))
                            csvwritemodule.write(record);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                readTextFile.process();

            } catch (IOException e) {
                e.printStackTrace();
                onLogFileExceptionListener.ioException(e);
            }
        });
        thread.start();

    }

    public void processSingle() {
        try {
            readlogmodule.run();
        } catch (IOException e) {
            e.printStackTrace();
            onLogFileExceptionListener.ioException(e);
        }
    }
}