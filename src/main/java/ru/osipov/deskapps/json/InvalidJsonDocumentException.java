package ru.osipov.deskapps.json;

public class InvalidJsonDocumentException extends RuntimeException {
    public InvalidJsonDocumentException(String message, Throwable throwable){
        super(message,throwable);
    }
}
