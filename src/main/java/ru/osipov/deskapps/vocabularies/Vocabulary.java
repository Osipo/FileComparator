package ru.osipov.deskapps.vocabularies;

import ru.osipov.deskapps.json.InvalidJsonDocumentException;
import ru.osipov.deskapps.json.jsElements.*;
import java.util.*;

public class Vocabulary {
    private Set<String> ids;
    private Map<String,Set<String>> classes;
    private Map<String,Word> words;
    private String empty;
    private Map<String,StyleRule> styles;

    private Set<String> keywords;
    private Set<String> exts;
    private String comment;
    private String mlCommentStart;
    private String mlCommentEnd;

    public Vocabulary(JsonObject ob){
        this.ids = new HashSet<>();
        this.classes = new HashMap<>();
        this.keywords = new HashSet<>();
        this.words = new HashMap<>();
        this.exts = new HashSet<>();
        this.styles = new HashMap<>();
        this.empty = null;
        JsonElement W = ob.getElement("words");
        if(W instanceof JsonArray){
            ArrayList<JsonElement> ws = ((JsonArray) W).getElements();
            for(JsonElement el : ws){
                if(el instanceof JsonObject){
                    JsonObject i = (JsonObject) el;
                    JsonElement id = i.getElement("id");
                    Word w_i = null;
                    if(id instanceof JsonString){
                        w_i = new Word(((JsonString) id).getValue());
                    }
                    else
                        throw new InvalidJsonDocumentException("property name \"id\" with string value is required in object (elements of \"words\")! ",null);

                    JsonElement cls = i.getElement("className");

                    if(cls instanceof JsonString){
                        w_i.setClassName(((JsonString) cls).getValue());
                    }
                    else if(cls != null)
                        throw new InvalidJsonDocumentException("property \"className\" must have a String value! ",null);

                    JsonElement p = i.getElement("pattern");
                    if(p instanceof JsonString){
                        if(((JsonString) p).getValue().equals("")){
                            if(empty == null) {
                                this.empty = ((JsonString) id).getValue();
                                continue;
                            }
                            else
                                throw new InvalidJsonDocumentException("Cannot add new empty symbol (Symbol of empty string)! Only one symbol is permitted!",null);
                        }
                        w_i.setPattern(((JsonString) p).getValue());
                    }
                    else if(p == null && this.empty == null){
                        this.empty = ((JsonString) id).getValue();
                        continue;
                    }
                    else if(p == null){
                        throw new InvalidJsonDocumentException("Cannot add new empty symbol (Symbol of empty string)! Only one symbol is permitted!",null);
                    }
                    else
                        throw new InvalidJsonDocumentException("property name \"pattern\" with string value is required in object (elements of \"words\")! ",null);

                    this.ids.add(((JsonString) id).getValue());
                    if(cls != null){
                        Set<String> c_ids = this.classes.get(((JsonString) cls).getValue());
                        if(c_ids != null)
                            c_ids.add(((JsonString) id).getValue());
                        else {
                            c_ids = new HashSet<>();
                            c_ids.add(((JsonString) id).getValue());
                            classes.put(((JsonString) cls).getValue(), c_ids);
                        }
                    }
                    this.words.put(((JsonString) id).getValue(),w_i);
                }
                else
                    throw new InvalidJsonDocumentException("Elements of array \"words\" must be JsonObjects!",null);
            }
        }
        else
            throw new InvalidJsonDocumentException("Required property name \"words\" with JsonArray value.",null);

        //Optional properties "keywords" and "extensions"
        JsonElement kws = ob.getElement("keywords");
        if(kws instanceof JsonArray){
            ArrayList<JsonElement> kwsarr = ((JsonArray) kws).getElements();
            for(JsonElement el: kwsarr){
                if(el instanceof JsonString){
                    keywords.add(((JsonString) el).getValue());
                }
                else
                    throw new InvalidJsonDocumentException("Elements of array \"keywords\" must be JsonStrings!",null);
            }
        }
        else if(kws != null)
            throw new InvalidJsonDocumentException("Property name \"keywords\" must be JsonArray! ",null);
        JsonElement extensions = ob.getElement("extensions");
        if(extensions instanceof JsonArray){
            ArrayList<JsonElement> extarr = ((JsonArray) extensions).getElements();
            for(JsonElement el: extarr){
                if(el instanceof JsonString){
                    exts.add(((JsonString) el).getValue());
                }
                else
                    throw new InvalidJsonDocumentException("Elements of array \"extensions\" must be JsonStrings!",null);
            }
        }
        else if(extensions != null)
            throw new InvalidJsonDocumentException("Property name \"extensions\" must be JsonArray! ",null);

        JsonElement styles = ob.getElement("styles");
        if(styles instanceof JsonArray){
            ArrayList<JsonElement> sarr = ((JsonArray) styles).getElements();
            for(JsonElement el : sarr){
                if(el instanceof JsonObject){
                    JsonObject style = (JsonObject) el;
                    JsonElement sel = style.getElement("selector");
                    if(sel instanceof JsonString){
                        StyleRule s_el = this.styles.get(((JsonString) sel).getValue());
                        if(s_el != null){
                            initStyleObj(s_el,style);
                        }
                        else {
                            s_el = new StyleRule(((JsonString) sel).getValue());
                            initStyleObj(s_el,style);
                            this.styles.put(((JsonString) sel).getValue(), s_el);
                        }
                    }
                    else
                        throw new InvalidJsonDocumentException("Property name \"selector\" with String value is required in object (elements of \"styles\")! ",null);
                }
                else
                    throw new InvalidJsonDocumentException("Elements of array \"styles\" must be JsonString! ",null);
            }
        }
        else if(styles != null)
            throw new InvalidJsonDocumentException("Property name \"styles\" must be JsonArray! ",null);

        //Optional properties: comment, mlCommentStart, mlCommentEnd
        JsonElement comment = ob.getElement("comment");
        if(comment instanceof JsonString){
            this.comment = ((JsonString) comment).getValue();
            if(!this.ids.contains(this.comment))
                throw new InvalidJsonDocumentException("The value of property \"comment\" must be an id of a word! ",null);
        }
        else if(comment != null)
            throw new InvalidJsonDocumentException("Property name \"comment\" must be JsonString! ",null);
        JsonElement mlcs = ob.getElement("mlCommentStart");
        if(mlcs instanceof JsonString){
            this.mlCommentStart = ((JsonString) mlcs).getValue();
            if(!this.ids.contains(this.mlCommentStart))
                throw new InvalidJsonDocumentException("Property name \"mlCommentStart\" must be an id of a word! ",null);

            JsonElement mlce = ob.getElement("mlCommentEnd");
            if(mlce instanceof JsonString){
                this.mlCommentEnd = ((JsonString) mlce).getValue();
            }
            else if(mlce != null)
                throw new InvalidJsonDocumentException("Property name \"mlCommentEnd\" must be JsonString! ",null);
            else
                throw new InvalidJsonDocumentException("When property name \"mlCommentStart\" is defined the property \"mlCommentEnd\" must be defined too! ",null);
        }
        else if(mlcs != null)
            throw new InvalidJsonDocumentException("Property name \"mlCommentStart\" must be JsonString! ",null);

        System.out.println("Vocabulary was built.");
        System.out.println(this);
    }

