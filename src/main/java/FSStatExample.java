import java.io.*;

public class FSStatExample {

    public static void main(String[] args) {

        if(args.length != 3){
            System.err.println("Usage: fstat <directory> <max_fs> <nb>");
            System.exit(1);
        }

        File folder = new File(args[0]);
        long maxFS = Long.parseLong(args[1]);
        int NB = Integer.parseInt(args[2]);

        ReportResult result = new FSStat().getFSReport(folder, maxFS, NB);

        System.out.println("Total files: " + result.numFiles());
        for (int i = 0; i < NB; i++) {
            long lo = i * maxFS / NB;
            long hi = (i + 1) * maxFS / NB;
            System.out.printf("Band %d [%d, %d): %d files%n",
                    i, lo, hi, result.numFilesPerBand().get(i));
        }
        System.out.printf("Overflow (>= %d): %d files%n",
                maxFS, result.numFilesPerBand().get(NB));
        }
}
