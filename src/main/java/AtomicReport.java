import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicReport implements Report {
    private final List<AtomicLong> bands;
    private final AtomicLong numberOfFile;

    public AtomicReport(int numberOfBands) {
        AtomicLong[] temp = new AtomicLong[numberOfBands];
        for (int i = 0; i < numberOfBands; i++) {
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
    public void incrementNumberOfFiles(OptionalInt bandNumber){
        numberOfFile.incrementAndGet();
        bandNumber.ifPresent(i -> bands.get(i).incrementAndGet());
    }
}