package ru.osipov.deskapps;

import ru.osipov.deskapps.json.SimpleJsonParser;
import ru.osipov.deskapps.json.jsElements.JsonObject;
import ru.osipov.deskapps.listeners.file.SelectFile;
import ru.osipov.deskapps.vocabularies.Vocabulary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.InputStream;

public class App extends JFrame {

    private JTextPane txtEditor1;
    private JTextPane txtEditor2;

    private JScrollPane txtwrapper1;
    private JScrollPane txtwrapper2;
    private Vocabulary currentV;

    private int w;
    private int h;


    public void setVocabulary(Vocabulary v){
        this.currentV = v;
    }

    public Vocabulary getCurrentVocabulary(){
        return currentV;
    }

    public App(){
        super("FileComparator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.w = 600;
        this.h = 600;
        setSize(new Dimension(w,h));
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        txtEditor1 = new JTextPane();
        txtwrapper1 = new JScrollPane(txtEditor1);
        txtEditor2 = new JTextPane();
        txtwrapper2 = new JScrollPane(txtEditor2);

        initLeft(pane);
        initCenter(pane);
        initToolBar(pane);
        pack();
        setVisible(true);
    }

    private void initLeft(Container frame){
        JPanel left = new JPanel();
        left.setMinimumSize(new Dimension((int)(w*0.2+20),h));
        left.setLayout(new FlowLayout(FlowLayout.LEFT,20,20));
        JPanel sc = new JPanel();
        sc.setPreferredSize(left.getMinimumSize());
        left.add(sc);
        JPanel scroller = new JPanel();
        scroller.setPreferredSize(new Dimension((int)left.getMinimumSize().getWidth(),30));
        sc.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        sc.add(scroller);
        sc.setBackground(Color.LIGHT_GRAY);
        sc.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scroller.setBackground(Color.BLACK);

        //left.setBackground(Color.MAGENTA);
        frame.add(left,BorderLayout.WEST);
    }

    private void initCenter(Container frame){
        //Init text panel.
        JPanel content = new JPanel();
        JPanel up1 = new JPanel();
        JPanel up2 = new JPanel();
        JPanel lp1 = new JPanel();
        JPanel lp2 = new JPanel();
        content.setLayout(new GridBagLayout());
        content.add(up1,new GridBagConstraints(0,0,1,1,1,0.15,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
        content.add(up2,new GridBagConstraints(1,0,1,1,1,0.15,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
        content.add(txtwrapper1,new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTH, GridBagConstraints.BOTH,new Insets(0,0,20,20),0,0));
        content.add(txtwrapper2, new GridBagConstraints(1,1,1,1,1,1,GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0,0,20,20),0,0));
        content.add(lp1,new GridBagConstraints(0,2,1,1,1,0.15,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
        content.add(lp2,new GridBagConstraints(1,2,1,1,1,0.15,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
        content.setPreferredSize(new Dimension((int)(w*0.8),(int)(h*0.8)));
//        up1.setBackground(Color.RED);
//        up2.setBackground(Color.YELLOW);
//        lp1.setBackground(Color.YELLOW);
//        lp2.setBackground(Color.CYAN);
//        content.setBackground(Color.GREEN);
        frame.add(content,BorderLayout.CENTER);
    }

    private void initMenu(Container frame){
        JMenuBar m = new JMenuBar();
        m.setPreferredSize(new Dimension(frame.getWidth(),30));

        JMenu file = new JMenu("Files");
        JMenuItem op1 = new JMenuItem("Open File 1");
        JMenuItem op2 = new JMenuItem("Open File 2");
        JMenuItem close = new JMenuItem("Close");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAll = new JMenuItem("Save all");
        JSeparator sep = new JSeparator();
        JMenuItem exit = new JMenuItem("Exit");



        exit.addActionListener((x) -> System.exit(0));

        op1.addActionListener(new SelectFile(this,txtEditor1));
        op2.addActionListener(new SelectFile(this,txtEditor2));
        close.addActionListener((x) -> {
            txtEditor1.setText(null);
            txtEditor2.setText(null);
        });
        file.add(op1);
        file.add(op2);
        file.add(close);
        file.add(save);
        file.add(saveAll);
        file.add(sep);
        file.add(exit);
        m.add(file);

        frame.add(m,BorderLayout.NORTH);
    }

    //Toolbar is under the Menu.
    private void initToolBar(Container frame){
        JPanel tbarw = new JPanel();
        tbarw.setLayout(new BorderLayout());
        //tbarw.setBackground(Color.YELLOW);
        tbarw.setPreferredSize(new Dimension(w,(int)(h*0.11)));
        frame.add(tbarw,BorderLayout.NORTH);
        JPanel tbar = new JPanel();
        tbar.setPreferredSize(new Dimension((int)tbarw.getPreferredSize().getWidth()*10,(int)(h*0.04)));
        initMenu(tbarw);
        //tbar.setBackground(Color.BLUE);
        tbarw.add(tbar,BorderLayout.LINE_START);
        fillToolBar(tbar);
    }

    //HERE VOCABULARY IS PROCESSED
    private void fillToolBar(Container frame){
        frame.setLayout(new GridBagLayout());
        JLabel l = new JLabel("Vocabulary: ");
        JComboBox<String> vocabularies = new JComboBox<>();
        vocabularies.addItem("SQL");
        vocabularies.addItem("Custom");
        vocabularies.addActionListener((x) -> {
            String s = (String)vocabularies.getSelectedItem();
            if(s != null)
                if(!s.equals("Custom")){
                    System.out.println(s);
                    InputStream in = getClass().getClassLoader().getResourceAsStream(s+".json");
                    if(in != null) {
                        SimpleJsonParser parser = new SimpleJsonParser();
                        JsonObject ob = parser.parseStream(in);
                        if (ob != null) {
                            this.currentV = new Vocabulary(ob);
                            JOptionPane.showMessageDialog(this,"Vocabulary was successful loaded!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Cannot create vocabulary!\n\tCannot parse to JsonDocument!");
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(this,"Cannot find resource file!");
                }
                else{
                    System.out.println("Custom");
                    SelectFile act = new SelectFile(this,null);
                    act.actionPerformed(null);
                }
        });
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT,20,0));
        p.add(l);
        p.add(vocabularies);
        frame.add(p,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
    }
}
