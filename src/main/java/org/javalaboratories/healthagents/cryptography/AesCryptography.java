package org.javalaboratories.healthagents.cryptography;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AesCryptography {

    private final Logger logger = LoggerFactory.getLogger(AesCryptography.class);

    // Can be configured into Constant and read the configuration file for injection
    private static final String KEY = "0246810121416180";

    // The parameters represent the algorithm name / encryption mode / data filling method
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int MAX_BUFFER_SZ = 16;

    private AesCryptography() {}


    /**
     * Encrypts inputStream
     *
     * @param stream stream to be encrypted
     * @param encryptKey encryption secret key
     * @return encrypted resultant string from inputStream.
     */
    public static String encrypt(final InputStream stream, String encryptKey) {
        String result;
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            char[] buffer = new char[MAX_BUFFER_SZ];
            int read;
            StringBuilder b = new StringBuilder();
            while ((read = reader.read(buffer,0,MAX_BUFFER_SZ)) > -1) {
                b.append(buffer,0,read);
            }
            result = b.toString();
        } catch (IOException e) {
            throw new IllegalCryptographyStateException("Failed to encrypt input stream",e);
        }
        return encrypt(result,encryptKey);
    }

    /**
     * Encryption
     *
     * @param content encrypted string
     * @param encryptKey key value
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String encryptKey) {
        byte[] b;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
            b = cipher.doFinal(content.getBytes(UTF_8));
        } catch (Exception e) {
            throw new IllegalCryptographyStateException("Failed to encrypt content",e);
        }
        return Base64.encodeBase64String(b);
    }

    /**
     * Decrypt
     *
     * @param encryptStr decrypted string
     * @param decryptKey decrypted key value
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr, String decryptKey)  {
        byte[] decryptBytes;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            byte[] encryptBytes = Base64.decodeBase64(encryptStr);
            decryptBytes = cipher.doFinal(encryptBytes);
        } catch (Exception e) {
            throw new IllegalCryptographyStateException("Failed to decrypt content",e);
        }
        return new String(decryptBytes);
    }

    public static String encrypt(InputStream content) {
        return encrypt(content, KEY);
    }

    public static String encrypt(String content) {
        return encrypt(content, KEY);
    }

    public static String decrypt(String encryptStr) {
        return decrypt(encryptStr, KEY);
    }
}
