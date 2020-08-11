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
> {  
    "words" : [  
        {"id" : "uniqueIdName", "pattern" : "regex" },  
        {"id" : "comline", "pattern" : "//", "className": "comments" },  
        {"id" : "mlCom", "pattern" : "/@*", "className": "comments" },  
        {"id" : "unique", "pattern" : "regex", "className": "nonUnique" },  
        ...  
    ],   
    "styles" : [  
        {"selector" : "#unique", "color" : "red", "size" : int },  
        {"selector" : "comments", "color" : "green" },  
        ...  
    ],  
    "comment" : "comLine",  
    "mlCommentStart" : "mlCom",  
    "mlCommentEnd" : "*/",  
    "extensions" : ["txt","sql",...]  
}
