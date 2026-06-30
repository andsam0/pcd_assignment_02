import java.util.List;

public interface Report {

    List<Long> numFilesPerBand();

    Long numFiles();

    void incrementNumberOfFiles(int bandNumber);
}