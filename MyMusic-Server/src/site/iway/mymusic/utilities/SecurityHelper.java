package site.iway.mymusic.utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.CRC32;

public class SecurityHelper {

    public static byte[] md5(byte[] data, int startIndex, int count) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            md.update(data, startIndex, count);
            byte[] digestBytes = md.digest();
            return digestBytes;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] md5(byte[] data) {
        return md5(data, 0, data.length);
    }

    public static byte[] sha1(byte[] data, int startIndex, int count) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            md.update(data, startIndex, count);
            byte[] digestBytes = md.digest();
            return digestBytes;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] sha1(byte[] data) {
        return sha1(data, 0, data.length);
    }

    public static long crc32(byte[] data, int startIndex, int count) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, startIndex, count);
        return crc32.getValue();
    }

    public static long crc32(byte[] data) {
        return crc32(data, 0, data.length);
    }

    public static byte[] tripleDESEncrypt(byte[] data, byte[] key) {
        try {
            String algorithm = "DESede";
            SecretKey secretKey = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e1) {
            return null;
        }
    }

    public static byte[] tripleDESDecrypt(byte[] data, byte[] key) {
        try {
            String algorithm = "DESede";
            SecretKey secretKey = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e1) {
            return null;
        }
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] base64Decode(String string) {
        return Base64.getDecoder().decode(string);
    }

}
