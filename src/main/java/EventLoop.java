import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EventLoop {

    private final Vertx vertx = Vertx.vertx();
    private final FileSystem fs = vertx.fileSystem();

    public Future<ReportResult> getFSReport(String directory, long maxFS, int NB) {
        return calculateBands(maxFS, NB, directory);
    }

    private Future<ReportResult> calculateBands(long maxFS, int NB, String p) {
        Path path = Path.of(p);
        if (Files.isRegularFile(path)) {
            return fs.props(p).compose(props -> {
                long size = props.size();
                int band = bandIndex(size, maxFS, NB);
                List<Long> bands = new ArrayList<>(Collections.nCopies(NB + 1, 0L));
                bands.set(band, 1L);
                return Future.succeededFuture(new ReportResult(bands, 1));
            });
        } else if (Files.isDirectory(path)) {
            return fs.readDir(p).compose(files -> {
                List<Future<ReportResult>> futureResults = new ArrayList<>();
                for (String file : files) {
                    futureResults.add(calculateBands(maxFS, NB, file));
                }
                return Future.all(futureResults)
                        .map(cf -> cf.<ReportResult>list().stream()
                                .filter(Objects::nonNull)
                                .reduce(
                                        ReportResult.emptyResult(NB + 1),
                                        ReportResult::merge
                                )
                        );
            });
        }
        return Future.succeededFuture(ReportResult.emptyResult(NB + 1));
    }

    private int bandIndex(long size, long maxFS, int NB) {
        if (size >= maxFS) return NB;
        return (int) (size * NB / maxFS);
    }

    public void close() {
        vertx.close();
    }
}
