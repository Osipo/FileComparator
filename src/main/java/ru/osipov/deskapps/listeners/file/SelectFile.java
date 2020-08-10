package ru.osipov.deskapps.listeners.file;

import ru.osipov.deskapps.App;
import ru.osipov.deskapps.json.InvalidJsonDocumentException;
import ru.osipov.deskapps.json.SimpleJsonParser;
import ru.osipov.deskapps.json.jsElements.JsonObject;
import ru.osipov.deskapps.lexers.DFALexer;
import ru.osipov.deskapps.lexers.Token;
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
    private JLabel fname;
    private SelectedItem isFirstEditor;
    public SelectFile(Component component, JTextPane editor, SelectedItem isFirstEditor, JLabel fname){
        this.p = component;
        this.editor = editor;
        this.isFirstEditor = isFirstEditor;
        this.fname = fname;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(p);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            String fn = f.getAbsolutePath();
            System.out.println("Selected file: " + f.getAbsolutePath());
            if(fname != null) {
                if(fn.length() > 40){//Filename too long
                    String c = fn.substring(0,3) + "...\\" + f.getName();
                    fname.setText(c);
                }
                else
                    fname.setText(fn);
                fname.setToolTipText(fn);//Tip preserves full FileName.
            }
            if(editor == null){//Action from ComboBox.
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
                            ((App) p).setLexer(new DFALexer(v));
                        }
                        JOptionPane.showMessageDialog(p,"Vocabulary was successful loaded!");
                    }catch (InvalidJsonDocumentException err){
                        System.out.println(err.getMessage());//Cannot read from JsonDocument and write into Vocabulary.
                    }
                }
                else
                    JOptionPane.showMessageDialog(p, "Cannot create vocabulary!\n\tCannot parse to JsonDocument!");
            }
            else{//write file content to specified editor.
                if(p instanceof App){//check that we pass the Application GUI instance
                    Vocabulary v = ((App) p).getCurrentVocabulary();//get from GUI current Vocabulary.
                    if(isFirstEditor == SelectedItem.FIRST)
                         ((App) p).setSelected1(true);
                    else if(isFirstEditor == SelectedItem.SECOND)
                        ((App) p).setSelected2(true);
                    if(v != null){//Vocabulary was set.
                        DFALexer lexer = ((App) p).getLexer();//get built Lexer from App
                        Document doc = editor.getDocument();
                        lexer.setTarget(doc);
                        Token t = new Token(null,"ini",'i');
                        try(FileInputStream fl = new FileInputStream(f)){//READ FROM FILE
                            while(!t.getLexem().equals("$")) {//while not(EOF)
                                t = lexer.recognize(fl);
                                while(t == null)
                                    t = lexer.recognize(fl);
                                String id = t.getName();
                                System.out.println(id+"::"+t.getLexem());
                                Word w = v.getWords().get(id);
                                if(w != null){
                                    String cl = w.getClassName();
                                    Style s = null;
                                    if(cl != null){
                                        s = editor.getStyle(cl);
                                        if(s == null)
                                            s = editor.getStyle("#"+id);
                                    }
                                    else
                                        s = editor.getStyle("#"+id);
                                    doc.insertString(doc.getLength(),t.getLexem(),s);

                                }
                                else if(t.getName().equals("keywords")){
                                    Style s = editor.getStyle("keywords");
                                    doc.insertString(doc.getLength(),t.getLexem(),s);
                                }
                                else
                                    doc.insertString(doc.getLength(),t.getLexem(),null);
                            }
                            if(isFirstEditor == SelectedItem.FIRST)
                                ((App) p).getCompHandler().setDoc1(doc);
                            else if(isFirstEditor == SelectedItem.SECOND)
                                ((App) p).getCompHandler().setDoc2(doc);
                            ((App) p).getCompHandler().setStyle(editor.getStyle("comparison"));
                        }
                        catch (IOException ex){
                            System.out.println("Cannot open file.");
                        }
                        catch (BadLocationException ex){
                            System.out.println("Cannot apply styles!");
                        }
                        finally {
                            t = null;
                            v = null;
                            lexer.reset();
                            lexer = null;
                        }
                    }
                    //ELSE IF NO VOCABULARY => DO NOTHING.
                    //TODO: Make some action to process this situation!
                }
            }
        }
    }
}