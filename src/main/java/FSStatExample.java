import java.io.*;

public class FSStatExample {

    public static void main(String[] args) throws IOException {

        if(args.length != 3){
            System.err.println("Usage: fstat <directory> <max_fs> <nb>");
            System.exit(1);
        }

        FSStat fsStat = new FSStat();

        Folder folder = Folder.fromDirectory(new File(args[0]));
        long maxFs = Long.parseLong(args[1]);
        long nb = Long.parseLong(args[2]);

        ReportResult result = fsStat.getFSReport(folder,maxFs,nb);

        System.out.println("Numero di file: "+result.numFiles());
        for(int band = 1; band <= nb; band++){
            System.out.println("Numero di file per la banda "+band+" ("+(band-1)*maxFs/nb+"): "+result.numFilesPerBand().get(band-1));
        }
//        System.out.println("Numero di file per banda: "+result.numFilesPerBand());


//        final int repeatCount = Integer.parseInt(args[2]);
//        long counts;
//        long startTime;
//        long stopTime;
//
//        long[] singleThreadTimes = new long[repeatCount];
//        long[] forkedThreadTimes = new long[repeatCount];

//        for (int i = 0; i < repeatCount; i++) {
//            startTime = System.currentTimeMillis();
//            counts = wordCounter.countOccurrencesOnSingleThread(folder, args[1]);
//            stopTime = System.currentTimeMillis();
//            singleThreadTimes[i] = (stopTime - startTime);
//            System.out.println(counts + " , single thread search took " + singleThreadTimes[i] + "ms");
//        }
//
//        for (int i = 0; i < repeatCount; i++) {
//            startTime = System.currentTimeMillis();
//            counts = wordCounter.countOccurrencesInParallel(folder, args[1]);
//            stopTime = System.currentTimeMillis();
//            forkedThreadTimes[i] = (stopTime - startTime);
//            System.out.println(counts + " , fork / join search took " + forkedThreadTimes[i] + "ms");
//        }
//
//        System.out.println("\nCSV Output:\n");
//        System.out.println("Single thread,Fork/Join");
//        for (int i = 0; i < repeatCount; i++) {
//            System.out.println(singleThreadTimes[i] + "," + forkedThreadTimes[i]);
//        }
//        System.out.println();
    }
}
