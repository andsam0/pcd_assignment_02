public class EventLoopExample {

    public static void main(String[] args) {
        String directory = args[0];
        long maxFS = Long.parseLong(args[1]);
        long bands = Long.parseLong(args[2]);

        long start = System.currentTimeMillis();
        EventLoop eventLoop = new EventLoop();
        eventLoop.getFSReport(directory, maxFS, (int) bands).onComplete(asyncResult -> {
            ReportResult report = asyncResult.result();

            System.out.println("Total files: " + report.numFiles());
            for (int i = 0; i < bands; i++) {
                long lo = i * maxFS / bands;
                long hi = (i + 1) * maxFS / bands;
                System.out.printf("Band %d [%d, %d): %d files%n",
                        i, lo, hi, report.bands().get(i));
            }
            System.out.printf("Overflow (>= %d): %d files%n",
                    maxFS, report.numFiles() - report.bands().subList(0, (int) (bands - 1)).stream().mapToLong(Long::longValue).sum());
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        });
    }
}