import java.util.concurrent.Future;

public interface AsyncReportCalculator {

    Future<Report> getFSReport(String directory, long maxFS, int bands);
}
