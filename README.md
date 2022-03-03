
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

 ---> **Readme is incomplete.**

#### That's all.
Yeah. This is this much hard, this much easy or whatever you want to say. There is also another great database known as [CloremDB](https://github.com/ErrorxCode/CloremDB). It is also a NoSQL database but it's of key-value type. The data is stored in the form of JSON tree. The only downside of this database is that this doesn't support "Arrays" or "List", and also this require minSdk to be > 26. So if your minSdk is > 26 & you don't want to store array or list in database then use [Clorastore](https://github.com/ErrorxCode/ClorastoreDB) , otherwise use [CloremDB](https://github.com/ErrorxCode/CloremDB)
