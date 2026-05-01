import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportController {

    private final InteractiveVirtualThreadsReport model;
    private final ReportView view;
    private Thread viewUpdater;
    private final ExecutorService executor;

    ReportController(ReportView view, InteractiveVirtualThreadsReport model){
        this.view = view;
        this.model = model;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start(String directory, long maxFS, int bands){

        viewUpdater = Thread.ofVirtual().start(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(25);
                    SwingUtilities.invokeLater(view::onUpdate);
                }
            } catch (InterruptedException _) {
            }
        });

        this.executor.submit(() -> this.model.start(directory, maxFS, bands));
    }

    public void stop(){
        this.executor.submit(() -> {
            this.model.stop();
            finishUpdate();
        });
    }

    public void completed(){
        finishUpdate();
        SwingUtilities.invokeLater(this.view::onComplete);
    }

    private void finishUpdate() {
        viewUpdater.interrupt();
        try {
            viewUpdater.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Report getReport() {
        return this.model.getReport();
    }
}
