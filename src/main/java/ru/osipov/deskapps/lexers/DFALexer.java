package ru.osipov.deskapps.lexers;

import ru.osipov.deskapps.fsa.structures.automats.CNFA;
import ru.osipov.deskapps.fsa.structures.automats.DFA;
import ru.osipov.deskapps.fsa.structures.automats.NFA;
import ru.osipov.deskapps.fsa.structures.graphs.Pair;
import ru.osipov.deskapps.fsa.structures.graphs.Vertex;
import ru.osipov.deskapps.fsa.structures.lists.LinkedStack;
import ru.osipov.deskapps.lexers.generators.FALexerGenerator;
import ru.osipov.deskapps.vocabularies.Vocabulary;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
public class DFALexer extends DFA implements ILexer {

    private LexerIO io;
    private Set<String> keywords;
    private String commentStart;
    private String mlcStart;
    private String mlcEnd;
    private Document target;

//
//    private Set<String> operands;
//    private Map<String,String> aliases;
//    private Token prevTok;
//    private String id;

    public DFALexer(DFA dfa, LexerIO io){
        super(dfa,true);
        this.deleteDeadState();
        this.io = io;
        this.keywords = new HashSet<>();
//        this.operands = new HashSet<>();
//        this.aliases = new HashMap<>();
        this.commentStart = null;
        this.mlcStart = null;
        this.mlcEnd = null;
//        this.id = null;
//        this.prevTok = null;
    }

    public DFALexer(NFA nfa, LexerIO io){
        super(nfa);
        this.deleteDeadState();
        this.io = io;
        this.keywords = new HashSet<>();
//        this.operands = new HashSet<>();
//        this.aliases = new HashMap<>();
//        this.prevTok = null;
        this.commentStart = null;
        this.mlcStart = null;
        this.mlcEnd = null;
//        this.id = null;
    }

    public DFALexer(CNFA nfa){
        super(nfa);
        this.deleteDeadState();
        this.keywords = new HashSet<>();
        System.out.println("DFA States: "+this.getNodes().size());
        System.out.println("Patterns (F): "+this.getFinished().size());
        System.out.println("Start: "+this.getStart());
        this.io = new LookAheadBufferedLexer();
        this.keywords = new HashSet<>();
//        this.operands = new HashSet<>();
//        this.aliases = new HashMap<>();
//        this.prevTok = null;
        this.commentStart = null;
        this.mlcStart = null;
        this.mlcEnd = null;
//        this.id = null;
    }


    public DFALexer(DFA dfa, int bsize){
        super(dfa,true);//set Minimization for lexer true.
        this.deleteDeadState();
        System.out.println("MinDFA States: "+this.getNodes().size());
        System.out.println("Patterns (F): "+this.getFinished().size());
        System.out.println("Start: "+this.getStart());
        this.io = new LookAheadBufferedLexer(bsize);
        this.keywords = new HashSet<>();
//        this.operands = new HashSet<>();
//        this.aliases = new HashMap<>();
//        this.prevTok = null;
        this.commentStart = null;
        this.mlcStart = null;
        this.mlcEnd = null;
//        this.id = null;
    }

    public DFALexer(DFA dfa){
        super(dfa,true);//set Minimization for lexer true.
        this.deleteDeadState();
        System.out.println("MinDFA States: "+this.getNodes().size());
        System.out.println("Patterns (F): "+this.getFinished().size());
        System.out.println("Start: "+this.getStart());
        this.io = new LookAheadBufferedLexer();
        this.keywords = new HashSet<>();
//        this.operands = new HashSet<>();
//        this.aliases = new HashMap<>();
//        this.prevTok = null;
        this.commentStart = null;
        this.mlcStart = null;
        this.mlcEnd = null;
//        this.id = null;
    }

    //MAIN CONSTRUCTOR.
    public DFALexer(Vocabulary V){
        super(new DFA(FALexerGenerator.buildNFA(V)),true);
        this.deleteDeadState();
        this.io = new LookAheadBufferedLexer();
        this.keywords = V.getKeywords();
        this.commentStart = V.getComment();
        this.mlcStart = V.getMlCommentStart();
        this.mlcEnd = V.getMlCommentEnd();
    }

    public void setTarget(Document doc){
        this.target = doc;
    }

    public Document getTarget(){
        return target;
    }

    @Override
    public void setKeywords(Set<String> kws){
        this.keywords = kws;
    }

    @Override
    public Set<String> getKeywords(){
        return keywords;
    }


    @Override
    public void setCommentLine(String s){
        this.commentStart = s;
    }

    @Override
    public void setMlCommentStart(String s){
        this.mlcStart = s;
    }

    @Override
    public void setMlCommentEnd(String s){
        this.mlcEnd = s;
    }

