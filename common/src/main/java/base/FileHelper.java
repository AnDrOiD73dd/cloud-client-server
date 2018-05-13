package base;

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

    public static String generateServerFileName(String filePath) {
        Path filename = Paths.get(filePath).getFileName();
        long time = System.currentTimeMillis();
        return String.valueOf(time) + "-" + filename.toString();
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

    public static long getSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
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

    public static long getDate(String filePath) throws IOException {
        return Files.getLastModifiedTime(Paths.get(filePath)).toMillis();
    }

    public static String getName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }

    public static boolean deleteLocalFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!isExists(path.toAbsolutePath().toString()))
            return false;
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            System.out.println("Не могу удалить файл: " + e.getMessage());
            return false;
        }
    }

    public static byte[] convertToByteArray(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Произошла ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }
}
