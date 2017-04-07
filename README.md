# Java-LDLibraryGL
A Java game library containing LWJGL natives and a game state management framework.
The library project and metadata was created in Eclipse Neon (4.6.0). The project was compiled with Java 1.8.0_112 64-bit and contains LGWJL3 jars and native libraries.

# Getting the repository
You can get the Java-LDLibraryGL project by either by cloning the repository or by adding it as a submodule to an existing git repository.
If you want to add the Java-LDLibraryGL project to your existing git project, type the following at the git Bash:

```
git submodule add https://github.com/LintfordPickle/Java-LDLibraryGL.git <directory>
```

This will register the git repository as a submodule under the directory specified. This command will automatically clone the submodule into the repository. You can check the status of the submodules using:

```
git status
```

and you can pull the latest version of the submodule with the following command:

```
git submodule update
```

note: a git clone of a repository will not by default pull any submodules. In this case you need to submodule init and then submodule update.

# Library Usage
Once you have cloned the Java-LDLibraryGL repository, you can import it into your IDE workspace. It should then be added as a project to your game project's classpath. This can be done either using the project properties dialog (e.g. in Eclipse), or by added the following to the .classpath:

```
<classpathentry combineaccessrules="false" kind="src" path="<relative_path_to_ldlibrary_folder>"/>
```

Once you have done this, you should be able to import the packages from the LDLibrary and access the classes within. To test the setup and get started, you can open a new LWJGL window by adding the following class to your project:

```
// Import the OpenGL bindings from within the LWJGL framework
import org.lwjgl.opengl.GL11;

// import LDLibrary classes
import net.ld.library.GameInfo;
import net.ld.library.core.LWJGLCore;

/** Create a class which extends LWJGLCore. This will be responsible for instantiating and opening an OpenGL window. */
public class GameBase extends LWJGLCore {

/** Main entry point for the game. */
public static void main(String args[]) {
	GameInfo lGameInfo = new GameInfo() { };

	GameBase lGameBase = new GameBase(lGameInfo);
	
	// Create the main application window
	if( lGameBase.createWindow() ) {
		// If successful, begin the game loop
		lGameBase.onRunGameLoop();
	
	}
  
}
```

LWJGLCore is an abstract class which defines core methods for an OpenGL game. These methods are called automatically, and are:

**void onInitialiseGL()**: This is called once at the start of the application. From here you can set the initial state for OpenGL.

**void onInitialiseApp()**: This method is also called once, and it provides a convenient place to initialise any other classes for your game.

**void onLoadContent()**: This is called once at the beginning of the application and after onInitialiseGL(). Here you can begin loading OpenGL resources and using the OpenGL context.

**boolean onHandleInput()**: Called once per frame, before onUpdate(), and is where you can handle input.

**void onUpdate(GameTime pGameTime)**: Called once per frame.

**void onDraw()**: Called once per frame.


# Resource Files
The LDLibrary also has a couple of resource files that are used as standard by the ScreenManager (such as a 'default' font and texture file). These resources are embedded in the jar and streamed at runtime.

You can specify to the ResourceManager in LWJGLCore to watch a texture directory for changes. Any changes to texture
files at runtime will be automatically reloaded and updated in the running game. To do this, for example, use:

```
mResourceManager.watchTextureDirectory("res/textures");
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


# Quick Startup
The following elements can be added directly into your game class which extends LWJGLCore, to provide some basic features:

onInitialiseGL:
```
	// Enable depth testing
	GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	
	// Enable depth testing
	GL11.glEnable(GL11.GL_DEPTH_TEST);
	GL11.glDepthFunc(GL11.GL_LEQUAL);
	
	// Set the clear color to corn flower blue
	GL11.glClearColor(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);
```

onDraw():
```
	// Clear the depth buffer and color buffer
	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
```

## Unit Tests

Units tests are created with JUnit 4, which must be present in the project build path.

Units tests have been included in the /test/ source folder. The unit tests share the same package names as the classes they test (to allow package level access modifiers).
