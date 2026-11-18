package io;

public class InvalidCommandException extends Exception {

    private final int lineNumber;

    public InvalidCommandException(int lineNumber, String message) {
        super("Komut Dosyası Hatası (Satır " + lineNumber + "): " + message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