    @Override
    public Token recognize(InputStream f) throws IOException, BadLocationException {
        char cur = (char)io.getch(f);
        while(cur == ' ' || cur == '\t' || cur == '\n' || cur == '\r') {
            target.insertString(target.getLength(),cur+"",null);
            if (cur == '\n') {
                io.setCol(0);
            }
            cur = (char) io.getch(f);
        }
        if(((int)cur) == 65535)
            return new Token("$","$",'t');
        Vertex s = this.start;
        LinkedStack<Vertex> states = new LinkedStack<>();
        states.push(s);
        StringBuilder sb = new StringBuilder();
        sb.append(cur);
        while(((int)cur) != 65535 && !s.isDead()){//while not EOF or not deadState.
            s = moveTo(s,cur);
            if(s == null || s.isDead()){
                //System.out.println("Lexeme at ("+io.getLine()+":"+io.getCol()+") :: "+sb.toString());
                String err = sb.toString();
                while(s == null || !s.isFinish()){
                    if(sb.length() == 0) {
                        return new Token("Unrecognized ", "Error at (" + io.getLine() + ":" + io.getCol() + ") :: Unrecognized token: " + err + "\n", 'e');
                        //return prevTok;
                    }
                    cur = sb.charAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                    io.ungetch(cur);
                    s = states.top();
                    states.pop();
                }
                if(s.getValue().equals(this.commentStart)){//commentLine start.
                    while(cur != '\n' && ((int)cur) != 65535){//check also EOF symbol
                        cur = (char)io.getch(f);
                    }
                    return null;//return null for comments.
                }
                else if(s.getValue().equals(this.mlcStart)){
                    int mls = 0; int mle = mlcEnd.length();
                    boolean isp = false;
                    while(cur != 65535){//cur != EOF
                        cur = (char)io.getch(f);
                        while(mls < mle && mlcEnd.charAt(mls) == cur ){
                            cur = (char)io.getch(f);
                            isp = true;
                            mls++;
                        }
                        if(mls == mle){
                           return null;//mlComment found. return null
                        }
                        else {
                            mls = 0;
                            if(isp)
                                io.ungetch(cur);
                            isp = false;
                        }
                    }
                    return new Token("$","$",'t');
                }
                if(keywords.size() > 0 && keywords.contains(sb.toString())) {
                    return new Token("keywords", sb.toString(), 't');
                    //return prevTok;
                }
//                else if(prevTok == null || (operands.size() > 0 && operands.contains(prevTok.getName())) ){
//                    prevTok =  new Token(s.getValue(), sb.toString(), 't');
//                    return prevTok;
//                }
//                else if(prevTok != null && operands.size() > 0){
//                    prevTok = new Token(aliases.getOrDefault(s.getValue(),s.getValue()), sb.toString(), 't');
//                    return prevTok;
//                }
                else {
                    return new Token(s.getValue(), sb.toString(), 't');
                    //return prevTok;
                }
            }
            else {
                cur = (char)io.getch(f);
                sb.append(cur);
                states.push(s);
            }
        }
        if(sb.length() > 0){
            //System.out.println("Lexeme at ("+io.getLine()+":"+io.getCol()+") :: "+sb.toString());
            if((int)cur == 65535)
                sb.deleteCharAt(sb.length() - 1);//remove redundant read EOF ch.
            if(s.isFinish()) {
                if(s.getValue().equals(this.commentStart)){//commentLine start.
                    while(cur != '\n' && ((int)cur) != 65535){//check also EOF symbol
                        cur = (char)io.getch(f);
                    }
                    return null;//return null for comments.
                }
                else if(s.getValue().equals(this.mlcStart)){
                    int mls = 0; int mle = mlcEnd.length();
                    boolean isp = false;
                    while(cur != 65535){//cur != EOF
                        cur = (char)io.getch(f);
                        while(mlcEnd.charAt(mls) == cur && mls < mle){
                            cur = (char)io.getch(f);
                            isp = true;
                            mls++;
                        }
                        if(mls == mle){
                            return null;//mlComment found. return null
                        }
                        else {
                            mls = 0;
                            if(isp)
                                io.ungetch(cur);
                            isp = false;
                        }
                    }
                    return new Token("$","$",'t');
                }
                if(keywords.size() > 0 && keywords.contains(sb.toString())) {
                    return new Token("keywords", sb.toString(), 't');
                    //return prevTok;
                }
//                else if(prevTok == null || (operands.size() > 0 && operands.contains(prevTok.getName())) ){
//                    prevTok =  new Token(s.getValue(), sb.toString(), 't');
//                    return prevTok;
//                }
//                else if(prevTok != null && operands.size() > 0){
//                    prevTok = new Token(aliases.getOrDefault(s.getValue(),s.getValue()), sb.toString(), 't');
//                    return prevTok;
//                }
                else {
                    return new Token(s.getValue(), sb.toString(), 't');
                    //return prevTok;
                }
            }
            else {
                return new Token("Unrecognized", "Error at (" + io.getLine() + ":" + io.getCol() + ") :: Unrecognized token: " + sb.toString() + "\n", 'e');
                //return prevTok;
            }
        }
        else {
            return new Token("$", "$", 't');
            //return prevTok;
        }
    }


    private Vertex moveTo(Vertex v, char c){
//        String s = c + "";
//        char ch = s.toLowerCase().charAt(0);
        List<Pair<Vertex,Character>> k = tranTable.keySet().stream().filter(x -> x.getV1().equals(v) && x.getV2() == c).collect(Collectors.toList());
        List<Pair<Vertex,Character>> k2 = tranTable.keySet().stream().filter(x -> x.getV1().equals(v) && x.getV2() == (char)0).collect(Collectors.toList());
        return k.size() > 0 ? tranTable.get(k.get(0)) : k2.size() > 0 ? tranTable.get(k2.get(0)) : null;
    }

    public Token generateError(String s1, String s2){
        return new Token("Unrecognized","Error at ("+io.getLine()+":"+io.getCol()+") :: Expected token: "+s1+"  but actual: "+s2+"\n",'e');
    }

    @Override
    public void reset() {
        io.setCol(0);
        io.setLine(1);
        io.clear();
        //this.prevTok = null;
    }
}
