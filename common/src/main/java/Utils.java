import org.apache.commons.codec.digest.DigestUtils;

public class Utils {

    public static String getSha256(String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }
}
