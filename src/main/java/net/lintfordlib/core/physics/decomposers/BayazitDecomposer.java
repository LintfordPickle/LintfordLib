package net.lintfordlib.core.physics.decomposers;

//Taken from BayazitDecomposer.cs (FarseerPhysics.Common.Decomposition.BayazitDecomposer)
//at http://farseerphysics.codeplex.com

import java.security.InvalidParameterException;
import java.util.ArrayList;

import net.lintfordlib.core.maths.Vector2f;

/// <summary>
/// Convex decomposition algorithm created by Mark Bayazit (http://mnbayazit.com/)
/// For more information about this algorithm, see http://mnbayazit.com/406/bayazit
/// </summary>
public class BayazitDecomposer {

	public static final float Epsilon = 1.192092896e-07f;
	public static int MaxPolygonVertices = 6;

	public static Vector2f Cross(Vector2f a, float s) {
		return new Vector2f(s * a.y, -s * a.x);
	}

	private static Vector2f At(int i, ArrayList<Vector2f> vertices) {
		int s = vertices.size();
		return vertices.get(i < 0 ? s - (-i % s) : i % s);
	}

	private static ArrayList<Vector2f> Copy(int i, int j, ArrayList<Vector2f> vertices) {
		ArrayList<Vector2f> p = new ArrayList<Vector2f>();
		while (j < i)
			j += vertices.size();
		// p.reserve(j - i + 1);
		for (; i <= j; ++i) {
			p.add(At(i, vertices));
		}
		return p;
	}

	public static float GetSignedArea(ArrayList<Vector2f> vect) {
		int i;
		float area = 0;
		for (i = 0; i < vect.size(); i++) {
			int j = (i + 1) % vect.size();
			area += vect.get(i).x * vect.get(j).y;
			area -= vect.get(i).y * vect.get(j).x;
		}
		area /= 2.0f;
		return area;
	}

	public static float GetSignedArea(Vector2f[] vect) {
		int i;
		float area = 0;
		for (i = 0; i < vect.length; i++) {
			int j = (i + 1) % vect.length;
			area += vect[i].x * vect[j].y;
			area -= vect[i].y * vect[j].x;
		}
		area /= 2.0f;
		return area;
	}

	public static Boolean IsCounterClockWise(ArrayList<Vector2f> vect) {
		// We just return true for lines
		if (vect.size() < 3)
			return true;
		return (GetSignedArea(vect) > 0.0f);
	}

