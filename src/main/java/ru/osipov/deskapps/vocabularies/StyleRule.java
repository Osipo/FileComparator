package ru.osipov.deskapps.vocabularies;

public class StyleRule {
    private String selector;//id.

    //Text properties
    private String color;
    private String font;
    private Integer size;
    private TextWeight weight;//bold, italic or normal.

    public StyleRule(String sel){
        this.selector = sel;
        this.color = null;
        this.font = null;
        this.size = null;
        this.weight = null;
    }

    public String getSelector(){
        return selector;
    }

    //Text properties (getters and setters)

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public TextWeight getWeight() {
        return weight;
    }

    public void setWeight(TextWeight weight) {
        this.weight = weight;
    }

    @Override
    public String toString(){
        return "{" +
                "color: " + color +
                ", font: " + font +
                ", size: " + size +
                ", weight: " + weight +
                "}";
    }
}
