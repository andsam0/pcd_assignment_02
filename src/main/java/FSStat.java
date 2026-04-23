import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;

public class FSStat {

    public Single<ReportResult> getFSReport(File dir, long maxFS, int NB) {
        return walkFiles(dir)
                .subscribeOn(Schedulers.io())
                .map(file -> bandIndex(file.length(), maxFS, NB))
                .collect(
                        () -> new ArrayList<>(Collections.nCopies(NB + 1, 0L)),
                        (list, idx) -> list.set(idx, list.get(idx) + 1)
                ).map(bands -> new ReportResult(bands.stream().mapToLong(Long::longValue).sum(), bands));
    }

    private Observable<File> walkFiles(File entry) {
//        System.out.println(Thread.currentThread().getName());
        if (entry.isFile()) {
            return Observable.just(entry);
        }
        File[] children = entry.listFiles();
        if (children == null)
            return Observable.empty(); // see https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/File.html#listFiles()
        return Observable.fromArray(children)
                .flatMap(x -> walkFiles(x).subscribeOn(Schedulers.io()));
    }

    private int bandIndex(long size, long maxFS, int NB) {
        if (size >= maxFS) return NB;
        return (int) (size * NB / maxFS);
    }
}