	public static Boolean IsCounterClockWise(Vector2f[] vect) {
		// We just return true for lines
		if (vect.length < 3)
			return true;
		return (GetSignedArea(vect) > 0.0f);
	}

// / <summary>
// / Decompose the polygon into several smaller non-concave polygon.
// / If the polygon is already convex, it will return the original polygon,
// unless it is over Settings.MaxPolygonVertices.
// / Precondition: Counter Clockwise polygon
// / </summary>
// / <param name="vertices"></param>
// / <returns></returns>
	public static ArrayList<ArrayList<Vector2f>> ConvexPartition(ArrayList<Vector2f> vertices) {
		// We force it to CCW as it is a precondition in this algorithm.
		// vertices.ForceCounterClockWise();
		if (!IsCounterClockWise(vertices)) {

			ArrayList<Vector2f> reversed = new ArrayList<Vector2f>(vertices.size());
			for (int i = vertices.size() - 1; i >= 0; i--) {
				reversed.add(vertices.get(i));
			}
			vertices = reversed;
		}
		ArrayList<ArrayList<Vector2f>> list = new ArrayList<ArrayList<Vector2f>>();
		float d, lowerDist, upperDist;
		Vector2f p;
		Vector2f lowerInt = new Vector2f();
		Vector2f upperInt = new Vector2f(); // intersection points
		int lowerIndex = 0, upperIndex = 0;
		ArrayList<Vector2f> lowerPoly, upperPoly;
		for (int i = 0; i < vertices.size(); ++i) {
			if (Reflex(i, vertices)) {
				lowerDist = upperDist = Float.MAX_VALUE; // std::numeric_limits<qreal>::max();
				for (int j = 0; j < vertices.size(); ++j) {
					// if line intersects with an edge
					if (Left(At(i - 1, vertices), At(i, vertices), At(j, vertices)) && RightOn(At(i - 1, vertices), At(i, vertices), At(j - 1, vertices))) {
						// find the point of intersection
						p = LineIntersect(At(i - 1, vertices), At(i, vertices), At(j, vertices), At(j - 1, vertices));
						if (Right(At(i + 1, vertices), At(i, vertices), p)) {
							// make sure it's inside the poly
							d = SquareDist(At(i, vertices), p);
							if (d < lowerDist) {
								// keep only the closest intersection
								lowerDist = d;
								lowerInt = p;
								lowerIndex = j;
							}
						}
					}
					if (Left(At(i + 1, vertices), At(i, vertices), At(j + 1, vertices)) && RightOn(At(i + 1, vertices), At(i, vertices), At(j, vertices))) {
						p = LineIntersect(At(i + 1, vertices), At(i, vertices), At(j, vertices), At(j + 1, vertices));
						if (Left(At(i - 1, vertices), At(i, vertices), p)) {
							d = SquareDist(At(i, vertices), p);
							if (d < upperDist) {
								upperDist = d;
								upperIndex = j;
								upperInt = p;
							}
						}
					}
				}
				// if there are no vertices to connect to, choose a point in the
				// middle
				if (lowerIndex == (upperIndex + 1) % vertices.size()) {
					Vector2f sp = new Vector2f((lowerInt.x + upperInt.x) / 2, (lowerInt.y + upperInt.y) / 2);
					lowerPoly = Copy(i, upperIndex, vertices);
					lowerPoly.add(sp);
					upperPoly = Copy(lowerIndex, i, vertices);
					upperPoly.add(sp);
				} else {
					double highestScore = 0, bestIndex = lowerIndex;
					while (upperIndex < lowerIndex)
						upperIndex += vertices.size();
					for (int j = lowerIndex; j <= upperIndex; ++j) {
						if (CanSee(i, j, vertices)) {
							double score = 1 / (SquareDist(At(i, vertices), At(j, vertices)) + 1);
							if (Reflex(j, vertices)) {
								if (RightOn(At(j - 1, vertices), At(j, vertices), At(i, vertices)) && LeftOn(At(j + 1, vertices), At(j, vertices), At(i, vertices))) {
									score += 3;
								} else {
									score += 2;
								}
							} else {
								score += 1;
							}
							if (score > highestScore) {
								bestIndex = j;
								highestScore = score;
							}
						}
					}
					lowerPoly = Copy(i, (int) bestIndex, vertices);
					upperPoly = Copy((int) bestIndex, i, vertices);
				}
				list.addAll(ConvexPartition(lowerPoly));
				list.addAll(ConvexPartition(upperPoly));
				return list;
			}
		}
		// polygon is already convex
		if (vertices.size() > MaxPolygonVertices) {
			lowerPoly = Copy(0, vertices.size() / 2, vertices);
			upperPoly = Copy(vertices.size() / 2, 0, vertices);
			list.addAll(ConvexPartition(lowerPoly));
			list.addAll(ConvexPartition(upperPoly));
		} else
			list.add(vertices);
		// The polygons are not guaranteed to be with collinear points. We
		// remove
		// them to be sure.
		for (int i = 0; i < list.size(); i++) {
			list.set(i, SimplifyTools.CollinearSimplify(list.get(i), 0));
		}
		// Remove empty vertice collections
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).size() == 0)
				list.remove(i);
		}
		return list;
	}

	private static Boolean CanSee(int i, int j, ArrayList<Vector2f> vertices) {
		if (Reflex(i, vertices)) {
			if (LeftOn(At(i, vertices), At(i - 1, vertices), At(j, vertices)) && RightOn(At(i, vertices), At(i + 1, vertices), At(j, vertices)))
				return false;
		} else {
			if (RightOn(At(i, vertices), At(i + 1, vertices), At(j, vertices)) || LeftOn(At(i, vertices), At(i - 1, vertices), At(j, vertices)))
				return false;
		}
		if (Reflex(j, vertices)) {
			if (LeftOn(At(j, vertices), At(j - 1, vertices), At(i, vertices)) && RightOn(At(j, vertices), At(j + 1, vertices), At(i, vertices)))
				return false;
		} else {
			if (RightOn(At(j, vertices), At(j + 1, vertices), At(i, vertices)) || LeftOn(At(j, vertices), At(j - 1, vertices), At(i, vertices)))
				return false;
		}
		for (int k = 0; k < vertices.size(); ++k) {
			if ((k + 1) % vertices.size() == i || k == i || (k + 1) % vertices.size() == j || k == j) {
				continue; // ignore incident edges
			}
			Vector2f intersectionPoint = new Vector2f();
			if (LineIntersect(At(i, vertices), At(j, vertices), At(k, vertices), At(k + 1, vertices), true, true, intersectionPoint)) {
				return false;
			}
		}
		return true;
	}

	public static Vector2f LineIntersect(Vector2f p1, Vector2f p2, Vector2f q1, Vector2f q2) {
		Vector2f i = new Vector2f();
		float a1 = p2.y - p1.y;
		float b1 = p1.x - p2.x;
		float c1 = a1 * p1.x + b1 * p1.y;
		float a2 = q2.y - q1.y;
		float b2 = q1.x - q2.x;
		float c2 = a2 * q1.x + b2 * q1.y;
		float det = a1 * b2 - a2 * b1;
		if (!FloatEquals(det, 0)) {
			// lines are not parallel
			i.x = (b2 * c1 - b1 * c2) / det;
			i.y = (a1 * c2 - a2 * c1) / det;
		}
		return i;
	}

	public static Boolean FloatEquals(float value1, float value2) {
		return Math.abs(value1 - value2) <= Epsilon;
	}

