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
                        if(p instanceof App){
                            ((App) p).setVocabulary(v);
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
                        lexer.setTarget(editor);
                        Token t = new Token(null,"ini",'i');
                        try(FileInputStream fl = new FileInputStream(f)){//READ FROM FILE
                            while(!t.getLexem().equals("$")) {//while not(EOF)
                                t = lexer.recognize(fl);
                                while(t == null)
                                    t = lexer.recognize(fl);
                                String id = t.getName();
                                System.out.println(t.getLexem());
                                if(id.equals("keywords")){
                                    StyleRule r = v.getStyles().get("keywords");
                                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                                    setStyle(r,attrs);
                                    StyledDocument doc = editor.getStyledDocument();
                                    doc.insertString(doc.getLength(),t.getLexem(),attrs);
                                    continue;
                                }
                                Word w = v.getWords().get(id);
                                if(w != null){
                                    StyleRule r = v.getStyles().get("#"+id);
                                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                                    setStyle(r,attrs);
                                    String cl = w.getClassName();
                                    if(cl != null){
                                        StyleRule rc = v.getStyles().get(cl);
                                        setStyle(rc,attrs);
                                    }
                                    StyledDocument doc = editor.getStyledDocument();
                                    doc.insertString(doc.getLength(),t.getLexem(),attrs);
                                }
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

    private void setStyle(StyleRule rule,SimpleAttributeSet attrs){
        if(rule != null) {
            String c = rule.getColor() == null ? "BLACK" : rule.getColor();
            String f = rule.getFont() == null ? "Serif" : rule.getFont();
            int sz = rule.getSize() == null ? 14 : rule.getSize();
            TextWeight w = rule.getWeight() == null ? TextWeight.NORMAL : rule.getWeight();
            StyleConstants.setForeground(attrs, Color.getColor(c.toUpperCase()));
            StyleConstants.setFontSize(attrs,sz);
            StyleConstants.setFontFamily(attrs,f);
            switch(w){
                case BOLD: StyleConstants.setBold(attrs,true); break;
                case ITALIC:StyleConstants.setItalic(attrs,true); break;
                case NORMAL: default:{
                    StyleConstants.setItalic(attrs,false);
                    StyleConstants.setBold(attrs,false);
                    break;
                }
            }
        }
    }
}
