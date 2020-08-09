package ru.osipov.deskapps.listeners.file;

import ru.osipov.deskapps.App;
import ru.osipov.deskapps.lexers.DFALexer;
import ru.osipov.deskapps.lexers.JTextPaneReader;
import ru.osipov.deskapps.lexers.LookAheadBufferedLexer;
import ru.osipov.deskapps.lexers.Token;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class CompareTwoFiles implements ActionListener {

    private Document d1;
    private Document d2;
    private App p;
    private DFALexer l;

    private Style style;

    public CompareTwoFiles(App parent,Document d1, Document d2, DFALexer l,Style s){
        this.d1 = d1;
        this.d2 = d2;
        this.l = l;
        this.style = s;
        this.p = parent;
    }

    public Document getDoc1() {
        return d1;
    }

    public Document getDoc2() {
        return d2;
    }

    public void setDoc1(Document d1) {
        this.d1 = d1;
    }

    public void setDoc2(Document d2) {
        this.d2 = d2;
    }

    public void setLexer(DFALexer lex){
        this.l = lex;
    }

    public void setStyle(Style s){
        this.style = s;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(p.getCurrentVocabulary() == null){
            JOptionPane.showMessageDialog(p,"For comparison you must SELECT a Vocabulary" +
                    "And Specify two files. In order to specify two files for comparison" +
                    "you MUST OPEN them.");
            return;
        }
        if(!p.isSelected1() || !p.isSelected2()){
            JOptionPane.showMessageDialog(p,"You must open File 1 and File 2 for comparison");
            return;
        }
        try {
            DFALexer l2 = (DFALexer)l.clone();
            JTextPaneReader r1 = new JTextPaneReader(d1);
            JTextPaneReader r2 = new JTextPaneReader(d2);
            l.setIO(r1);
            l2.setIO(r2);

            //READ FIRST Tokens
            Token t1 = l.recognize(null);
            Token t2 = l2.recognize(null);
            //Skeep Comments.
            while(t1 == null || t2 == null){
                if(t1 == null)
                    t1 = l.recognize(null);
                else
                    t2 = l2.recognize(null);
            }

            while (!t1.getName().equals("$") || !t2.getName().equals("$")) {
                if (t1.getName().equals("$")) {//first file is Ended.
                    int p = r2.getPos();
                    p = p - t2.getLexem().length();
                    d2.remove(p,t2.getLexem().length());
                    d2.insertString(p,t2.getLexem(),style);

                    //Read NEXT token from File 2.
                    t2 = l2.recognize(null);
                    //Skeep Comments.
                    while (t2 == null) {
                        t2 = l2.recognize(null);
                    }
                } else if (t2.getName().equals("$")) {
                    int p = r1.getPos();
                    p = p - t1.getLexem().length();
                    d1.remove(p,t1.getLexem().length());
                    d1.insertString(p,t1.getLexem(),style);

                    //Read NEXT token from File 1.
                    t1 = l.recognize(null);
                    //Skeep Comments.
                    while (t1 == null) {
                        t1 = l.recognize(null);
                    }
                } else {
                    //IF Tokens differs from one another.
                    if (!compareTokens(t1, t2)) {
                        int p1 = r1.getPos();
                        int p2 = r2.getPos();
                        p1 = p1 - t1.getLexem().length();
                        p2 = p2 - t2.getLexem().length();
                        System.out.println("Difference at "+p1+" | "+p2);
                        System.out.println(t1.getLexem()+" | "+t2.getLexem());
                        d1.remove(p1,t1.getLexem().length());
                        d2.remove(p2,t2.getLexem().length());
                        d1.insertString(p1, t1.getLexem(),style);
                        d2.insertString(p2, t2.getLexem(),style);
                    }

                    //Read NEXT Tokens.
                    t1 = l.recognize(null);
                    t2 = l2.recognize(null);
                    //Skeep Comments.
                    while (t1 == null || t2 == null) {
                        if (t1 == null)
                            t1 = l.recognize(null);
                        else
                            t2 = l2.recognize(null);
                    }
                }
            }
        } catch (CloneNotSupportedException ex) {
            System.out.println("Cannot copy lexer!");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Cannot read");
        } catch (BadLocationException ex) {
            System.out.println("Cannot find position in document!");
            ex.printStackTrace();
        }
        finally {
            l.setIO(new LookAheadBufferedLexer());
        }
    }

    private boolean compareTokens(Token t1, Token t2){
        return (t1.getName().equals(t2.getName())) && (t1.getLexem().equals(t2.getLexem()));
    }
}
