import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ReportResult(List<Long> bands, long numFiles){
    public static ReportResult emptyResult(int NB){
        return new ReportResult(new ArrayList<>(Collections.nCopies(NB, 0L)),0);
    }

    public ReportResult merge(ReportResult other){
        List<Long> newBands = new ArrayList<>();
        for(int i=0; i<other.bands.size(); i++){
            newBands.add(this.bands.get(i)+other.bands.get(i));
        }
        return new ReportResult(newBands, this.numFiles+other.numFiles);
    }
}