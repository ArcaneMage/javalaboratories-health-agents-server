package org.javalaboratories.healthagents.cryptography;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AesCryptographyTest {

    private AesCryptography cryptography;

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
}