// / <summary>
// / This method detects if two line segments (or lines) intersect,
// / and, if so, the point of intersection. Use the
// <paramname="firstIsSegment"/> and
// / <paramname="secondIsSegment"/> parameters to set whether the
// intersection point
// / must be on the first and second line segments. Setting these
// / both to true means you are doing a line-segment to line-segment
// / intersection. Setting one of them to true means you are doing a
// / line to line-segment intersection test, and so on.
// / Note: If two line segments are coincident, then
// / no intersection is detected (there are actually
// / infinite intersection points).
// / Author: Jeremy Bell
// / </summary>
// / <param name="point1">The first point of the first line segment.</param>
// / <param name="point2">The second point of the first line
// segment.</param>
// / <param name="point3">The first point of the second line
// segment.</param>
// / <param name="point4">The second point of the second line
// segment.</param>
// / <param name="point">This is set to the intersection
// / point if an intersection is detected.</param>
// / <param name="firstIsSegment">Set this to true to require that the
// / intersection point be on the first line segment.</param>
// / <param name="secondIsSegment">Set this to true to require that the
// / intersection point be on the second line segment.</param>
// / <returns>True if an intersection is detected, false
// otherwise.</returns>
	public static Boolean LineIntersect(Vector2f point1, Vector2f point2, Vector2f point3, Vector2f point4, Boolean firstIsSegment, Boolean secondIsSegment, Vector2f point) {
		point = new Vector2f();
		// these are reused later.
		// each lettered sub-calculation is used twice, except
		// for b and d, which are used 3 times
		float a = point4.y - point3.y;
		float b = point2.x - point1.x;
		float c = point4.x - point3.x;
		float d = point2.y - point1.y;
		// denominator to solution of linear system
		float denom = (a * b) - (c * d);
		// if denominator is 0, then lines are parallel
		if (!(denom >= -Epsilon && denom <= Epsilon)) {
			float e = point1.y - point3.y;
			float f = point1.x - point3.x;
			float oneOverDenom = 1.0f / denom;
			// numerator of first equation
			float ua = (c * e) - (a * f);
			ua *= oneOverDenom;
			// check if intersection point of the two lines is on line segment 1
			if (!firstIsSegment || ua >= 0.0f && ua <= 1.0f) {
				// numerator of second equation
				float ub = (b * e) - (d * f);
				ub *= oneOverDenom;
				// check if intersection point of the two lines is on line
				// segment 2
				// means the line segments intersect, since we know it is on
				// segment 1 as well.
				if (!secondIsSegment || ub >= 0.0f && ub <= 1.0f) {
					// check if they are coincident (no collision in this case)
					if (ua != 0f || ub != 0f) {
						// There is an intersection
						point.x = point1.x + ua * b;
						point.y = point1.y + ua * d;
						return true;
					}
				}
			}
		}
		return false;
	}

