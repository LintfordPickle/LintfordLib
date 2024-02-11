package net.lintfordlib.core.particles.particlesystems.deserializer;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemDefinition;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemDeserializer implements JsonDeserializer<ParticleSystemDefinition> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String INITIALIZER_PACKAGE_LOCATION = ParticleInitializerBase.class.getPackageName() + ".custom.";
	public static final String MODIFIER_PACKAGE_LOCATION = ParticleModifierBase.class.getPackageName() + ".custom.";

	public static final String PARTICLE_SYSTEM_MAX_PARTICLE_COUNT = "maxParticleCount";
	public static final String PARTICLE_SYSTEM_NAME = "name";
	public static final String PARTICLE_SYSTEM_DISPLAY_NAME = "displayName";
	public static final String PARTICLE_SYSTEM_SPRITESHETE_NAME = "spritesheetName";
	public static final String PARTICLE_SYSTEM_SPRITESHEET_FILENAME = "spritesheetFilename";
	public static final String PARTICLE_SYSTEM_SPRITE_NAME = "spriteName";
	public static final String PARTICLE_SYSTEM_TEXTURE_FILTER = "textureFilterMode";
	public static final String PARTICLE_SYSTEM_PARTICLE_LIFE_MIN = "particleLifeMin";
	public static final String PARTICLE_SYSTEM_PARTICLE_LIFE_MAX = "particleLifeMax";
	public static final String PARTICLE_SYSTEM_PARTICLE_SRC_BLEND = "glSrcBlendFactor";
	public static final String PARTICLE_SYSTEM_PARTICLE_DEST_BLEND = "glDestBlendFactor";
	public static final String PARTICLE_SYSTEM_PARTICLE_ONDEATH_EMITTER_NAME = "onDeathEmitterName";

	public static final String PARTICLE_SYSTEM_INITIALIZER_LIST = "initializers";
	public static final String PARTICLE_SYSTEM_MODIFIER_LIST = "modifiers";
	public static final String PARTICLE_SYSTEM_CLASS_NAME = "className";

	// --------------------------------------
	// Deserializer
	// --------------------------------------

	@Override
	public ParticleSystemDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
		Debug.debugManager().logger().e(getClass().getSimpleName(), INITIALIZER_PACKAGE_LOCATION);
		Debug.debugManager().logger().e(getClass().getSimpleName(), MODIFIER_PACKAGE_LOCATION);

		final var lGson = new Gson();
		final var lNewParticleSystemDefinition = new ParticleSystemDefinition();

		// get the variables of the new particle system
		JsonPrimitive lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_MAX_PARTICLE_COUNT);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.maxParticleCount = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.name = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_DISPLAY_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.displayName = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_SPRITESHETE_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.spritesheetName = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_SPRITESHEET_FILENAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.spritesheetFilepath = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_SPRITE_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.spriteName = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_TEXTURE_FILTER);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.textureFilterMode = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_PARTICLE_LIFE_MIN);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.particleLifeMin = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_PARTICLE_LIFE_MAX);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.particleLifeMax = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_PARTICLE_SRC_BLEND);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.glSrcBlendFactor = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_PARTICLE_DEST_BLEND);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.glDestBlendFactor = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_PARTICLE_ONDEATH_EMITTER_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleSystemDefinition.onDeathEmitterName = lTempPrimitive.getAsString();
		}

		// Initializers
		final var lInitializerArray = jsonElement.getAsJsonObject().getAsJsonArray(PARTICLE_SYSTEM_INITIALIZER_LIST);
		if (lInitializerArray != null) {
			final int lNumInitializers = lInitializerArray.size();
			for (int i = 0; i < lNumInitializers; i++) {
				JsonPrimitive lTempInitializerPrimitive = lInitializerArray.get(i).getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_CLASS_NAME);

				if (lTempInitializerPrimitive == null || !lTempInitializerPrimitive.isString())
					continue;

				ParticleInitializerBase lInitializerInst = null;

				var lInitializerName = lTempInitializerPrimitive.getAsString();
				try {
					lInitializerInst = (ParticleInitializerBase) lGson.fromJson(lInitializerArray.get(i), Class.forName(INITIALIZER_PACKAGE_LOCATION + lInitializerName));
				} catch (JsonSyntaxException exceptionJson) {
					Debug.debugManager().logger().printException(getClass().getSimpleName(), exceptionJson);
				} catch (ClassNotFoundException exception) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to instantiate particle system initializer '%s' for the ParticleSystem '%s'", lInitializerName, lNewParticleSystemDefinition.name));
					Debug.debugManager().logger().printException(getClass().getSimpleName(), exception);
				}

				if (lInitializerInst != null) {
					lNewParticleSystemDefinition.initializers.add(lInitializerInst);
				}
			}
		}

		// Modifiers
		final var lModifierArray = jsonElement.getAsJsonObject().getAsJsonArray(PARTICLE_SYSTEM_MODIFIER_LIST);
		if (lModifierArray != null) {
			final int lNumModifiers = lModifierArray.size();
			for (int i = 0; i < lNumModifiers; i++) {
				final var lTempModifierPrimitive = lModifierArray.get(i).getAsJsonObject().getAsJsonPrimitive(PARTICLE_SYSTEM_CLASS_NAME);

				if (lTempModifierPrimitive == null || !lTempModifierPrimitive.isString())
					continue;

				ParticleModifierBase lModifierInst = null;

				var lModifierName = lTempModifierPrimitive.getAsString();
				try {
					lModifierInst = (ParticleModifierBase) lGson.fromJson(lModifierArray.get(i), Class.forName(MODIFIER_PACKAGE_LOCATION + lModifierName));
				} catch (JsonSyntaxException exceptionJson) {
					Debug.debugManager().logger().printException(getClass().getSimpleName(), exceptionJson);
				} catch (ClassNotFoundException exception) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to instantiate particle system modifier '%s' for the ParticleSystem '%s'", lModifierName, lNewParticleSystemDefinition.name));
					Debug.debugManager().logger().printException(getClass().getSimpleName(), exception);
				}

				if (lModifierInst != null) {
					lNewParticleSystemDefinition.modifiers.add(lModifierInst);
				}
			}
		}

		return lNewParticleSystemDefinition;
	}
}
