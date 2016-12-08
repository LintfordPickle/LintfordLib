# Java-LDLibraryGL
A Java game library containing LWJGL natives and a game state management framework.

# Usage
In order to use the framework, you need to download or clone the project and add it as a 'required project' on the build path. Once you have done this, you should be able to use the classes within. To get started and open a window, you can use:

```
public class GameBase extends LWJGLCore {

/** Main entry point for your  application */
public static void main(String args[]) {
	GameInfo lGameInfo = new GameInfo() { };

	new GameBase(lGameInfo).createWindow();
  
}
```

LWJGLCore is an abstract class which defines a couple of core methods for an OpenGL game. These methods are called automatically, and are:

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
