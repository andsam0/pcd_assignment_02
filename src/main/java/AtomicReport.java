import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicReport implements Report {
    private final List<AtomicLong> bands;
    private final AtomicLong numberOfFile;

    public AtomicReport(int numberOfBands) {
        AtomicLong[] temp = new AtomicLong[numberOfBands+1];
        for (int i = 0; i < numberOfBands+1; i++) {
            temp[i] = new AtomicLong();
        }
        bands = List.of(temp);
        numberOfFile = new AtomicLong();
    }

    @Override
    public List<Long> numFilesPerBand(){
        return bands.stream().map(AtomicLong::get).toList();
    }

    @Override
    public long numFiles() {
        return numberOfFile.get();
    }

    @Override
    public void incrementNumberOfFiles(int bandNumber){
        numberOfFile.incrementAndGet();
        bands.get(bandNumber).incrementAndGet();
    }
}