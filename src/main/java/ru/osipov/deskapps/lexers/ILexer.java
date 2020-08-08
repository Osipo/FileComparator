package ru.osipov.deskapps.lexers;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public interface ILexer {
    Token recognize(InputStream f) throws IOException, BadLocationException;
    Token generateError(String s1, String s2);
    void reset();
    void setKeywords(Set<String> s);
    Set<String> getKeywords();
    void setCommentLine(String comment);
    void setMlCommentStart(String mlcS);
    void setMlCommentEnd(String mlcE);
}
