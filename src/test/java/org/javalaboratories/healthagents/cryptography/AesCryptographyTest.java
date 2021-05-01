package org.javalaboratories.healthagents.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AesCryptographyTest {

    private AesCryptography cryptography;

    @Test
    public void testAesCryptography_Encrypt_Pass() {
        String s = "This is a test of encryption, but should be ok";

        String result = AesCryptography.encrypt(s);
        assertEquals("2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6",result);
    }

    @Test
    public void testAesCryptography_Decrypt_Pass() {
        String s = "2zlIfm+Xo4miqwA4Azkl9Q5sXq1xIiP7zHwd4KrKDkn4jF4ebERaMS0DOR4BlYs6";

        String result = AesCryptography.decrypt(s);
        assertEquals("This is a test of encryption, but should be ok",result);
    }
}
