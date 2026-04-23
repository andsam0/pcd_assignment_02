import java.util.List;
import java.util.OptionalInt;

public interface Report {

    List<Long> getBands();

    long getNumberOfFile();

    void incrementNumberOfFiles(OptionalInt bandNumber);
}