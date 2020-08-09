package ru.osipov.deskapps.lexers;

import java.io.IOException;
import java.io.InputStream;

//Do not be confused with IO. Lexer supports only Input operations!
//The name "IO" specify Input and Output operations ON internal BUFFER.
//The BUFFER is used for BACKTRACKING!
//Within a source (from lexer reads lexems) ONLY Input operations are available.
public interface LexerIO {
    int ungetch(char c);
    int getch(InputStream r) throws IOException;
    int getFilech(InputStream r) throws IOException;
    void clear();
    int getLine();
    int getCol();
    void setLine(int l);
    void setCol(int c);
    String getFromBuffer();
}
