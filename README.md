
# Clorastore ~ MongoDB/Firestore alternative
<p align="left">
  <a href="#"><img alt="Version" src="https://img.shields.io/badge/Language-Java-1DA1F2?style=flat-square&logo=java"></a>
  <a href="#"><img alt="Bot" src="https://img.shields.io/badge/Version-2.5-green"></a>
  <a href="https://www.instagram.com/x__coder__x/"><img alt="Instagram - x__coder__" src="https://img.shields.io/badge/Instagram-x____coder____x-lightgrey"></a>
  <a href="#"><img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/ErrorxCode/OTP-Verification-Api?style=social"></a>
  </p>

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
- High level querying engine

  
## Acknowledgements
 - [Document oriented database](https://en.wikipedia.org/wiki/Document-oriented_database)
	
## Documentation
- [Javadocs](https://errorxcode.github.io/docs/clorastore/index.html)
- [Guide](https://github.com/ErrorxCode/ClorastoreDB/wiki/Documentation)

  
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
	        implementation 'com.github.Clorabase:ClorastoreDB:2.5'
	}
```

## It's easy
```java
Clorastore.insert().and().read().finish();
```

## Contribution
Contribution are always welcome. Rules and `code of conduct` are same. Please make a issue or pull request regarding any feature or bug.

## Powered by ❤
#### [Clorabase](https://clorabase.netlify.app)
> A account-less platform as a service for android apps. (PaaS)
