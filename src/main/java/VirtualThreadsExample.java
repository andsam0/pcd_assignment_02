import java.util.concurrent.ExecutionException;

public class VirtualThreadsExample {
    public static void main(String[] args){
        String directory = args[0];
        long maxFS = Long.parseLong(args[1]);
        long bands = Long.parseLong(args[2]);

        long start = System.currentTimeMillis();

        Report report;
        AsyncReportCalculator virtualThreadsReport = new InteractiveVirtualThreadsReport();
        try {
            report = virtualThreadsReport.getFSReport(directory, maxFS, (int)bands).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Total files: " + report.numFiles());
        for (int i = 0; i < bands; i++) {
            long lo = i * maxFS / bands;
            long hi = (i + 1) * maxFS / bands;
            System.out.printf("Band %d [%d, %d): %d files%n",
                    i, lo, hi, report.numFilesPerBand().get(i));
        }
        System.out.printf("Overflow (>= %d): %d files%n",
                maxFS, report.numFilesPerBand().get((int) bands));
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}
