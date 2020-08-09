package ru.osipov.deskapps.vocabularies;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.Set;

public class StyleInitializer {
    public static void initStyles(JTextPane editor1, JTextPane editor2,Vocabulary v){
        Set<String> s = v.getStyles().keySet();
        for(String sel : s){
            StyleRule rule = v.getStyles().get(sel);
            Style s1 = editor1.addStyle(sel,null);
            Style s2 = editor2.addStyle(sel,null);
            addToStyle(s1,rule);
            addToStyle(s2,rule);
        }
        initComparisonStyle(editor1);
        initComparisonStyle(editor2);
    }

    private static void initComparisonStyle(JTextPane e){
        Style d = e.addStyle("comparison",null);
        StyleConstants.setBackground(d,Color.YELLOW);
        StyleConstants.setBold(d,true);
    }


    private static void addToStyle(Style s1,StyleRule rule){
        String c = rule.getColor() == null ? "BLACK" : rule.getColor().toUpperCase();
        String f = rule.getFont() == null ? "Serif" : rule.getFont();
        int sz = rule.getSize() == null ? 14 : rule.getSize();
        TextWeight w = rule.getWeight() == null ? TextWeight.NORMAL : rule.getWeight();
        StyleConstants.setForeground(s1, ColorStyle.valueOf(c).getColor());
        StyleConstants.setFontSize(s1,sz);
        StyleConstants.setFontFamily(s1,f);
        switch(w){
            case BOLD: StyleConstants.setBold(s1,true); break;
            case ITALIC:StyleConstants.setItalic(s1,true); break;
            case NORMAL: default:{
                StyleConstants.setItalic(s1,false);
                StyleConstants.setBold(s1,false);
                break;
            }
        }
    }
}
