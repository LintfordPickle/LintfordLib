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

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.storage.FileUtils;

public abstract class Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String BASIC_VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	public static final String BASIC_FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mShaderID;
	private final String mVertPathname;
	private final String mFragPathname;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int shaderID() {
		return mShaderID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Shader(String pVertPath, String pFragPath) {
		mVertPathname = pVertPath;
		mFragPathname = pFragPath;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {

		String lVertexSource = FileUtils.loadString(mVertPathname);
		String lFragmentSource = FileUtils.loadString(mFragPathname);

		mShaderID = create(lVertexSource, lFragmentSource);
		glUseProgram(mShaderID);

		getUniformLocations();

	}

	public void unloadGLContent() {
		// TODO: Shader - do I need to somehow delete the shaders as well (I do not store the ID as a class member though)
		glDeleteProgram(mShaderID);

	}

	protected void update() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void recompile() {

		if (ConstantsTable.getBooleanValueDef("DEBUG_APP", false)) {
			System.out.println("Recompiling shader .. ");
			System.out.println("  " + mVertPathname);
			System.out.println("  " + mFragPathname);
			
		}

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
			System.err.println("Failed to compile vertex shader!" + mVertPathname);
			System.err.println(glGetShaderInfoLog(lVertID, 2048));
			throw new RuntimeException("Failed to compile vertex shader (" + GL_VERTEX_SHADER + ")");
		} else {
			System.out.println(glGetShaderInfoLog(lVertID, 2048));
		}

		glCompileShader(lFragID);
		if (glGetShaderi(lFragID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!" + mFragPathname);
			System.err.println(glGetShaderInfoLog(lFragID, 2048));
			throw new RuntimeException("Failed to compile fragment shader (" + GL_FRAGMENT_SHADER + ")");
		} else {
			System.out.println(glGetShaderInfoLog(lFragID, 2048));
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

		update();
	}

	public void unbind() {
		glUseProgram(0);
	}

	protected abstract void bindAtrributeLocations(int pShaderID);

	protected void getUniformLocations() {

	}

}