# Java-LDLibraryGL
A Java game library containing LWJGL natives and a game state management framework.

# Usage
In order to use the framework, you need to download or clone the project and add it to you existing Java project as a library on the build path.

In your main class, you can create a LWJGL window using:

```
public class GameBase extends LWJGLCore {
/** Main entry point for your  application */
public static void main(String args[]) {
		GameInfo lGameInfo = new GameInfo() { };

		new GameBase(lGameInfo).createWindow();

	}
  
}
```

# GameInfo
The GameInfo interface provides default methods (Java 1.8) specifying the behaviour of the LWJGL window to be created. Simply override any of the methods to provide custom behvaiour:

```
GameInfo lGameInfo = new GameInfo() {
			@Override
			public String windowTitle() {
				return "Hello World!";
			}
		};
```

# ScreenManager
There is also a ScreenManager framework contained within net.ld.library.screenmanager. This is a stack based menu system where you can push and pop screens on top of the stack to be updated & rendered in a top down fashion.
