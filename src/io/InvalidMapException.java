package io;

public class InvalidMapException extends Exception {

    private final int lineNumber;

    public InvalidMapException(int lineNumber, String message) {
        super("Harita Dosyası Hatası (Satır " + lineNumber + "): " + message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
