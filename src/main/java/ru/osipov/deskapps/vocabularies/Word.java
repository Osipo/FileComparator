package ru.osipov.deskapps.vocabularies;

public class Word {
    private String pattern;
    private String className;
    private String id;

    public Word(String id, String pattern, String className){
        this.id = id;
        this.pattern = pattern;
        this.className = className;
    }

    public Word(String id){
        this(id,null,null);
    }


    public void setPattern(String pattern){
        this.pattern = pattern;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getPattern() {
        return pattern;
    }
}
