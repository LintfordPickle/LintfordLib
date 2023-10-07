package net.lintfordlib.renderers;

import java.util.Comparator;

public class ZLayerComparator implements Comparator<BaseRenderer> {

	@Override
	public int compare(BaseRenderer baseRenderer1, BaseRenderer baseRenderer2) {
		return baseRenderer1.ZDepth() < baseRenderer2.ZDepth() ? -1 : baseRenderer1.ZDepth() == baseRenderer2.ZDepth() ? 0 : 1;
	}
}
