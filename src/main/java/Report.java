import java.util.List;

public interface Report {

    List<Long> numFilesPerBand();

    long numFiles();

    void incrementNumberOfFiles(int bandNumber);
}