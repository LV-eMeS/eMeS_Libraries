# eMeS_Libraries
Project consists of different solutions to make programmer's life easier.

Creator of project: **Māris Salenieks**

Contacts: maris.salenieks@gmail.com

Prerequisites:
* Java 8.
* Gradle.

 Installation:
 
 Add this to the main gradle file: 
 ```gradle
 	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add this to your project gradle file:
```gradle
	dependencies {
	        compile "com.github.LV-eMeS:eMeS_Libraries:v1.9.4"
	}

```