import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EventLoop {

    private final Vertx vertx = Vertx.vertx();
    private final FileSystem fs = vertx.fileSystem();

    public Future<ReportResult> getFSReport(String directory, long maxFS, int NB) {
        Path path = Path.of(directory);
        List<Future<ReportResult>> futures = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(path)) {
            stream
                .filter(p -> Files.isRegularFile(p) || Files.isDirectory(p))
                .map(Path::toString)
                .map(p -> {
                    return calculateBands(maxFS, NB, p);
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Future<ReportResult> calculateBands(long maxFS, int NB, String p) {
        if(Files.isRegularFile(Path.of(p))){
            return fs.props(p).compose(props -> {
                long size = props.size();
                int band = bandIndex(size, maxFS, NB);
                List<Long> bands = new ArrayList<>(Collections.nCopies(NB + 1,0L)); // wrap for mutability
                bands.set(band, 1L);
                return Future.succeededFuture(new ReportResult(bands, 1)) ;
            });
        } else {
            fs.readDir(p).onComplete(files -> {
                List<Future<ReportResult>> futureResults = new ArrayList<>();
                for(String file : files.result()){
                    futureResults.add(calculateBands(maxFS,NB,file));
                }
                Future.all(futureResults)
                        .map(CompositeFuture::list)
                        .compose(results -> Future.succeededFuture(results.stream()
                                .filter(Objects::nonNull)
                                        .reduce(
                                                (ReportResult) ReportResult.emptyResult(NB),
                                                ReportResult::merge  // equivalent to (a, b) -> a.merge(b)
                                        )
//                                .reduce((ReportResult)ReportResult.emptyResult(NB),
////                                        (report, element) -> {
////                                    if(report instanceof ReportResult report2 && element instanceof ReportResult element2){
////                                        report = report2.merge(element2);
////                                    }
////                                }
//                                )
                                )
                        );
            });
        }
        return null;
    }

    private int bandIndex(long size, long maxFS, int NB) {
        if (size >= maxFS) return NB;
        return (int) (size * NB / maxFS);
    }
}
