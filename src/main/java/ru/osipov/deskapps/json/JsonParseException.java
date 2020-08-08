package ru.osipov.deskapps.json;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String message, Throwable throwable){
        super(message,throwable);
    }
}
