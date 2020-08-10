package ru.osipov.deskapps;

import ru.osipov.deskapps.json.SimpleJsonParser;
import ru.osipov.deskapps.json.jsElements.JsonObject;
import ru.osipov.deskapps.lexers.DFALexer;
import ru.osipov.deskapps.listeners.file.CompareTwoFiles;
import ru.osipov.deskapps.listeners.file.SelectFile;
import ru.osipov.deskapps.listeners.file.SelectedItem;
import ru.osipov.deskapps.vocabularies.StyleInitializer;
import ru.osipov.deskapps.vocabularies.Vocabulary;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Utilities;
import java.awt.*;
import java.io.InputStream;

public class App extends JFrame {

    private JTextPane txtEditor1;
    private JTextPane txtEditor2;

    private JScrollPane txtwrapper1;
    private JScrollPane txtwrapper2;
    private Vocabulary currentV;
    private DFALexer lexer;
    private JLabel fname1;//File names.
    private JLabel fname2;
    private JLabel ed1CaretP;
    private JLabel ed2CaretP;
    private boolean selected1 = false;//are File 1 and File 2 opened now.
    private boolean selected2 = false;
    private CompareTwoFiles compHandler;


    private int w;
    private int h;


    public App(){
        super("FileComparator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.w = 600;
        this.h = 600;
        setSize(new Dimension(w,h));
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        txtEditor1 = new JTextPane();
        txtwrapper1 = new JScrollPane(txtEditor1);
        txtEditor2 = new JTextPane();
        txtwrapper2 = new JScrollPane(txtEditor2);
        lexer = null;
        compHandler = new CompareTwoFiles(this,null,null,null,null);
        fname1 = new JLabel();
        fname2 = new JLabel();
        ed1CaretP = new JLabel();
        ed2CaretP = new JLabel();
        initLeft(pane);
        initCenter(pane);
        initToolBar(pane);
        pack();

        txtEditor1.addCaretListener((c) ->{
            JTextPane s = (JTextPane)c.getSource();
            int caretPos = s.getCaretPosition();
            int rowNum = (caretPos == 0) ? 1 : 0;
            int colNum = -1;
            try{
                int offset = Utilities.getRowStart(s, caretPos);
                colNum = caretPos - offset;// + 1;
                for (offset = caretPos; offset > 0;) {
                    offset = Utilities.getRowStart(s, offset) - 1;
                    rowNum++;
                }
                this.ed1CaretP.setText("Line: "+rowNum+" Column:"+colNum);
                //this.ed1CaretP.setText(rowNum+":"+colNum);
            } catch (BadLocationException e) {
                e.printStackTrace();
                this.ed1CaretP.setText("Cannot find position!");
            }
        });
        txtEditor2.addCaretListener((c) ->{
            JTextPane s = (JTextPane)c.getSource();
            int caretPos = s.getCaretPosition();
            int rowNum = (caretPos == 0) ? 1 : 0;
            int colNum = -1;
            try{
                int offset = Utilities.getRowStart(s, caretPos);
                colNum = caretPos - offset;// + 1;
                for (offset = caretPos; offset > 0;) {
                    offset = Utilities.getRowStart(s, offset) - 1;
                    rowNum++;
                }
                this.ed2CaretP.setText("Line: "+rowNum+" Column:"+colNum);
                //this.ed2CaretP.setText(rowNum+":"+colNum);
            } catch (BadLocationException e) {
                e.printStackTrace();
                this.ed2CaretP.setText("Cannot find position!");
            }
        });
        setVisible(true);
    }

    public void setVocabulary(Vocabulary v){
        this.currentV = v;
    }

    public Vocabulary getCurrentVocabulary(){
        return currentV;
    }

    public DFALexer getLexer(){
        return lexer;
    }

    public void setLexer(DFALexer l){
        this.lexer = l;
        compHandler.setLexer(l);
        compHandler.setStyle(txtEditor1.getStyle("comparison"));
    }

    public void setSelected1(boolean selected1) {
        this.selected1 = selected1;
    }

    public void setSelected2(boolean selected2) {
        this.selected2 = selected2;
    }

    public boolean isSelected1() {
        return selected1;
    }

    public boolean isSelected2() {
        return selected2;
    }

    public JLabel getFname1(){
        return fname1;
    }

    public JLabel getFname2(){
        return fname2;
    }

    public CompareTwoFiles getCompHandler(){
        return compHandler;
    }

    private void initLeft(Container frame){
        JPanel left = new JPanel();
        left.setMinimumSize(new Dimension((int)(w*0.2+20),h));
        left.setLayout(new FlowLayout(FlowLayout.LEFT,20,20));
        JPanel sc = new JPanel();
        sc.setPreferredSize(left.getMinimumSize());
        left.add(sc);

        //NOT READY YET. COMMENT
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

        //INIT UP CELLS
        up1.setLayout(new GridBagLayout());
        up2.setLayout(new GridBagLayout());
        initMenuEditor(up1,SelectedItem.FIRST);
        initMenuEditor(up2,SelectedItem.SECOND);


        //Add FileName labels.
        JLabel l1 = new JLabel("Filename: ");
        JPanel fn = new JPanel(new FlowLayout(FlowLayout.LEADING,0,0));
        fn.add(l1);
        fn.add(fname1);

        up1.add(fn, new GridBagConstraints(0,1,1,1,1,0.1,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        JLabel l2 = new JLabel("Filename: ");
        JPanel fn2 = new JPanel(new FlowLayout(FlowLayout.LEADING,0,0));
        fn2.add(l2);
        fn2.add(fname2);

        up2.add(fn2, new GridBagConstraints(0,1,1,1,1,0.1,GridBagConstraints.LINE_START,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        //INIT LOW CELLS
        lp1.setLayout(new FlowLayout(FlowLayout.TRAILING,0,0));
        lp2.setLayout(new FlowLayout(FlowLayout.TRAILING,0,0));
        lp1.add(ed1CaretP);
        lp2.add(ed2CaretP);


        frame.add(content,BorderLayout.CENTER);
    }

    private void initMenuEditor(Container frame, SelectedItem item){
        JPanel bar = new JPanel();
        bar.setBackground(Color.LIGHT_GRAY);
        frame.add(bar,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTH,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        bar.setLayout(new FlowLayout(FlowLayout.TRAILING,30,0));
        JButton op = new JButton();
        op.setText("Open");
        if(item == SelectedItem.FIRST)
            op.addActionListener(new SelectFile(this,txtEditor1,item,fname1));
        else if(item == SelectedItem.SECOND)
            op.addActionListener(new SelectFile(this,txtEditor2,item,fname2));
        JButton cls = new JButton();
        cls.setText("Close");
        cls.addActionListener((x) ->{
            if(item == SelectedItem.FIRST) {
                txtEditor1.setText(null);
                setSelected1(false);
                fname1.setText(null);
                fname1.setToolTipText(null);
            }
            else if(item == SelectedItem.SECOND) {
                txtEditor2.setText(null);
                setSelected2(false);
                fname2.setText(null);
                fname2.setToolTipText(null);
            }
        });
        bar.add(op);
        bar.add(cls);
        bar.setPreferredSize(new Dimension((int)frame.getMinimumSize().getWidth(),(int)(frame.getMinimumSize().getHeight() / 2)));
    }

    private void initMenu(Container frame){
        JMenuBar m = new JMenuBar();
        m.setPreferredSize(new Dimension(frame.getWidth(),30));

        JMenu file = new JMenu("Files");
        JMenuItem op1 = new JMenuItem("Open File 1");
        JMenuItem op2 = new JMenuItem("Open File 2");
        JMenuItem close = new JMenuItem("Close");
        JMenuItem comp = new JMenuItem("Compare");
        //JMenuItem save = new JMenuItem("Save"); //NOT READY YET
        //JMenuItem saveAll = new JMenuItem("Save all");
        JSeparator sep = new JSeparator();
        JMenuItem exit = new JMenuItem("Exit");



        exit.addActionListener((x) -> System.exit(0));

        op1.addActionListener(new SelectFile(this,txtEditor1, SelectedItem.FIRST,fname1));
        op2.addActionListener(new SelectFile(this,txtEditor2,SelectedItem.SECOND,fname2));
        close.addActionListener((x) -> {
            txtEditor1.setText(null);
            txtEditor2.setText(null);
            setSelected1(false);
            setSelected2(false);
            fname1.setText(null);
            fname1.setToolTipText(null);
            fname2.setText(null);
            fname2.setToolTipText(null);
        });

        comp.addActionListener(compHandler);

        file.add(op1);
        file.add(op2);
        file.add(comp);
        file.add(close);
        //file.add(save); // NOT READY YET
        //file.add(saveAll);
        file.add(sep);
        file.add(exit);
        m.add(file);

        frame.add(m,BorderLayout.NORTH);
    }

    //Toolbar is Container for Menu.
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

        //ComboBox Item LISTENERS
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
                            initStyles();
                            lexer = new DFALexer(currentV);
                            compHandler.setLexer(lexer);

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
                    SelectFile act = new SelectFile(this,null,SelectedItem.NONE,null);
                    act.actionPerformed(null);
                }
        });
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT,20,0));
        p.add(l);
        p.add(vocabularies);
        frame.add(p,new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,20),0,0));
    }

    public void initStyles(){
        StyleInitializer.initStyles(txtEditor1,txtEditor2,currentV);
    }

}
