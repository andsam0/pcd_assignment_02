import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

public class EventLoopExample {

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        EventLoop eventLoop = new EventLoop();
        eventLoop.getFSReport(args[0], Long.parseLong(args[1]), Integer.parseInt(args[2]));

        for (Long size : allSize) {
            report.incrementNumberOfFiles(bandIndex(size, maxFS, bands));
        }

        System.out.println("Total files: " + report.numFiles());
        for (int i = 0; i < (long) bands; i++) {
            long lo = i * maxFS / (long) bands;
            long hi = (i + 1) * maxFS / (long) bands;
            System.out.printf("Band %d [%d, %d): %d files%n",
                    i, lo, hi, report.numFilesPerBand().get(i));
        }
        System.out.printf("Overflow (>= %d): %d files%n",
                maxFS, report.numFiles() - report.numFilesPerBand().subList(0, (int) ((long) bands -1)).stream().mapToLong(Long::longValue).sum());

        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}