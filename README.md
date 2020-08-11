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
&emsp;&emsp;"words" : [  
&emsp;&emsp;&emsp;&emsp;{"id" : "uniqueIdName", "pattern" : "regex" },  
&emsp;&emsp;&emsp;&emsp;{"id" : "comLine", "pattern" : "//", "className": "comments" },  
&emsp;&emsp;&emsp;&emsp;{"id" : "mlCom", "pattern" : "/@*", "className": "comments" },  
&emsp;&emsp;&emsp;&emsp;{"id" : "unique", "pattern" : "regex", "className": "nonUnique" },  
&emsp;&emsp;&emsp;&emsp;...  
&emsp;&emsp;],   
&emsp;&emsp;"styles" : [  
&emsp;&emsp;&emsp;&emsp;{"selector" : "#unique", "color" : "red", "size" : int },  
&emsp;&emsp;&emsp;&emsp;{"selector" : "comments", "color" : "green" },  
&emsp;&emsp;&emsp;&emsp;...  
&emsp;&emsp;],  
&emsp;&emsp;"comment" : "comLine",  
&emsp;&emsp;"mlCommentStart" : "mlCom",  
&emsp;&emsp;"mlCommentEnd" : "*/",  
&emsp;&emsp;"extensions" : ["txt","sql",...]  
}
```

Property `words` is a list of ***words***. Each ***word*** is *JsonObject*  
with two required *properties*:  
 - `"id"` specifies an unqiue name. By that name the ***word*** can be found.
 The value of this property is a **JsonString**.
 - `"pattern"` specifies a ***regular expression*** for word. For example  
 an expression `[0-9]+` specifies an infinity numeric character string.  
 The value of this property is a **JsonString**.