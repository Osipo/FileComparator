package ru.osipov.deskapps.lexers;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.InputStream;

//READ SYMBOLS FROM Document Object (JTextPane content)
//BUT NOT FROM InputStream!
public class JTextPaneReader extends LookAheadBufferedLexer implements LexerIO {

    private Document source;
    private int pos;

    public JTextPaneReader(Document doc, int bsize){
        super(bsize);
        this.source = doc;
        this.pos = -1;
    }

    public JTextPaneReader(Document doc){
        super();
        this.source = doc;
        this.pos = -1;
    }

    @Override
    public int getch(InputStream r) throws IOException {
        if(bufp > 0)
            return buf[--bufp];
        else{
            col++;
            pos++;
            if(pos == source.getLength())
                return 65535;//EOF
            char c;
            try{
                c = source.getText(pos,1).charAt(0);//get one symbol [instead of (int)r.read();]
            } catch (BadLocationException e){
                return 65535;//EOF
            }
            //System.out.println((int)c);
            if(c == '\n'){
                line++; col = 0;
            }
            return c;
        }
    }

    @Override
    public int getFilech(InputStream r) throws IOException {
        char c;// = (char)r.read();
        pos++;
        if(pos == source.getLength())
            return 65535;//EOF
        try{
            c = source.getText(pos,1).charAt(0);
        } catch (BadLocationException e){
            return 65535;//EOF
        }
        if(c == '\n'){
            line += 1;
            col = 0;
        }
        else{
            col += 1;
        }
        return c;
    }
    @Override
    public void clear(){
        super.clear();
        this.pos = -1;
    }

    public int getPos(){
        return pos;
    }
}