    private void initStyleObj(StyleRule s, JsonObject ob) throws InvalidJsonDocumentException{
        JsonElement col = ob.getElement("color");
        if(col instanceof JsonString){
            String v = ((JsonString) col).getValue();
            if(s.getColor() != null)
                throw new InvalidJsonDocumentException("Cannot override value of property \"color\" "+s.getColor()+" with" + v
                        + "\n for selector "+s.getSelector(),null);
            s.setColor(v);
        }
        else if(col != null)
            throw new InvalidJsonDocumentException("Property name \"color\" must have a String value! ",null);
        JsonElement font = ob.getElement("font");
        if(font instanceof JsonString){
            String v = ((JsonString) font).getValue();
            if(s.getFont() != null)
                throw new InvalidJsonDocumentException("Cannot override value of property \"font\" "+s.getFont()+" with" + v
                        + "\n for selector "+s.getSelector(),null);
            s.setFont(v);
        }
        else if(font != null)
            throw new InvalidJsonDocumentException("Property name \"font\" must have a String value! ",null);
        JsonElement size = ob.getElement("size");
        if(size instanceof JsonNumber){
            Integer v = ((JsonNumber) size).getValue().intValue();
            if(s.getSize() != null)
                throw new InvalidJsonDocumentException("Cannot override value of property \"size\" "+s.getSize()+" with" + v
                        + "\n for selector "+s.getSelector(),null);
            s.setSize(v);
        }
        else if(size != null)
            throw new InvalidJsonDocumentException("Property name \"size\" must have a Numeric value! ",null);
        JsonElement weight = ob.getElement("weight");
        if(weight instanceof JsonString){
            TextWeight v = TextWeight.valueOf(((JsonString) weight).getValue().toUpperCase());
            if(s.getWeight() != null)
                throw new InvalidJsonDocumentException("Cannot override value of property \"weight\" "+s.getWeight()+" with" + v
                        + "\n for selector "+s.getSelector(),null);
            s.setWeight(v);
        }
        else if(weight != null)
            throw new InvalidJsonDocumentException("Property name \"weight\" must have a String value! ",null);
    }

    public Map<String, Set<String>> getClasses() {
        return classes;
    }

    public Set<String> getIds() {
        return ids;
    }

    public Map<String, StyleRule> getStyles() {
        return styles;
    }

    public Map<String, Word> getWords() {
        return words;
    }

    public Set<String> getExts() {
        return exts;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public String getEmpty(){
        return empty;
    }


    public String getComment() {
        return comment;
    }

    public String getMlCommentStart(){return mlCommentStart;}

    public String getMlCommentEnd() {
        return mlCommentEnd;
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("Vocabulary {\n\tterms: {");
        Set<String> ws = words.keySet();
        for(String k : ws){
            b.append("\n\t\t").append(k).append(" -> ").append(words.get(k).getPattern());
        }
        b.append("\n\t}");
        b.append("\n\tclasses: {");
        Set<String> cws = classes.keySet();
        for(String k : cws){
            b.append("\n\t\t").append(k).append(" -> ").append(classes.get(k));
        }
        b.append("\n\t}");
        b.append("\n\tempty: ").append(this.empty);
        b.append("\n\tstyles: {");
        Set<String> ks = styles.keySet();
        for(String k : ks){
            b.append("\n\t\t").append(k).append(styles.get(k));
        }
        b.append("\n\t}");
        b.append("\n\tkeywords: ").append(this.keywords);
        b.append("\n\textensions: ").append(this.exts);
        b.append("\n}");
        return b.toString();
    }
}