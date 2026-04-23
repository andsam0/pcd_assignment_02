import java.util.OptionalInt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;


public class Sequential {
    static public void getFSReport(String directory, long maxFS, int bands) {
        Report report = new AtomicReport(bands);
        Path path = Path.of(directory);
        List<Long> allSize;
        try (Stream<Path> stream = Files.walk(path)) {
            allSize = stream
                    .filter(Files::isRegularFile) // skip symlinks and directories
                    .map(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Long size : allSize) {
            OptionalInt band = size > maxFS ? OptionalInt.empty() : OptionalInt.of((int) (size * bands / maxFS));
            report.incrementNumberOfFiles(band);
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
//        System.out.println("Number of files: " + report.getNumberOfFile());
//        System.out.println("List of sizes: " + report.getBands().stream().toList());
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        getFSReport(args[0], Long.parseLong(args[1]), Integer.parseInt(args[2]));
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

}