// precondition: ccw
	private static Boolean Reflex(int i, ArrayList<Vector2f> vertices) {
		return Right(i, vertices);
	}

	private static Boolean Right(int i, ArrayList<Vector2f> vertices) {
		return Right(At(i - 1, vertices), At(i, vertices), At(i + 1, vertices));
	}

	private static Boolean Left(Vector2f a, Vector2f b, Vector2f c) {
		return Area(a, b, c) > 0;
	}

	private static Boolean LeftOn(Vector2f a, Vector2f b, Vector2f c) {
		return Area(a, b, c) >= 0;
	}

	private static Boolean Right(Vector2f a, Vector2f b, Vector2f c) {
		return Area(a, b, c) < 0;
	}

	private static Boolean RightOn(Vector2f a, Vector2f b, Vector2f c) {
		return Area(a, b, c) <= 0;
	}

	public static float Area(Vector2f a, Vector2f b, Vector2f c) {
		return a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y);
	}

	private static float SquareDist(Vector2f a, Vector2f b) {
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		return dx * dx + dy * dy;
	}
}

class SimplifyTools {
	private static Boolean[] _usePt;
	private static double _distanceTolerance;

// / <summary>
// / Removes all collinear points on the polygon.
// / </summary>
// / <param name="vertices">The polygon that needs simplification.</param>
// / <param name="collinearityTolerance">The collinearity tolerance.</param>
// / <returns>A simplified polygon.</returns>
	public static ArrayList<Vector2f> CollinearSimplify(ArrayList<Vector2f> vertices, float collinearityTolerance) {
		// We can't simplify polygons under 3 vertices
		if (vertices.size() < 3)
			return vertices;
		ArrayList<Vector2f> simplified = new ArrayList<Vector2f>();
		for (int i = 0; i < vertices.size(); i++) {
			int prevId = i - 1;
			if (prevId < 0)
				prevId = vertices.size() - 1;
			int nextId = i + 1;
			if (nextId >= vertices.size())
				nextId = 0;
			Vector2f prev = vertices.get(prevId);
			Vector2f current = vertices.get(i);
			Vector2f next = vertices.get(nextId);
			// If they collinear, continue
			if (Collinear(prev, current, next, collinearityTolerance))
				continue;
			simplified.add(current);
		}
		return simplified;
	}

	public static Boolean Collinear(Vector2f a, Vector2f b, Vector2f c, float tolerance) {
		return FloatInRange(BayazitDecomposer.Area(a, b, c), -tolerance, tolerance);
	}

	public static Boolean FloatInRange(float value, float min, float max) {
		return (value >= min && value <= max);
	}

// / <summary>
// / Removes all collinear points on the polygon.
// / Has a default bias of 0
// / </summary>
// / <param name="vertices">The polygon that needs simplification.</param>
// / <returns>A simplified polygon.</returns>
	public static ArrayList<Vector2f> CollinearSimplify(ArrayList<Vector2f> vertices) {
		return CollinearSimplify(vertices, 0);
	}

// / <summary>
// / Ramer-Douglas-Peucker polygon simplification algorithm. This is the
// general recursive version that does not use the
// / speed-up technique by using the Melkman convex hull.
// /
// / If you pass in 0, it will remove all collinear points
// / </summary>
// / <returns>The simplified polygon</returns>
	public static ArrayList<Vector2f> DouglasPeuckerSimplify(ArrayList<Vector2f> vertices, float distanceTolerance) {
		_distanceTolerance = distanceTolerance;
		_usePt = new Boolean[vertices.size()];
		for (int i = 0; i < vertices.size(); i++)
			_usePt[i] = true;
		SimplifySection(vertices, 0, vertices.size() - 1);
		ArrayList<Vector2f> result = new ArrayList<Vector2f>();
		for (int i = 0; i < vertices.size(); i++)
			if (_usePt[i])
				result.add(vertices.get(i));
		return result;
	}

