package net.lintford.library.core.graphics.particles;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.graphics.shaders.Shader;

public class ParticleShader extends Shader {

	public static final String SHADER_NAME = "ParticleShader";
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleShader(String vertPath, String fragPath) {
		super(SHADER_NAME, vertPath, fragPath);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void bindAtrributeLocations(int pShaderID) {
		GL20.glBindAttribLocation(pShaderID, 0, "in_Position");
		GL20.glBindAttribLocation(pShaderID, 1, "in_Color");
		GL20.glBindAttribLocation(pShaderID, 2, "in_TexCoord");

	}

}
