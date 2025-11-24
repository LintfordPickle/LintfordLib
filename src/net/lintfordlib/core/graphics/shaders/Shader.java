package net.lintfordlib.core.graphics.shaders;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.FileUtils;

public abstract class Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	protected Shader(String shaderName, String vertFilename, String fragFilename) {
		mName = shaderName;

		mVertPathname = vertFilename;
		mFragPathname = fragFilename;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		final var lVertexSource = FileUtils.loadString(mVertPathname);
		final var lFragmentSource = FileUtils.loadString(mFragPathname);

		if (lVertexSource == null || lFragmentSource == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to load shader - no vert/frag filename specified.");
			return;
		}

		mShaderID = create(lVertexSource, lFragmentSource);
		glUseProgram(mShaderID);

		getUniformLocations();
	}

	public void unloadResources() {
		glDeleteProgram(mShaderID);
		mShaderID = -1;
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

		final var vertexSource = FileUtils.loadString(mVertPathname);
		final var fragmentSource = FileUtils.loadString(mFragPathname);

		mShaderID = create(vertexSource, fragmentSource);

		glUseProgram(mShaderID);
		getUniformLocations();
	}

	public int create(String vertexSource, String fragSource) {
		final var programID = glCreateProgram();

		final var vertID = glCreateShader(GL_VERTEX_SHADER);
		final var fragID = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(vertID, vertexSource);
		glShaderSource(fragID, fragSource);

		glCompileShader(vertID);
		if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
			Debug.debugManager().logger().e(DEBUG_TAG_VERT_NAME, "Failed to compile vertex shader!" + mVertPathname);
			final int logSize = glGetShaderi(fragID, GL_INFO_LOG_LENGTH);

			final var shaderLogText = glGetShaderInfoLog(vertID, logSize);
			Debug.debugManager().logger().e(DEBUG_TAG_VERT_NAME, shaderLogText);
			throw new RuntimeException("Failed to compile vertex shader (" + GL_VERTEX_SHADER + "): " + shaderLogText);
		}

		glCompileShader(fragID);
		if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
			Debug.debugManager().logger().e(DEBUG_TAG_FRAG_NAME, "Failed to compile fragment shader!" + mFragPathname);
			final int logSize = glGetShaderi(fragID, GL_INFO_LOG_LENGTH);

			final var shaderInfoLog = glGetShaderInfoLog(fragID, logSize);
			Debug.debugManager().logger().e(DEBUG_TAG_FRAG_NAME, shaderInfoLog);

			throw new RuntimeException("Failed to compile fragment shader (" + GL_FRAGMENT_SHADER + "): " + shaderInfoLog);
		}

		glAttachShader(programID, vertID);
		glAttachShader(programID, fragID);

		bindAtrributeLocations(programID);

		glLinkProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
		}

		if (programID != 0)
			glDetachShader(programID, vertID);

		if (programID != 0)
			glDetachShader(programID, fragID);

		glValidateProgram(programID);

		return programID;
	}

	public void bind() {
		glUseProgram(mShaderID);

		updateUniforms();
	}

	public void unbind() {
		glUseProgram(0);
	}

	protected abstract void bindAtrributeLocations(int shaderID);

	protected void getUniformLocations() {

	}
}