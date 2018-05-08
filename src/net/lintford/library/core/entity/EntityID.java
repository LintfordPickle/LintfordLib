package net.lintford.library.core.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityID {

	private static AtomicInteger mAtomicInteger = new AtomicInteger(Integer.MIN_VALUE);
	
	public static int getEntityNumber() {
		return mAtomicInteger.incrementAndGet();
		
	}
	

}
