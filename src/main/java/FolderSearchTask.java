import java.io.File;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class FolderSearchTask extends RecursiveTask<ReportResult> {

    private final Folder folder;
    private final long maxFs;
    private final long numSizeBand;

    public FolderSearchTask(Folder folder, long maxFs, long numSizeBand) {
        super();
        this.folder = folder;
        this.maxFs = maxFs;
        this.numSizeBand = numSizeBand;
    }
    
    @Override
    protected ReportResult compute() {
        long count = 0L;
        List<RecursiveTask<ReportResult>> folderForks = new LinkedList<>();
        List<RecursiveTask<Long>> fileForks = new LinkedList<>();

        for (Folder subFolder : folder.subFolders()) {
            FolderSearchTask task = new FolderSearchTask(subFolder,this.maxFs,this.numSizeBand);
            folderForks.add(task);
            task.fork();
        }
        for (File file : folder.files()) {
            FileSizeTask task = new FileSizeTask(file);
            fileForks.add(task);
            task.fork();
        }

        List<ReportResult> results = new LinkedList<>();
        for (RecursiveTask<ReportResult> folderTask : folderForks) {
            results.add(folderTask.join());
        }

        List<Long> numFilesPerBand = new ArrayList<>(Collections.nCopies((int)numSizeBand,0L));
        for (RecursiveTask<Long> fileTask : fileForks) {
            long fileSize = fileTask.join();
            long bandWidth = this.maxFs / this.numSizeBand;
            // TODO: gestire file con dimensione uguale o maggiore di maxFs
            int bandIndex = Math.toIntExact(fileSize / bandWidth);
//            System.out.println(fileSize+"->"+bandIndex);
//            System.out.println(bandIndex);
            Long previous = numFilesPerBand.get(bandIndex);
            numFilesPerBand.set(bandIndex, previous+1);
        }
        int numFiles = folder.files().toArray().length;
        for(ReportResult result : results){
            for(int i=0; i<result.numFilesPerBand().toArray().length; i++){
                Long previous = result.numFilesPerBand().get(i);
                numFilesPerBand.set(i, previous + numFilesPerBand.get(i));
            }
            numFiles += result.numFiles();
        }

        return new ReportResult(numFiles, numFilesPerBand);
    }
}
    