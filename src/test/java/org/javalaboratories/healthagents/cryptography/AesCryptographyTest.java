package org.javalaboratories.healthagents.cryptography;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AesCryptographyTest {

    private AesCryptography cryptography;
    private static final String SECURITY_PASSWD_FILE = "security-passwd.yml";

    @Test
    public void testAesCryptography_EncryptString_Pass() {
        String s = "This is a test of encryption, but should be ok";

        String result = AesCryptography.encrypt(s);
        assertEquals("2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6",result);
    }

    @Test
    public void testAesCryptography_EncryptStream_Pass() {
        InputStream s = new ByteArrayInputStream("This is a test of encryption, but should be ok".getBytes());

        String result = AesCryptography.encrypt(s);
        assertEquals("2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6",result);
    }

    @Test
    public void testAesCryptography_DecryptString_Pass() {
        String s = "2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6";

        String result = AesCryptography.decrypt(s);
        assertEquals("This is a test of encryption, but should be ok",result);
    }

    @Test
    public void testAesCryptography_DecryptStream_Pass() {
        InputStream s = new ByteArrayInputStream("2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6".getBytes());

        String result = AesCryptography.decrypt(s);
        assertEquals("This is a test of encryption, but should be ok",result);
    }

    @Disabled("Only enable to create the encrypted security identity file; move the resultant file to the Resources directory")
    public void utilityMethod_EncryptedSecurityPasswdYanl() throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(SECURITY_PASSWD_FILE)) {
            int c;
            StringBuilder buffer = new StringBuilder();
            while ( (c = stream.read()) != -1 ) {
                buffer.append((char) c);
            }
            String data = AesCryptography.encrypt(buffer.toString());
            Path path = Paths.get(SECURITY_PASSWD_FILE+".enc");
            Files.write(path,data.getBytes());
        }
    }
}
