import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;

public class Utils {

    public static String getSha256(String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }

    public static long getFreeSpace() {
        File file = new File(FileService.getWorkingDirectory());
        return file.getUsableSpace();
    }
}
