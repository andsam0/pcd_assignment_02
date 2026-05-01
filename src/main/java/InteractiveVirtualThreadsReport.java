import javax.swing.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class InteractiveVirtualThreadsReport implements AsyncReportCalculator {
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Report report;
    private long maxFS;
    private int bands;
    private Phaser phaser;
    private ExecutorService executor;

    @Override
    public Future<Report> getFSReport(String directory, long maxFS, int bands) {
        this.report = new AtomicReport(bands);
        this.maxFS = maxFS;
        this.bands = bands;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.phaser = new Phaser(1);

        final ReportView reportView = new ReportView(this, maxFS, bands);
        reportView.setVisible(true);

        Thread viewUpdater = Thread.ofVirtual().start(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(10);
                    SwingUtilities.invokeLater(reportView::onUpdate);
                }
            } catch (InterruptedException _) {
            }
        });

        Path path = Path.of(directory);
        submit(() -> calculateSize(path));

        return executor.submit(() -> {
            phaser.awaitAdvance(phaser.arrive());
            viewUpdater.interrupt();
            viewUpdater.join();
            if (stopped.get()) SwingUtilities.invokeLater(reportView::onStop);
            else SwingUtilities.invokeLater(reportView::onComplete);
            return report;
        });
    }

    private void calculateSize(Path path) {
        if (stopped.get()) return;

        /* for testing purposes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */


        try (Stream<Path> stream = Files.list(path)) {
            stream
                    .filter(p -> Files.isRegularFile(p) || Files.isDirectory(p))
                    .forEach(p -> {
                        if (Files.isRegularFile(p)) {
                            if (stopped.get()) return;
                            try {
                                this.report.incrementNumberOfFiles(bandIndex(Files.size(p), maxFS, bands));
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        } else {
                            submit(() -> calculateSize(p));
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void submit(Runnable action) {
        phaser.register(); // must be done before creating the new child
        executor.submit(() -> {
            try {
                action.run();
            } finally {
                phaser.arriveAndDeregister();
            }
        });
    }

    private int bandIndex(long size, long maxFS, int NB) {
        if (size >= maxFS) return NB;
        return (int) (size * NB / maxFS);
    }

    public void stop() {
        this.stopped.set(true);
    }

    public Report getReport() {
        return this.report;
    }
}
