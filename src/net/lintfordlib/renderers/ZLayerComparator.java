package net.lintfordlib.renderers;

import java.util.Comparator;

public class ZLayerComparator implements Comparator<BaseRenderer> {

	@Override
	public int compare(BaseRenderer baseRenderer1, BaseRenderer baseRenderer2) {
		return baseRenderer1.zDepth() < baseRenderer2.zDepth() ? -1 : baseRenderer1.zDepth() == baseRenderer2.zDepth() ? 0 : 1;
	}
}
