import java.util.concurrent.ForkJoinPool;

public class FSStat {

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();


    public ReportResult getFSReport(Folder folder, long maxFs, long numSizeBand) {
        return forkJoinPool.invoke(new FolderSearchTask(folder, maxFs, numSizeBand));
    }

}
