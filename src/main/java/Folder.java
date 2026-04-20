import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public record Folder(List<Folder> subFolders, List<File> files) {

    public static Folder fromDirectory(File dir) throws IOException {
        List<File> files = new LinkedList<File>();
        List<Folder> subFolders = new LinkedList<Folder>();
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
                subFolders.add(Folder.fromDirectory(entry));
            } else {
                files.add(entry);
            }
        }
        return new Folder(subFolders, files);
    }
}

