import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    public static String getWorkingDirectory() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
//        return System.getProperty("user.dir");
    }

    public static Path getUserDirectory(String rootCloudDirName, String username) {
        return Paths.get(getWorkingDirectory(), rootCloudDirName, username);
    }

    public static Path generateServerFilePath(String rootCloudDirName, String username, String filePath) {
        Path filename = Paths.get(filePath).getFileName();
        long time = System.currentTimeMillis();
        return Paths.get(getWorkingDirectory(), rootCloudDirName, username, String.valueOf(time) + "-" + filename.toString());
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

    public static String getHumanSize(long fileSize) {
        double size = (double) fileSize;
        String res = String.valueOf(size) + " B";
        double divider = 1024.0;
        int decimalPlaces = 2;
        if(size > divider) {
            size /= divider;
            String strSize = String.valueOf(size);
            res = strSize.substring(0, strSize.indexOf('.') + decimalPlaces) + " KB";
            if (size > divider) {
                size /= divider;
                strSize = String.valueOf(size);
                res = strSize.substring(0, strSize.indexOf('.') + decimalPlaces) + " MB";
                if (size > divider) {
                    size /= divider;
                    strSize = String.valueOf(size);
                    res = strSize.substring(0, strSize.indexOf('.') + decimalPlaces) + " GB";
                    if (size > divider) {
                        size /= divider;
                        strSize = String.valueOf(size);
                        res = strSize.substring(0, strSize.indexOf('.') + decimalPlaces) + " TB";
                    }
                }
            }
        }
        return res;
    }

    public static long getDate(String path) throws IOException {
        return Files.getLastModifiedTime(Paths.get(path)).toMillis();
    }

    public static String getName(String path) {
        return Paths.get(path).getFileName().toString();
    }
}
