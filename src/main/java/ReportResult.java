import java.util.List;

public record ReportResult(long numFiles, List<Long> numFilesPerBand) {}
