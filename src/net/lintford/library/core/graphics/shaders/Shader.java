package net.lintford.library.core.graphics.shaders;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.storage.FileUtils;

public abstract class Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String BASIC_VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	public static final String BASIC_FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	private static final String DEBUG_TAG_VERT_NAME = "OpenGL Vert";
	private static final String DEBUG_TAG_FRAG_NAME = "OpenGL Frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private int mShaderID;
	private final String mVertPathname;
	private final String mFragPathname;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mName;
	}

	public int shaderID() {
		return mShaderID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Shader(String pName, String pVertFilename, String pFragFilename) {
		mName = pName;

		mVertPathname = pVertFilename;
		mFragPathname = pFragFilename;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {

		String lVertexSource = FileUtils.loadString(mVertPathname);
		String lFragmentSource = FileUtils.loadString(mFragPathname);

		if (lVertexSource == null || lFragmentSource == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load shader - no vert/frag filename specified.");
			return;

		}

		mShaderID = create(lVertexSource, lFragmentSource);
		glUseProgram(mShaderID);

		getUniformLocations();

	}

	public void unloadGLContent() {
		glUseProgram(0);
		glDeleteProgram(mShaderID);

	}

	public void reloadShader() {

	}

	protected void updateUniforms() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void recompile() {

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Recompiling shader .. ");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  " + mVertPathname);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "  " + mFragPathname);

		String lVertexSource = FileUtils.loadString(mVertPathname);
		String lFragmentSource = FileUtils.loadString(mFragPathname);

		mShaderID = create(lVertexSource, lFragmentSource);

		glUseProgram(mShaderID);
		getUniformLocations();
	}

	public int create(String vertexSource, String fragSource) {
		int lProgramID = glCreateProgram();

		int lVertID = glCreateShader(GL_VERTEX_SHADER);
		int lFragID = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(lVertID, vertexSource);
		glShaderSource(lFragID, fragSource);

		glCompileShader(lVertID);
		if (glGetShaderi(lVertID, GL_COMPILE_STATUS) == GL_FALSE) {
			Debug.debugManager().logger().e(DEBUG_TAG_VERT_NAME, "Failed to compile vertex shader!" + mVertPathname);
			Debug.debugManager().logger().e(DEBUG_TAG_VERT_NAME, glGetShaderInfoLog(lVertID, 2048));

			throw new RuntimeException("Failed to compile vertex shader (" + GL_VERTEX_SHADER + ")");

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), glGetShaderInfoLog(lVertID, 2048));

		}

		glCompileShader(lFragID);
		if (glGetShaderi(lFragID, GL_COMPILE_STATUS) == GL_FALSE) {
			Debug.debugManager().logger().e(DEBUG_TAG_FRAG_NAME, "Failed to compile fragment shader!" + mFragPathname);
			Debug.debugManager().logger().e(DEBUG_TAG_FRAG_NAME, glGetShaderInfoLog(lFragID, 2048));

			throw new RuntimeException("Failed to compile fragment shader (" + GL_FRAGMENT_SHADER + ")");

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), glGetShaderInfoLog(lFragID, 2048));

		}

		glAttachShader(lProgramID, lVertID);
		glAttachShader(lProgramID, lFragID);

		// glBindAttributeLocation only takes effect AFTER the linking
		bindAtrributeLocations(lProgramID);

		glLinkProgram(lProgramID);
		glValidateProgram(lProgramID);

		return lProgramID;
	}

	public void bind() {
		glUseProgram(mShaderID);

		updateUniforms();
	}

	public void unbind() {
		glUseProgram(0);
	}

	protected abstract void bindAtrributeLocations(int pShaderID);

	protected void getUniformLocations() {

	}

}