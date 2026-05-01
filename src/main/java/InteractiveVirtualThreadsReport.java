import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class InteractiveVirtualThreadsReport {
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private Report report;
    private long maxFS;
    private int bands;
    private final Phaser phaser;
    private final ExecutorService executor;
    private ReportController controller;

    public InteractiveVirtualThreadsReport() {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.phaser = new Phaser(1);
    }
    public void setController(ReportController controller){
        this.controller = controller;
    }
    public void start(String directory, long maxFS, int bands) {
        this.report = new AtomicReport(bands);
        this.maxFS = maxFS;
        this.bands = bands;
        this.stopped.set(false);

        Path path = Path.of(directory);
        submit(() -> calculateSize(path));

        executor.submit(() -> {
            phaser.awaitAdvance(phaser.arrive());
            if (!stopped.get()) controller.completed();
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
