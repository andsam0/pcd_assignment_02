import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class VirtualThreadsReport {

    private Report report;
    private long maxFS;
    private int bands;
    private Phaser phaser;
    private ExecutorService executor;

    public Future<Report> getFSReport(String directory, long maxFS, int bands) {
        this.report = new AtomicReport(bands);
        this.maxFS = maxFS;
        this.bands = bands;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.phaser = new Phaser(1);

        Path path = Path.of(directory);
        submit(() -> calculateSize(path));

        return executor.submit(() -> {
            phaser.awaitAdvance(phaser.arrive());
            return report;
        });
    }

    private void calculateSize(Path path) {
        try (Stream<Path> stream = Files.list(path)) {
            stream
                    .filter(p -> Files.isRegularFile(p) || Files.isDirectory(p))
                    .forEach(p -> {
                        if (Files.isRegularFile(p)) {
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

}
