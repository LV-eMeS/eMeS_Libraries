# eMeS_Libraries
Project consists of different solutions to make Java developer's life easier.

Creator of project: **Māris Salenieks**

Contacts: maris.salenieks@gmail.com

Prerequisites:
* Java 8.
* Gradle.

 Installation:
 
 Add this code to the main gradle file: 
 ```gradle
allprojects {
	repositories {
		...
	    maven { url 'https://jitpack.io' }
	}
}
```

And this code to same project gradle file:
```gradle
dependencies {
     ...
     compile "com.github.LV-eMeS:eMeS_Libraries:v2.X.Y"
     ...
 }
```
Precise version of artifact can be found in [![](https://jitpack.io/v/LV-eMeS/eMeS_Libraries.svg)](https://jitpack.io/#LV-eMeS/eMeS_Libraries)