	private static void SimplifySection(ArrayList<Vector2f> vertices, int i, int j) {
		if ((i + 1) == j)
			return;
		Vector2f A = vertices.get(i);
		Vector2f B = vertices.get(j);
		double maxDistance = -1.0;
		int maxIndex = i;
		for (int k = i + 1; k < j; k++) {
			double distance = DistancePointLine(vertices.get(k), A, B);
			if (distance > maxDistance) {
				maxDistance = distance;
				maxIndex = k;
			}
		}
		if (maxDistance <= _distanceTolerance)
			for (int k = i + 1; k < j; k++)
				_usePt[k] = false;
		else {
			SimplifySection(vertices, i, maxIndex);
			SimplifySection(vertices, maxIndex, j);
		}
	}

	private static double DistancePointPoint(Vector2f p, Vector2f p2) {
		double dx = p.x - p2.x;
		double dy = p.y - p2.x;
		return Math.sqrt(dx * dx + dy * dy);
	}

	private static double DistancePointLine(Vector2f p, Vector2f A, Vector2f B) {
		// if start == end, then use point-to-point distance
		if (A.x == B.x && A.y == B.y)
			return DistancePointPoint(p, A);
		// otherwise use comp.graphics.algorithms Frequently Asked Questions
		// method
		/*
		 * (1) AC dot AB r = --------- ||AB||^2 r has the following meaning: r=0 Point = A r=1 Point = B r<0 Point is on the backward extension of AB r>1 Point is on the forward extension of AB 0<r<1 Point is interior to AB
		 */
		double r = ((p.x - A.x) * (B.x - A.x) + (p.y - A.y) * (B.y - A.y)) / ((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
		if (r <= 0.0)
			return DistancePointPoint(p, A);
		if (r >= 1.0)
			return DistancePointPoint(p, B);
		/*
		 * (2) (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay) s = ----------------------------- Curve^2 Then the distance from C to Point = |s|*Curve.
		 */
		double s = ((A.y - p.y) * (B.x - A.x) - (A.x - p.x) * (B.y - A.y)) / ((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
		return Math.abs(s) * Math.sqrt(((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y)));
	}

// From physics2d.net
	public static ArrayList<Vector2f> ReduceByArea(ArrayList<Vector2f> vertices, float areaTolerance) {
		if (vertices.size() <= 3)
			return vertices;
		if (areaTolerance < 0) {
			throw new InvalidParameterException("areaTolerance: must be equal to or greater then zero.");
		}
		ArrayList<Vector2f> result = new ArrayList<Vector2f>();
		Vector2f v1, v2, v3;
		float old1, old2, new1;
		v1 = vertices.get(vertices.size() - 2);
		v2 = vertices.get(vertices.size() - 1);
		areaTolerance *= 2;
		for (int index = 0; index < vertices.size(); ++index, v2 = v3) {
			if (index == vertices.size() - 1) {
				if (result.size() == 0) {
					throw new InvalidParameterException("areaTolerance: The tolerance is too high!");
				}
				v3 = result.get(0);
			} else {
				v3 = vertices.get(index);
			}
			old1 = Cross(v1, v2);
			old2 = Cross(v2, v3);
			new1 = Cross(v1, v3);
			if (Math.abs(new1 - (old1 + old2)) > areaTolerance) {
				result.add(v2);
				v1 = v2;
			}
		}
		return result;
	}

	public static Float Cross(Vector2f a, Vector2f b) {
		return a.x * b.y - a.y * b.x;
	}

// From Eric Jordan's convex decomposition library
// / <summary>
// / Merges all parallel edges in the list of vertices
// / </summary>
// / <param name="vertices">The vertices.</param>
// / <param name="tolerance">The tolerance.</param>
	public static void MergeParallelEdges(ArrayList<Vector2f> vertices, float tolerance) {
		if (vertices.size() <= 3)
			return; // Can't do anything useful here to a triangle
		Boolean[] mergeMe = new Boolean[vertices.size()];
		int newNVertices = vertices.size();
		// Gather points to process
		for (int i = 0; i < vertices.size(); ++i) {
			int lower = (i == 0) ? (vertices.size() - 1) : (i - 1);
			int middle = i;
			int upper = (i == vertices.size() - 1) ? (0) : (i + 1);
			float dx0 = vertices.get(middle).x - vertices.get(lower).x;
			float dy0 = vertices.get(middle).y - vertices.get(lower).y;
			float dx1 = vertices.get(upper).y - vertices.get(middle).x;
			float dy1 = vertices.get(upper).y - vertices.get(middle).y;
			float norm0 = (float) Math.sqrt(dx0 * dx0 + dy0 * dy0);
			float norm1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
			if (!(norm0 > 0.0f && norm1 > 0.0f) && newNVertices > 3) {
				// Merge identical points
				mergeMe[i] = true;
				--newNVertices;
			}
			dx0 /= norm0;
			dy0 /= norm0;
			dx1 /= norm1;
			dy1 /= norm1;
			float cross = dx0 * dy1 - dx1 * dy0;
			float dot = dx0 * dx1 + dy0 * dy1;
			if (Math.abs(cross) < tolerance && dot > 0 && newNVertices > 3) {
				mergeMe[i] = true;
				--newNVertices;
			} else
				mergeMe[i] = false;
		}
		if (newNVertices == vertices.size() || newNVertices == 0)
			return;
		int currIndex = 0;
		// Copy the vertices to a new list and clear the old
		ArrayList<Vector2f> oldVertices = new ArrayList<Vector2f>(vertices);
		vertices.clear();
		for (int i = 0; i < oldVertices.size(); ++i) {
			if (mergeMe[i] || newNVertices == 0 || currIndex == newNVertices)
				continue;
			// Debug.Assert(currIndex < newNVertices);
			vertices.add(oldVertices.get(i));
			++currIndex;
		}
	}

// Misc
// / <summary>
// / Merges the identical points in the polygon.
// / </summary>
// / <param name="vertices">The vertices.</param>
// / <returns></returns>
	public static ArrayList<Vector2f> MergeIdenticalPoints(ArrayList<Vector2f> vertices) {
		ArrayList<Vector2f> results = new ArrayList<Vector2f>();
		for (int i = 0; i < vertices.size(); i++) {
			Vector2f vOriginal = vertices.get(i);

			boolean alreadyExists = false;
			for (int j = 0; j < results.size(); j++) {
				Vector2f v = results.get(j);
				if (vOriginal.equals(v)) {
					alreadyExists = true;
					break;
				}
			}
			if (!alreadyExists)
				results.add(vertices.get(i));
		}
		return results;
	}

// / <summary>
// / Reduces the polygon by distance.
// / </summary>
// / <param name="vertices">The vertices.</param>
// / <param name="distance">The distance between points. Points closer than
// this will be 'joined'.</param>
// / <returns></returns>
	public static ArrayList<Vector2f> ReduceByDistance(ArrayList<Vector2f> vertices, float distance) {
		// We can't simplify polygons under 3 vertices
		if (vertices.size() < 3)
			return vertices;
		ArrayList<Vector2f> simplified = new ArrayList<Vector2f>();
		for (int i = 0; i < vertices.size(); i++) {
			Vector2f current = vertices.get(i);
			int ii = i + 1;
			if (ii >= vertices.size())
				ii = 0;
			Vector2f next = vertices.get(ii);
			Vector2f diff = new Vector2f(next.x - current.x, next.y - current.y);
			// If they are closer than the distance, continue
			if (diff.len2() <= distance)
				continue;
			simplified.add(current);
		}
		return simplified;
	}

// / <summary>
// / Reduces the polygon by removing the Nth vertex in the vertices list.
// / </summary>
// / <param name="vertices">The vertices.</param>
// / <param name="nth">The Nth point to remove. Example: 5.</param>
// / <returns></returns>
	public static ArrayList<Vector2f> ReduceByNth(ArrayList<Vector2f> vertices, int nth) {
		// We can't simplify polygons under 3 vertices
		if (vertices.size() < 3)
			return vertices;
		if (nth == 0)
			return vertices;
		ArrayList<Vector2f> result = new ArrayList<Vector2f>(vertices.size());
		for (int i = 0; i < vertices.size(); i++) {
			if (i % nth == 0)
				continue;
			result.add(vertices.get(i));
		}
		return result;
	}
}