
# Clorastore ~ Firestore as local database

 This is a local implementation of cloud firestore for android. Clorastore stores data in form of documents as [firestore](https://firebase.google.com/) and [mongoDB](https://www.mongodb.com/) does.
 The main aim of making this database is to make developer life easier. Programmer does not need to know that stupid SQL queries for just simple CRUD operations.
 This is the second easiest database ever made for android after [CloremDB](https://github.com/ErrorxCode/CloremDB).
 Although, this is similar to those of **Firestore** and **Mongo DB** but also differ in some ways. In Clorastore, collections can a contain collection whereas document can't.
 Each document is limited to 10 MB.

![image](/data-storage.png)

## Features

- Easy,lightweight and fast
- Capable of storing almost all datatype
- Use document and collections to store data

  
## Acknowledgements
 - [What is No-Sql](https://en.wikipedia.org/wiki/Document-oriented_database)
	
## Documentation
- [Javadocs](https://errorxcode.github.io/docs/clorastore/index.html)

  
## Deployment / Installation
 In your project build.gradle
```groovy
 allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
In your app build.gradle
```groovy
dependencies {
	        implementation 'com.github.ErrorxCode:ClorastoreDB:2.0'
	}
```

### CRUD operations

First get the root of the database.
```java
Collection db = Clorastore.getInstance(this,"demoDB").getDatabase();
```

#### Create

```java
Collection collection = db.collection("contributors");
```

Creates a new collection. here `collection()` method's returns the newly created collection.



```java
Map<String,Object> map = new HashMap<>();
map.put("name","Rahil");
map.put("score",78);
map.put("isMale",true);

collection.document("myself",map);
```

Creates a document with the `Map` as its fields.

#### Read

```java
Map<String,Object> map = collection.getDocumentFields("myself");
```

Returns all the fields of document in the form of map. If your document only contain one type of data, you can cast it to `(Map)`.


#### Update

```java
collection.document("myself","name","Rahil khan");
```

Updates field 'name' in the document 'myself'.


#### Delete

```java
collection.delete("myself");
```

Deletes the collection 'myself'.


```java
Clorastore.getInstance(this,"demoDB").delete();
```

Deletes the whole database.


#### Itration
- Returns the list of collections present in the current collection.
```java
List<Collection> collections = collection.getCollections();
```

- Returns the list of document names, present in this collection.
```java
List<String> list = collection.getDocuments();
for (String doc : list){
    Map<String,Object> map = collection.getDocumentFields(doc);
    // Iterate through each map.
}
```


***Note** : Please refer to [javadocs](https://errorxcode.github.io/docs/clorastore/index.html) for detailed information of each method*
Alternatively, you can also hover or press ctrl + method name to view javadocs in the android studio.
.

## Usage example
This code will create a collection named 'contributors' with 3 document each. Each document will contain information about user.
```java
 Map<String,Object> rahil = new HashMap<>();
 rahil.put("github","@Errorxcode");
 rahil.put("instagram","@x__coder__x");
 rahil.put("website","xcoder.tk");

 Map<String,Object> shubam = new HashMap<>();
 rahil.put("github","@shubhamp98");
 rahil.put("instagram","@weshubh");
 rahil.put("website","shubhamp98.github.io");

 Map<String,Object> anas = new HashMap<>();
 rahil.put("github","@anas43950");
 rahil.put("instagram","@anas43950");
 rahil.put("website","x-code.ml");

 Clorastore.getInstance(this,"demoDB").getDatabase()
         .collection("contributors")
         .document("rahil",rahil)
         .document("shubam",shubam)
         .document("anas",anas);
```
This will result in structure like this :


![structure](/structure.png)

What we have done is that first we created a collection named 'contributors' 
using `collection("contributors")` method. Then we created 3 document in it
using `document("rahil",rahil)`. Each document contain fields of respective maps.

#### That's all.
Yeah. This is this much hard, this much easy or whatever you want to say. There is also another great database known as [CloremDB](https://github.com/ErrorxCode/CloremDB). It is also a NoSQL database but it's of key-value type. The data is stored in the form of JSON tree. The only downside of this database is that this doesn't support "Arrays" or "List", and also this require minSdk to be > 26. So if your minSdk is > 26 & you don't want to store array or list in database then use [Clorastore](https://github.com/ErrorxCode/ClorastoreDB) , otherwise use [CloremDB](https://github.com/ErrorxCode/CloremDB)
