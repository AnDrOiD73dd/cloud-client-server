import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileService {

    public static String getWorkingDirectory() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
//        return System.getProperty("user.dir");
    }

    public static boolean isExists(String path) {
        return Files.exists(Paths.get(path));
    }

    public static boolean createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.println("Не могу создать директорию: " + path.toString());
            return false;
        }
        return true;
    }

    public static long getSize(String path) throws IOException {
        return Files.size(Paths.get(path));
    }

    public static long getDate(String path) throws IOException {
        return Files.getLastModifiedTime(Paths.get(path)).toMillis();
    }

    public static String getName(String path) {
        return Paths.get(path).getFileName().toString();
    }
}
