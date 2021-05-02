package org.javalaboratories.healthagents.cryptography;

public class IllegalCryptographyStateException extends RuntimeException {

    public IllegalCryptographyStateException() {
        super();
    }

    public IllegalCryptographyStateException(String message, Throwable e) {
        super(message,e);
    }
}
