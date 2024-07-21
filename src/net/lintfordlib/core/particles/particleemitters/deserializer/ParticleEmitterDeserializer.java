package net.lintfordlib.core.particles.particleemitters.deserializer;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.particles.particleemitters.ParticleEmitterDefinition;
import net.lintfordlib.core.particles.particleemitters.shapes.ParticleEmitterShape;

public class ParticleEmitterDeserializer implements JsonDeserializer<ParticleEmitterDefinition> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SHAPE_PACKAGE_LOCATION = ParticleEmitterShape.class.getPackageName() + ".";

	public static final String PARTICLE_EMITTER_TRIGGER_TYPE = "triggerType";
	public static final String PARTICLE_EMITTER_TRIGGER_COOLDOWN = "triggerCooldown";
	public static final String PARTICLE_EMITTER_TRIGGER_LENGTH_MS = "triggeredEmissionLengthMs";

	public static final String PARTICLE_EMITTER_NAME = "name";
	public static final String PARTICLE_EMITTER_DISPLAY_NAME = "displayname";

	public static final String PARTICLE_EMITTER_EMIT_TIME_MIN = "emitTimeMin";
	public static final String PARTICLE_EMITTER_EMIT_TIME_MAX = "emitTimeMax";
	public static final String PARTICLE_EMITTER_EMIT_AMOUNT_MIN = "emitAmountMin";
	public static final String PARTICLE_EMITTER_EMIT_AMOUNT_MAX = "emitAmountMax";
	public static final String PARTICLE_EMITTER_EMIT_FORCE_MIN = "emitForceMin";
	public static final String PARTICLE_EMITTER_EMIT_FORCE_MAX = "emitForceMax";
	public static final String PARTICLE_EMITTER_POS_OFFSET_X = "positionRelOffsetX";
	public static final String PARTICLE_EMITTER_POS_OFFSET_Y = "positionRelOffsetY";
	public static final String PARTICLE_EMITTER_POS_OFFSET_ROT = "positionRelOffsetRot";

	public static final String PARTICLE_EMITTER_PS_NAME = "particleSystemName";
	public static final String PARTICLE_EMITTER_SHARED_PS = "useSharedParticleSystem";

	public static final String PARTICLE_EMITTER_CHILD_EMITTER_LIST = "childEmitters";
	public static final String PARTICLE_EMITTER_CHILD_EMITTERS = "childEmitters";

	public static final String PARTICLE_EMITTER_SHAPE_CLASS_NAME = "emitterShape";

	// --------------------------------------
	// Deserializer
	// --------------------------------------

	@Override
	public ParticleEmitterDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
		Debug.debugManager().logger().e(getClass().getSimpleName(), SHAPE_PACKAGE_LOCATION);

		final var lGsonBuilder = new GsonBuilder();
		lGsonBuilder.registerTypeAdapter(ParticleEmitterDefinition.class, new ParticleEmitterDeserializer());
		final var lGson = lGsonBuilder.create();

		final var lNewParticleEmitterDefinition = new ParticleEmitterDefinition();

		// get the variables of the new particle system
		JsonPrimitive lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_TIME_MIN);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitTimeMin = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.name = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_DISPLAY_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.displayName = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_TIME_MAX);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitTimeMax = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_AMOUNT_MIN);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitAmountMin = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_AMOUNT_MAX);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitAmountMax = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_FORCE_MIN);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitForceMin = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_EMIT_FORCE_MAX);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.emitForceMax = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_POS_OFFSET_X);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.positionRelOffsetX = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_POS_OFFSET_Y);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.positionRelOffsetY = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_POS_OFFSET_ROT);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.positionRelOffsetRot = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_PS_NAME);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.particleSystemName = lTempPrimitive.getAsString();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_SHARED_PS);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.useSharedParticleSystem = lTempPrimitive.getAsBoolean();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_TRIGGER_TYPE);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.triggerType = lTempPrimitive.getAsInt();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_TRIGGER_COOLDOWN);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.triggerCooldown = lTempPrimitive.getAsFloat();
		}

		lTempPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive(PARTICLE_EMITTER_TRIGGER_LENGTH_MS);
		if (lTempPrimitive != null && !lTempPrimitive.isJsonNull()) {
			lNewParticleEmitterDefinition.triggeredEmissionLengthMs = lTempPrimitive.getAsFloat();
		}

		// Emitter Shape
		var lShapeObject = jsonElement.getAsJsonObject().getAsJsonObject(PARTICLE_EMITTER_SHAPE_CLASS_NAME);
		if (lShapeObject != null && !lShapeObject.isJsonNull()) {
			ParticleEmitterShape lEmitterShapeInst = null;
			JsonPrimitive lTempInitializerPrimitive = lShapeObject.getAsJsonPrimitive("className");

			var lShapeName = lTempInitializerPrimitive.getAsString();
			try {
				lEmitterShapeInst = (ParticleEmitterShape) lGson.fromJson(lShapeObject, Class.forName(SHAPE_PACKAGE_LOCATION + lShapeName));
			} catch (JsonSyntaxException exceptionJson) {
				Debug.debugManager().logger().printException(getClass().getSimpleName(), exceptionJson);
			} catch (ClassNotFoundException exception) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Unable to instantiate particle emitter shape '%s' for the ParticleEmitter '%s'", lShapeName, lNewParticleEmitterDefinition.displayName));
				Debug.debugManager().logger().printException(getClass().getSimpleName(), exception);
			}

			if (lEmitterShapeInst != null)
				lNewParticleEmitterDefinition.ParticleEmitterShape = lEmitterShapeInst;
		}

		// Child Emitters
		final var lChildEmitterArray = jsonElement.getAsJsonObject().getAsJsonArray(PARTICLE_EMITTER_CHILD_EMITTER_LIST);
		if (lChildEmitterArray != null) {
			final int lNumChildEmitters = lChildEmitterArray.size();
			for (int i = 0; i < lNumChildEmitters; i++) {
				ParticleEmitterDefinition lDef = (ParticleEmitterDefinition) lGson.fromJson(lChildEmitterArray.get(i), ParticleEmitterDefinition.class);
				lNewParticleEmitterDefinition.childEmitters().add(lDef);
			}
		}

		return lNewParticleEmitterDefinition;
	}
}
