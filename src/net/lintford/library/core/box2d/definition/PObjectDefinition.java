package net.lintford.library.core.box2d.definition;

import java.io.BufferedReader;
import java.io.FileInputStream;

/*
 Author: Chris Campbell - www.iforce2d.net

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.
 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:
 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.FrictionJointDef;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.PulleyJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.RopeJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lintford.library.core.box2d.entity.Box2dCircleInstance;
import net.lintford.library.core.box2d.entity.Box2dEdgeInstance;
import net.lintford.library.core.box2d.entity.Box2dPolygonInstance;

public class PObjectDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BODY_TYPE_STATIC = 0;
	public static final int BODY_TYPE_KINEMATIC = 1;
	public static final int BODY_TYPE_DYNAMIC = 2;

	// --------------------------------------
	// Inner Classes
	// --------------------------------------

	public class Jb2dJsonCustomProperties {

		Map<String, Integer> m_customPropertyMap_int;
		Map<String, Double> m_customPropertyMap_float;
		Map<String, String> m_customPropertyMap_string;
		Map<String, Vec2> m_customPropertyMap_vec2;
		Map<String, Boolean> m_customPropertyMap_bool;

		public Jb2dJsonCustomProperties() {
			m_customPropertyMap_int = new HashMap<String, Integer>();
			m_customPropertyMap_float = new HashMap<String, Double>();
			m_customPropertyMap_string = new HashMap<String, String>();
			m_customPropertyMap_vec2 = new HashMap<String, Vec2>();
			m_customPropertyMap_bool = new HashMap<String, Boolean>();
		}

	}

	public static final boolean INVERT_Y = true;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mPObjectName;

	protected Vector<Box2dBodyDefinition> mBodies;
	protected Map<Integer, Box2dBodyDefinition> mIndexToBodyMap;
	protected Map<Box2dBodyDefinition, Integer> mBodyToIndexMap;

	protected Vector<Box2dJointDefinition> mJoints;
	protected Map<Box2dJointDefinition, Integer> mJointToIndexMap;

	protected Map<Box2dBodyDefinition, String> mBodyToNameMap;
	protected Map<Box2dFixtureDefinition, String> mFixtureToNameMap;
	protected Map<Box2dJointDefinition, String> mJointToNameMap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mPObjectName;
	}

	public Vector<Box2dBodyDefinition> bodies() {
		return mBodies;
	}

	public Vector<Box2dJointDefinition> joints() {
		return mJoints;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public PObjectDefinition() {
		this(true);
	}

	public PObjectDefinition(boolean useHumanReadableFloats) {

		if (!useHumanReadableFloats) {
			// The floatToHex function is not giving the same results
			// as the original C++ version... not critical so worry about it
			// later.
			System.out.println("Non human readable floats are not implemented yet");
			useHumanReadableFloats = true;
		}

		mIndexToBodyMap = new HashMap<>();
		mBodyToIndexMap = new HashMap<>();
		mJointToIndexMap = new HashMap<>();
		mBodies = new Vector<>();
		mJoints = new Vector<>();

		mBodyToNameMap = new HashMap<>();
		mFixtureToNameMap = new HashMap<>();
		mJointToNameMap = new HashMap<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadFromFile(String filename, StringBuilder errorMsg, World existingWorld) {
		clear();

		if (null == filename)
			return;

		BufferedReader br = null;
		String str = new String();
		try {
			InputStream fis;
			fis = new FileInputStream(filename);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			String line;
			while ((line = br.readLine()) != null) {
				str += line;
			}
		} catch (FileNotFoundException e) {
			errorMsg.append("Could not open file for reading: " + filename);
			return;

		} catch (IOException e) {
			errorMsg.append("Error reading file: " + filename);
			return;

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			JSONObject worldValue = new JSONObject(str);
			loadFromFileFromJSONObject(worldValue);

		} catch (JSONException e) {
			errorMsg.append("\nFailed to parse JSON: " + filename);
			e.printStackTrace();

		}
	}

	public void loadFromFileFromJSONObject(JSONObject worldValue) throws JSONException {
		clear();

		j2b2World(worldValue);

	}

	public void j2b2World(JSONObject worldValue) throws JSONException {

		mPObjectName = worldValue.optString("name", "POBJECT_UNANMED");

		// Read all bodies from file
		int i = 0;
		JSONArray bodyValues = worldValue.optJSONArray("body");
		if (null != bodyValues) {
			int numBodyValues = bodyValues.length();
			for (i = 0; i < numBodyValues; i++) {
				JSONObject bodyValue = bodyValues.getJSONObject(i);
				Box2dBodyDefinition body = j2b2Body(bodyValue);

				// TODO: Load custom values
				// readCustomPropertiesFromJson(body, bodyValue);

				mBodies.add(body);
				mIndexToBodyMap.put(i, body);

			}
		}

		// need two passes for joints because gear joints reference other joints
		JSONArray jointValues = worldValue.optJSONArray("joint");
		if (null != jointValues) {
			int numJointValues = jointValues.length();
			for (i = 0; i < numJointValues; i++) {
				JSONObject jointValue = jointValues.getJSONObject(i);
				if (!jointValue.optString("type", "").equals("gear")) {
					Box2dJointDefinition joint = j2b2Joint(jointValue);

					// TODO: Load custom values
					// readCustomPropertiesFromJson(joint, jointValue);

					mJoints.add(joint);

				}
			}
			for (i = 0; i < numJointValues; i++) {
				JSONObject jointValue = jointValues.getJSONObject(i);
				if (jointValue.optString("type", "").equals("gear")) {
					Box2dJointDefinition joint = j2b2Joint(jointValue);

					// TODO: Load custom values
					// readCustomPropertiesFromJson(joint, jointValue);

					mJoints.add(joint);

				}
			}
		}

	}

	public Box2dBodyDefinition j2b2Body(JSONObject bodyValue) throws JSONException {
		Box2dBodyDefinition bodyDef = new Box2dBodyDefinition();
		switch (bodyValue.getInt("type")) {
		case 0:
			bodyDef.bodyDefinition.type = BodyType.STATIC;
			bodyDef.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC;
			break;
		case 1:
			bodyDef.bodyDefinition.type = BodyType.KINEMATIC;
			bodyDef.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_KINEMATIC;
			break;
		case 2:
			bodyDef.bodyDefinition.type = BodyType.DYNAMIC;
			bodyDef.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC;
			break;
		}

		bodyDef.name = bodyValue.optString("name", "");
		bodyDef.bodyDefinition.position = jsonToVec("position", bodyValue);
		bodyDef.bodyDefinition.angle = jsonToFloat("angle", bodyValue);
		bodyDef.bodyDefinition.linearVelocity = jsonToVec("linearVelocity", bodyValue);
		bodyDef.bodyDefinition.angularVelocity = jsonToFloat("angularVelocity", bodyValue);
		bodyDef.bodyDefinition.linearDamping = jsonToFloat("linearDamping", bodyValue, -1, 0);
		bodyDef.bodyDefinition.angularDamping = jsonToFloat("angularDamping", bodyValue, -1, 0);
		bodyDef.bodyDefinition.gravityScale = jsonToFloat("gravityScale", bodyValue, -1, 1);

		bodyDef.bodyDefinition.allowSleep = bodyValue.optBoolean("allowSleep", true);
		bodyDef.bodyDefinition.awake = bodyValue.optBoolean("awake", false);
		bodyDef.bodyDefinition.fixedRotation = bodyValue.optBoolean("fixedRotation");
		bodyDef.bodyDefinition.bullet = bodyValue.optBoolean("bullet", false);
		bodyDef.bodyDefinition.active = bodyValue.optBoolean("active", true);

		String bodyName = bodyValue.optString("name", "");
		if (!"".equals(bodyName))
			setBodyName(bodyDef, bodyName);

		int i = 0;
		JSONArray fixtureValues = bodyValue.optJSONArray("fixture");
		if (null != fixtureValues) {
			int numFixtureValues = fixtureValues.length();
			for (i = 0; i < numFixtureValues; i++) {
				JSONObject fixtureValue = fixtureValues.getJSONObject(i);
				Box2dFixtureDefinition lBox2dFixtureDefinition = j2b2Fixture(bodyDef, fixtureValue);

				// TODO: Load custom values for the fixtures
				// readCustomPropertiesFromJson(fixture, fixtureValue);

				bodyDef.fixtureList.add(lBox2dFixtureDefinition);

			}
		}

		bodyDef.mass = jsonToFloat("massData-mass", bodyValue);
		bodyDef.massI = jsonToFloat("massData-I", bodyValue);
		bodyDef.massCenter = jsonToVec("massData-center", bodyValue);

		return bodyDef;
	}

	Box2dFixtureDefinition j2b2Fixture(Box2dBodyDefinition pBox2dBodyDefinition, JSONObject fixtureValue) throws JSONException {
		if (null == fixtureValue)
			return null;

		Box2dFixtureDefinition lBox2dFixtureDefinition = new Box2dFixtureDefinition();

		lBox2dFixtureDefinition.name = fixtureValue.optString("name", "");
		lBox2dFixtureDefinition.fixtureDef.restitution = jsonToFloat("restitution", fixtureValue);
		lBox2dFixtureDefinition.fixtureDef.friction = jsonToFloat("friction", fixtureValue);
		lBox2dFixtureDefinition.fixtureDef.density = jsonToFloat("density", fixtureValue);
		lBox2dFixtureDefinition.fixtureDef.isSensor = fixtureValue.optBoolean("sensor", false);

		lBox2dFixtureDefinition.fixtureDef.filter.categoryBits = fixtureValue.optInt("filter-categoryBits", 0x0001);
		lBox2dFixtureDefinition.fixtureDef.filter.maskBits = fixtureValue.optInt("filter-maskBits", 0xffff);
		lBox2dFixtureDefinition.fixtureDef.filter.groupIndex = fixtureValue.optInt("filter-groupIndex", 0);

		// Fixture fixture = null;
		if (null != fixtureValue.optJSONObject("circle")) {
			JSONObject circleValue = fixtureValue.getJSONObject("circle");
			CircleShape circleShape = new CircleShape();
			circleShape.m_radius = jsonToFloat("radius", circleValue);
			circleShape.m_p.set(jsonToVec("center", circleValue));

			lBox2dFixtureDefinition.fixtureDef.shape = circleShape;

			Box2dCircleInstance lCircleInstance = new Box2dCircleInstance();
			lCircleInstance.center.set(circleShape.m_p);
			lCircleInstance.radius = circleShape.m_radius;

			lBox2dFixtureDefinition.shape = lCircleInstance;

		} else if (null != fixtureValue.optJSONObject("edge")) {
			JSONObject edgeValue = fixtureValue.getJSONObject("edge");
			EdgeShape edgeShape = new EdgeShape();
			edgeShape.m_vertex1.set(jsonToVec("vertex1", edgeValue));
			edgeShape.m_vertex2.set(jsonToVec("vertex2", edgeValue));
			edgeShape.m_hasVertex0 = edgeValue.optBoolean("hasVertex0", false);
			edgeShape.m_hasVertex3 = edgeValue.optBoolean("hasVertex3", false);
			if (edgeShape.m_hasVertex0)
				edgeShape.m_vertex0.set(jsonToVec("vertex0", edgeValue));
			if (edgeShape.m_hasVertex3)
				edgeShape.m_vertex3.set(jsonToVec("vertex3", edgeValue));

			lBox2dFixtureDefinition.fixtureDef.shape = edgeShape;

			Box2dEdgeInstance lEdgeInstance = new Box2dEdgeInstance();

			lEdgeInstance.hasVertex0 = edgeShape.m_hasVertex0;
			lEdgeInstance.hasVertex3 = edgeShape.m_hasVertex3;

			lEdgeInstance.vertex1.set(edgeShape.m_vertex1);
			lEdgeInstance.vertex2.set(edgeShape.m_vertex2);

			if (lEdgeInstance.hasVertex0)
				lEdgeInstance.vertex0.set(edgeShape.m_vertex0);

			if (lEdgeInstance.hasVertex3)
				lEdgeInstance.vertex3.set(edgeShape.m_vertex3);

			lBox2dFixtureDefinition.shape = lEdgeInstance;

		} else if (null != fixtureValue.optJSONObject("chain")) {
			JSONObject chainValue = fixtureValue.getJSONObject("chain");
			ChainShape chainShape = new ChainShape();
			int numVertices = chainValue.getJSONObject("vertices").getJSONArray("x").length();
			Vec2 vertices[] = new Vec2[numVertices];
			for (int i = 0; i < numVertices; i++)
				vertices[i] = jsonToVec("vertices", chainValue, i);

			chainShape.createChain(vertices, numVertices);
			chainShape.m_hasPrevVertex = chainValue.optBoolean("hasPrevVertex", false);
			chainShape.m_hasNextVertex = chainValue.optBoolean("hasNextVertex", false);
			if (chainShape.m_hasPrevVertex)
				chainShape.m_prevVertex.set(jsonToVec("prevVertex", chainValue));
			if (chainShape.m_hasNextVertex)
				chainShape.m_nextVertex.set(jsonToVec("nextVertex", chainValue));

			lBox2dFixtureDefinition.fixtureDef.shape = chainShape;

			// TODO: Need to implement the Box2dChainInstance

		} else if (null != fixtureValue.optJSONObject("polygon")) {
			JSONObject polygonValue = fixtureValue.getJSONObject("polygon");
			Vec2 vertices[] = new Vec2[Settings.maxPolygonVertices];
			int numVertices = polygonValue.getJSONObject("vertices").getJSONArray("x").length();
			if (numVertices > Settings.maxPolygonVertices) {
				System.out.println("Ignoring polygon fixture with too many vertices.");
			} else if (numVertices < 2) {
				System.out.println("Ignoring polygon fixture less than two vertices.");
			} else if (numVertices == 2) {
				System.out.println("Creating edge shape instead of polygon with two vertices.");
				EdgeShape edgeShape = new EdgeShape();
				edgeShape.m_vertex1.set(jsonToVec("vertices", polygonValue, 0));
				edgeShape.m_vertex2.set(jsonToVec("vertices", polygonValue, 1));

				lBox2dFixtureDefinition.fixtureDef.shape = edgeShape;

				Box2dEdgeInstance lEdgeInstance = new Box2dEdgeInstance();

				lEdgeInstance.vertex1.set(edgeShape.m_vertex1);
				lEdgeInstance.vertex2.set(edgeShape.m_vertex2);

				lBox2dFixtureDefinition.shape = lEdgeInstance;

			} else {
				PolygonShape polygonShape = new PolygonShape();
				for (int i = 0; i < numVertices; i++)
					vertices[i] = jsonToVec("vertices", polygonValue, i);
				polygonShape.set(vertices, numVertices);
				lBox2dFixtureDefinition.fixtureDef.shape = polygonShape;

				Box2dPolygonInstance lPolygonInstance = new Box2dPolygonInstance();
				lPolygonInstance.vertices = vertices;
				lPolygonInstance.vertexCount = polygonShape.getVertexCount();
				lPolygonInstance.loadPhysics();
				lBox2dFixtureDefinition.shape = lPolygonInstance;

			}

		}

		String fixtureName = fixtureValue.optString("name", "");
		if (!fixtureName.equals("")) {
			setFixtureName(lBox2dFixtureDefinition, fixtureName);

		}

		return lBox2dFixtureDefinition;

	}

	Box2dJointDefinition j2b2Joint(JSONObject jointValue) throws JSONException {
		Box2dJointDefinition lBox2dJointDefinition = new Box2dJointDefinition();

		int bodyIndexA = jointValue.getInt("bodyA");
		int bodyIndexB = jointValue.getInt("bodyB");
		if (bodyIndexA >= mBodies.size() || bodyIndexB >= mBodies.size())
			return null;

		// keep these in scope after the if/else below
		RevoluteJointDef revoluteDef;
		PrismaticJointDef prismaticDef;
		DistanceJointDef distanceDef;
		PulleyJointDef pulleyDef;

		// WheelJointDef wheelDef;
		WeldJointDef weldDef;
		FrictionJointDef frictionDef;
		RopeJointDef ropeDef;

		String type = jointValue.optString("type", "");
		if (type.equals("revolute")) {
			revoluteDef = new RevoluteJointDef();
			revoluteDef.localAnchorA = jsonToVec("anchorA", jointValue);
			revoluteDef.localAnchorB = jsonToVec("anchorB", jointValue);
			revoluteDef.referenceAngle = jsonToFloat("refAngle", jointValue);
			revoluteDef.enableLimit = jointValue.optBoolean("enableLimit", false);
			revoluteDef.lowerAngle = jsonToFloat("lowerLimit", jointValue);
			revoluteDef.upperAngle = jsonToFloat("upperLimit", jointValue);
			revoluteDef.enableMotor = jointValue.optBoolean("enableMotor", false);
			revoluteDef.motorSpeed = jsonToFloat("motorSpeed", jointValue);
			revoluteDef.maxMotorTorque = jsonToFloat("maxMotorTorque", jointValue);

			lBox2dJointDefinition.jointDef = revoluteDef;

		} else if (type.equals("prismatic")) {
			lBox2dJointDefinition.jointDef = prismaticDef = new PrismaticJointDef();
			prismaticDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			prismaticDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			if (jointValue.has("localAxisA"))
				prismaticDef.localAxisA.set(jsonToVec("localAxisA", jointValue));
			else
				prismaticDef.localAxisA.set(jsonToVec("localAxis1", jointValue));

			prismaticDef.referenceAngle = jsonToFloat("refAngle", jointValue);
			prismaticDef.enableLimit = jointValue.optBoolean("enableLimit");
			prismaticDef.lowerTranslation = jsonToFloat("lowerLimit", jointValue);
			prismaticDef.upperTranslation = jsonToFloat("upperLimit", jointValue);
			prismaticDef.enableMotor = jointValue.optBoolean("enableMotor");
			prismaticDef.motorSpeed = jsonToFloat("motorSpeed", jointValue);
			prismaticDef.maxMotorForce = jsonToFloat("maxMotorForce", jointValue);
		} else if (type.equals("distance")) {
			lBox2dJointDefinition.jointDef = distanceDef = new DistanceJointDef();
			distanceDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			distanceDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			distanceDef.length = jsonToFloat("length", jointValue);
			distanceDef.frequencyHz = jsonToFloat("frequency", jointValue);
			distanceDef.dampingRatio = jsonToFloat("dampingRatio", jointValue);
		} else if (type.equals("pulley")) {
			lBox2dJointDefinition.jointDef = pulleyDef = new PulleyJointDef();
			pulleyDef.groundAnchorA.set(jsonToVec("groundAnchorA", jointValue));
			pulleyDef.groundAnchorB.set(jsonToVec("groundAnchorB", jointValue));
			pulleyDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			pulleyDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			pulleyDef.lengthA = jsonToFloat("lengthA", jointValue);
			pulleyDef.lengthB = jsonToFloat("lengthB", jointValue);
			pulleyDef.ratio = jsonToFloat("ratio", jointValue);
		} else if (type.equals("wheel")) {
			lBox2dJointDefinition.jointDef = revoluteDef = new RevoluteJointDef();
			revoluteDef.localAnchorA = jsonToVec("anchorA", jointValue);
			revoluteDef.localAnchorB = jsonToVec("anchorB", jointValue);
			revoluteDef.enableMotor = jointValue.optBoolean("enableMotor", false);
			revoluteDef.motorSpeed = jsonToFloat("motorSpeed", jointValue);
			revoluteDef.maxMotorTorque = jsonToFloat("maxMotorTorque", jointValue);
		} else if (type.equals("weld")) {
			lBox2dJointDefinition.jointDef = weldDef = new WeldJointDef();
			weldDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			weldDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			weldDef.referenceAngle = 0;
		} else if (type.equals("friction")) {
			lBox2dJointDefinition.jointDef = frictionDef = new FrictionJointDef();
			frictionDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			frictionDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			frictionDef.maxForce = jsonToFloat("maxForce", jointValue);
			frictionDef.maxTorque = jsonToFloat("maxTorque", jointValue);
		} else if (type.equals("rope")) {
			lBox2dJointDefinition.jointDef = ropeDef = new RopeJointDef();
			ropeDef.localAnchorA.set(jsonToVec("anchorA", jointValue));
			ropeDef.localAnchorB.set(jsonToVec("anchorB", jointValue));
			ropeDef.maxLength = jsonToFloat("maxLength", jointValue);
		}

		if (lBox2dJointDefinition.jointDef != null) {
			// set features common to all joints

			lBox2dJointDefinition.bodyAIndex = bodyIndexA;
			lBox2dJointDefinition.bodyBIndex = bodyIndexB;

			lBox2dJointDefinition.collideConnected = jointValue.optBoolean("collideConnected", false);

		}

		return lBox2dJointDefinition;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setBodyName(Box2dBodyDefinition body, String name) {
		mBodyToNameMap.put(body, name);
	}

	public void setFixtureName(Box2dFixtureDefinition fixture, String name) {
		mFixtureToNameMap.put(fixture, name);
	}

	public void setJointName(Box2dJointDefinition joint, String name) {
		mJointToNameMap.put(joint, name);
	}

	Box2dBodyDefinition lookupBodyFromIndex(int index) {
		if (mIndexToBodyMap.containsKey(index))
			return mIndexToBodyMap.get(index);
		else
			return null;
	}

	protected int lookupBodyIndex(Box2dBodyDefinition body) {
		Integer val = mBodyToIndexMap.get(body);
		if (null != val)
			return val.intValue();
		else
			return -1;
	}

	protected int lookupJointIndex(Box2dJointDefinition joint) {
		Integer val = mJointToIndexMap.get(joint);
		if (null != val)
			return val.intValue();
		else
			return -1;
	}

	public String getBodyName(Box2dBodyDefinition body) {
		return mBodyToNameMap.get(body);
	}

	public String getFixtureName(Box2dFixtureDefinition fixture) {
		return mFixtureToNameMap.get(fixture);
	}

	public String getJointName(Box2dJointDefinition joint) {
		return mJointToNameMap.get(joint);
	}

	public String floatToHex(float f) {
		int bits = Float.floatToIntBits(f);
		return Integer.toHexString(bits);
	}

	public void clear() {
		mIndexToBodyMap.clear();
		mBodyToIndexMap.clear();
		mJointToIndexMap.clear();
		mBodies.clear();
		mJoints.clear();

		mBodyToNameMap.clear();
		mFixtureToNameMap.clear();
		mJointToNameMap.clear();

	}

	float jsonToFloat(String name, JSONObject value) {
		return jsonToFloat(name, value, -1, 0);
	}

	float jsonToFloat(String name, JSONObject value, int index) {
		return jsonToFloat(name, value, index, 0);
	}

	float jsonToFloat(String name, JSONObject value, int index, float defaultValue) {
		if (!value.has(name))
			return defaultValue;

		if (index > -1) {
			JSONArray array = null;
			try {
				array = value.getJSONArray(name);
			} catch (JSONException e) {
			}
			if (null == array)
				return defaultValue;
			Object obj = array.opt(index);
			if (null == obj)
				return defaultValue;
			// else if ( value[name].isString() )
			// return hexToFloat( value[name].asString() );
			else
				return ((Number) obj).floatValue();
		} else {
			Object obj = value.opt(name);
			if (null == obj)
				return defaultValue;
			// else if ( value[name].isString() )
			// return hexToFloat( value[name].asString() );
			else
				return ((Number) obj).floatValue();
		}
	}

	Vec2 jsonToVec(String name, JSONObject value) throws JSONException {
		return jsonToVec(name, value, -1, new Vec2(0, 0));
	}

	Vec2 jsonToVec(String name, JSONObject value, int index) throws JSONException {
		return jsonToVec(name, value, index, new Vec2(0, 0));
	}

	Vec2 jsonToVec(String name, JSONObject value, int index, Vec2 defaultValue) throws JSONException {
		Vec2 vec = defaultValue;

		if (!value.has(name))
			return defaultValue;

		if (index > -1) {
			JSONObject vecValue = value.getJSONObject(name);
			JSONArray arrayX = vecValue.getJSONArray("x");
			JSONArray arrayY = vecValue.getJSONArray("y");

			vec.x = (float) arrayX.getDouble(index);
			float lTemp = (float) arrayY.getDouble(index);
			if (INVERT_Y)
				lTemp = -lTemp;
			vec.y = (float) arrayY.getDouble(index);

		} else {
			JSONObject vecValue = value.optJSONObject(name);
			if (null == vecValue)
				return defaultValue;
			else if (!vecValue.has("x")) // should be zero vector
				vec.set(0, 0);
			else {
				vec.x = jsonToFloat("x", vecValue);
				float lTemp = jsonToFloat("y", vecValue);
				if (INVERT_Y)
					lTemp = -lTemp;
				vec.y = lTemp;
			}
		}

		return vec;
	}

	public Box2dBodyDefinition[] getBodiesByName(String name) {
		Set<Box2dBodyDefinition> keys = new HashSet<>();
		for (Entry<Box2dBodyDefinition, String> entry : mBodyToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}

		return keys.toArray(new Box2dBodyDefinition[0]);

	}

	public Box2dFixtureDefinition[] getFixturesByName(String name) {
		Set<Box2dFixtureDefinition> keys = new HashSet<>();
		for (Entry<Box2dFixtureDefinition, String> entry : mFixtureToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys.toArray(new Box2dFixtureDefinition[0]);
	}

	public Box2dJointDefinition[] getJointsByName(String name) {
		Set<Box2dJointDefinition> keys = new HashSet<>();
		for (Entry<Box2dJointDefinition, String> entry : mJointToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}

		return keys.toArray(new Box2dJointDefinition[0]);

	}

	public Box2dBodyDefinition getBodyByName(String name) {
		for (Entry<Box2dBodyDefinition, String> entry : mBodyToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Box2dFixtureDefinition getFixtureByName(String name) {
		for (Entry<Box2dFixtureDefinition, String> entry : mFixtureToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Box2dJointDefinition getJointByName(String name) {
		for (Entry<Box2dJointDefinition, String> entry : mJointToNameMap.entrySet()) {
			if (name.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}