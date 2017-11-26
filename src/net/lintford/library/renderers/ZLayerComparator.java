package net.lintford.library.renderers;

import java.util.Comparator;

public class ZLayerComparator implements Comparator<BaseRenderer> {
	
	@Override
	public int compare(BaseRenderer o1, BaseRenderer o2) {
		return o1.ZDepth() < o2.ZDepth() ? -1 : o1.ZDepth() == o2.ZDepth() ? 0 : 1;
	}
	
}
