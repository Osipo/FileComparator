# File Comparator
## Descirption
Compares two files. Ignores tabs, spaces and other useless symbols (only used for human readability but have no any semantics)  
Compares word by word.
## Build
Open cmd (or bash) go to the root of the project (directory FileComparator) and execute:
```
gradlew bootJar
```
The built application will be appeared in **./build/libs**

## Usage
The built app (jar file) is located in directory **./build/libs**  
First of all you must LOAD the **Dictionary**. The **Dictionary**
defines a set of ***words*** which may appear within in files.  
You can load a predefined Dictionary from list or load  
**own Dictionary** (select Custom option in ComboBox).  
After that you must open 2 Files (File 1 and File 2) for comparison.  
And to Compare you must only click Compare (in Files menu).  
Each **Dictionary** is described by json document.  
### Dictionary Structure
The structure of json document is described as follows
>  
```json
{  
    "words" : [  
        {"id" : "uniqueIdName", "pattern" : "regex" },  
        {"id" : "comLine", "pattern" : "//", "className": "comments" },  
        {"id" : "mlCom", "pattern" : "/@*", "className": "comments" },  
        {"id" : "unique", "pattern" : "regex", "className": "nonUnique" }  
    ],   
    "styles" : [  
        {"selector" : "#unique", "color" : "red", "size" : 12 },  
        {"selector" : "comments", "color" : "green" },  
    ],  
    "comment" : "comLine",  
    "mlCommentStart" : "mlCom",  
    "mlCommentEnd" : "*/",  
    "extensions" : ["txt","sql"]  
}
```

Property `"words"` is a list of ***words***. Each ***word*** is *JsonObject*  
with two required *properties*:  
 - `"id"` specifies a unqiue name. By that name the ***word*** can be found.
 The value of this property is *JsonString*.
 - `"pattern"` specifies a ***regular expression*** for word. For example  
 an expression `[0-9]+` specifies an infinity numeric character string.  
 The value of this property is *JsonString*.  

The property `"className"` of the **word** is *JsonString*.  
This property is optional and used only for applying styles on specific set of ***words***.

Property `"styles"` is a list of ***styles***. `"styles"` are optional. Each ***style*** is *JsonObject*    
with one required property `"selector"` and 4 optionals.
 - `"selector"` specifies a unique name of ***style***.
 Each ***style*** can be found by its **selector**.
 - `"color"` specifies a color of text *(foreground)*.
 - `"size"` specifies a size of text.  
 The value of this property is *Integer*.
 - `"font"` specifies a font of text.  
 The value of this property is *JsonString*.
 - `"weight"` specifies a weight of text.
 The value of this property is one of three followed Strings:
 ```"BOLD", "ITALIC", "NORMAL"```
 
There are also four optional properties :
 - `"comment"` specifies the name of word which will be denote a single line comment.  
 The comments in text ARE IGNORED (in this version).
 - `"mlCommentStart"` same as `"comment"` BUT defines a multiline comment.  
 More precisely it define the name of word which will be denote the begining of multiline comment.
 - `"mlCommentEnd"` must be specified when `"mlCommentStart"` is noted.  
 Specifies the character sequence which will be denote the end of multiline comment.  
 The value of this property is *JsonString*.
 - `"extensions"` specifies file extensions which MUST BE MATCHED.  
 That is if `"extensions" : ["sql", "java"]` then only files with  
 those extensions ALLOWED to be compared. The others MUST BE IGNORED.
 
### Regex syntax
The ***regular expressions*** syntax is described as follows: 
 - `a` - *regular expression* which represent a single character string ("a"). 
 (You may type a multiple character sequence `abb...`. An operator of CONCATENATION `^` will be used implicitly.)  
 (So if you type a `abb` the regex will be `a^b^b`. Of course you can use `^` operator explicitly BUT THIS IS NOT RECOMMENDED!) 
 - `r1|r2` - the UNION of two ***regular expressions (r1 and r2)***.
 - `[A-Z]` - the characters class. It is shortance for UNION operation. (i.e it is equal to expression (A|B|...|Z)    ).
 - `(r)` - the GROUPING of *regex (r1)*.

#### Regex quantifiers
There are only two quantifiers are used `*` and `+` which means:
 - `r1+` : one or more times exactly r1.
 - `r1*` : zero or more times exactly r1.

#### Regex details
After processing all regexs the minimal DFA will be built.  
Technically this is not formally the DFA. It is based on model  
used in **Lex, JFLex and YACC** programs.

## Technical requirements.
Compiled on **JDK 1.8.0_161**.
