import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static String getSha256(String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }

    public static String getWorkingDirectory() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
//        return System.getProperty("user.dir");
    }
}
