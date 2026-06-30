import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AtomicReport implements Report {
    private final List<Long> bands;
    private Long numberOfFile = 0L;

    public AtomicReport(int numberOfBands) {
        bands = new ArrayList<>(Collections.nCopies(numberOfBands+1, 0L));
    }

    @Override
    public synchronized List<Long> numFilesPerBand(){
        return bands;
    }

    @Override
    public synchronized Long numFiles() {
        return numberOfFile;
    }

    @Override
    public synchronized void incrementNumberOfFiles(int bandNumber){
        numberOfFile++;
        bands.set(bandNumber, bands.get(bandNumber)+1);
    }
}