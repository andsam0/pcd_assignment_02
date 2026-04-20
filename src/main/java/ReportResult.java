import java.util.List;

public record ReportResult(int numFiles, List<Long> numFilesPerBand) {}
