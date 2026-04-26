import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;

public class FSStat {

    private final Scheduler scheduler = Schedulers.computation(); // io() in compiti pesanti produce troppi thread

    public Single<ReportResult> getFSReport(File dir, long maxFS, int NB) {
        return walkFiles(dir)
                .subscribeOn(this.scheduler)
                .map(file -> bandIndex(file.length(), maxFS, NB))
                .collect(
                        () -> new ArrayList<>(Collections.nCopies(NB + 1, 0L)),
                        (list, idx) -> list.set(idx, list.get(idx) + 1)
                ).map(bands -> new ReportResult(bands.stream().mapToLong(Long::longValue).sum(), bands));
    }

    private Observable<File> walkFiles(File entry) {
//        System.out.println(Thread.currentThread().getName());
        if (Files.isSymbolicLink(entry.toPath())) return Observable.empty();
        if (entry.isFile()) {
            return Observable.just(entry);
        }
        File[] children = entry.listFiles();
        if (children == null)
            return Observable.empty(); // see https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/File.html#listFiles()
        return Observable.fromArray(children)
                .flatMap(x -> walkFiles(x).subscribeOn(this.scheduler));
    }

    private int bandIndex(long size, long maxFS, int NB) {
        if (size >= maxFS) return NB;
        return (int) (size * NB / maxFS);
    }
}
