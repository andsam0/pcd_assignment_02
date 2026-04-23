import java.util.OptionalInt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;


public class Sequential {
    static public void getFSReport(String directory, long maxFileSize, int numberOfBands) {
        Report report = new AtomicReport(numberOfBands);
        Path path = Path.of(directory);
        List<Long> allSize;
        try (Stream<Path> stream = Files.walk(path)) {
            allSize = stream
                    .filter(Files::isRegularFile)
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
            OptionalInt band = size > maxFileSize ? OptionalInt.empty() : OptionalInt.of((int) (size * numberOfBands / maxFileSize));
            report.incrementNumberOfFiles(band);
        }
        System.out.println("Number of files: " + report.getNumberOfFile());
        System.out.println("List of sizes: " + report.getBands().stream().toList());
    }

    public static void main(String[] args){
        getFSReport("./", 100000, 10);
    }

}
