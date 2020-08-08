package ru.osipov.deskapps.json.jsElements;

public class JsonNull extends JsonElement<String> {
    @Override
    public String getValue() {
        return "null";
    }
}
