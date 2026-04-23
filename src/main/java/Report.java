import java.util.List;
import java.util.OptionalInt;

public interface Report {

    List<Long> numFilesPerBand();

    long numFiles();

    void incrementNumberOfFiles(OptionalInt bandNumber);
}