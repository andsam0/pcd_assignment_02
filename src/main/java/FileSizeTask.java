import java.io.File;
import java.util.concurrent.RecursiveTask;

public class FileSizeTask extends RecursiveTask<Long> {

    private final File file;
    
    public FileSizeTask(File file) {
        super();
        this.file = file;
    }
    
    @Override
    protected Long compute() { return file.length(); }
}

