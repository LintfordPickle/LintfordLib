package net.lintford.library.core.maths;

public class EndPoint extends Vector2f implements Comparable<EndPoint> {
	
	private static final long serialVersionUID = 8784886641847663060L;
	
	public boolean begin;
	public Segment segment;
	public boolean visualize;
	public float angle;

	@Override
	public int compareTo(EndPoint o) {
		// Traverse in angle order
		if (angle > o.angle)
			return 1;
		if (angle < o.angle)
			return -1;

		// for tie case, return the begin nodes before end nodes
		if (!begin && o.begin)
			return 1;
		if (begin && !o.begin)
			return -1;
		return 0;
	}
}
