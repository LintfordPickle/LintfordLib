package net.ld.library;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ld.library.core.LWJGLCoreTest;
import net.ld.library.core.graphics.VertexDataStructurePCTest;
import net.ld.library.core.graphics.VertexDataStructurePTTest;
import net.ld.library.core.graphics.textures.TextureManagerTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	LWJGLCoreTest.class,
	VertexDataStructurePCTest.class,
	VertexDataStructurePCTest.class,
	VertexDataStructurePTTest.class,
	TextureManagerTest.class
	})
public class CoreTests {

}
