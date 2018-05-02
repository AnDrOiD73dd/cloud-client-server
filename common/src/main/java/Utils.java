import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

    public static String getSha256(String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }

    public static long getFreeSpace() {
        File file = new File(FileHelper.getWorkingDirectory());
        return file.getUsableSpace();
    }

    public static Date getDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return new Date(milliseconds);
    }
}
