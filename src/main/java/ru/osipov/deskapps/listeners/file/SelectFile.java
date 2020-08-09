package ru.osipov.deskapps.listeners.file;

import ru.osipov.deskapps.App;
import ru.osipov.deskapps.json.InvalidJsonDocumentException;
import ru.osipov.deskapps.json.SimpleJsonParser;
import ru.osipov.deskapps.json.jsElements.JsonObject;
import ru.osipov.deskapps.lexers.DFALexer;
import ru.osipov.deskapps.lexers.Token;
import ru.osipov.deskapps.vocabularies.StyleRule;
import ru.osipov.deskapps.vocabularies.TextWeight;
import ru.osipov.deskapps.vocabularies.Vocabulary;
import ru.osipov.deskapps.vocabularies.Word;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SelectFile implements ActionListener {

    private Component p;
    private JTextPane editor;
    public SelectFile(Component component, JTextPane editor){
        this.p = component;
        this.editor = editor;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(p);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + f.getAbsolutePath());
//            try(BufferedInputStream s = new BufferedInputStream(new FileInputStream(f))){
//                    Document doc = editor.getDocument();
//                    byte[] b = new byte[255];
//                    s.read(b,0,255);
//                    doc.insertString(doc.getLength(), new String(b).toString(), null);
//            }
//            catch (IOException | BadLocationException ex){
//                JOptionPane.showMessageDialog(p,"Cannot open file!");
//            }
            if(editor == null){
                SimpleJsonParser parser = new SimpleJsonParser();
                System.out.println("No editor");
                System.out.println(f.getName());
                System.out.println(f.getAbsolutePath());
                JsonObject ob = parser.parse(f);
                if(ob != null){
                    try {
                        Vocabulary v = new Vocabulary(ob);
                        if(p instanceof App){//Vocabulary was created.
                            ((App) p).setVocabulary(v);//Save it into App
                            ((App) p).initStyles();//And add styles to editors.
                        }
                        JOptionPane.showMessageDialog(p,"Vocabulary was successful loaded!");
                    }catch (InvalidJsonDocumentException err){
                        System.out.println(err.getMessage());
                    }
                }
                else
                    JOptionPane.showMessageDialog(p, "Cannot create vocabulary!\n\tCannot parse to JsonDocument!");
            }
            else{//write file content to specified editor.
                if(p instanceof App){
                    Vocabulary v = ((App) p).getCurrentVocabulary();
                    if(v != null){
                        DFALexer lexer = new DFALexer(v);
                        Document doc = editor.getDocument();
                        lexer.setTarget(doc);
                        Token t = new Token(null,"ini",'i');
                        try(FileInputStream fl = new FileInputStream(f)){//READ FROM FILE
                            while(!t.getLexem().equals("$")) {//while not(EOF)
                                t = lexer.recognize(fl);
                                while(t == null)
                                    t = lexer.recognize(fl);
                                String id = t.getName();
                                Word w = v.getWords().get(id);
                                if(w != null){
                                    String cl = w.getClassName();
                                    Style s = null;
                                    if(cl != null){
                                        s = editor.getStyle(cl);
                                    }
                                    else
                                        s = editor.getStyle("#"+id);
                                    doc.insertString(doc.getLength(),t.getLexem(),s);

                                }
                                else
                                    doc.insertString(doc.getLength(),t.getLexem(),null);
                            }
                        }
                        catch (IOException ex){
                            System.out.println("Cannot open file.");
                        }
                        catch (BadLocationException ex){
                            System.out.println("Cannot apply styles!");
                        }
                    }
                }
            }
        }
    }
}
