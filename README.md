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
 - `"id"` specifies an unqiue name. By that name the ***word*** can be found.
 The value of this property is *JsonString*.
 - `"pattern"` specifies a ***regular expression*** for word. For example  
 an expression `[0-9]+` specifies an infinity numeric character string.  
 The value of this property is *JsonString*.
The property `"className"` of the **word** is *JsonString*.  
This property is optional and used only for applying styles on specific set of ***words***.
Property `"styles"` is a list of ***styles***. Each ***style*** is *JsonObject*    
with one required property `"selector"` and 4 optionals.
 - `"selector"` specifies an unique name of ***style***.
 Each ***style*** can be found by its **selector**.
 - `"color"` specifies a color of text *(foreground)*.
 - `"size"` specifies a size of text.  
 The value of this property is *Integer*.
 - `"font"` specifies a font of text.  
 The value of this property is *JsonString*.
 - `"weight"` specifies a weight of text.
 The value of this property is one of three followed Strings:
 ```"BOLD", "ITALIC", "NORMAL"```
 
