package net.ld.library;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.ld.library.cellworld.CellGridLevelTest;
import net.ld.library.cellworld.CellTileTest;
import net.ld.library.core.camera.CameraTest;
import net.ld.library.core.graphics.VertexDataStructurePCTest;
import net.ld.library.core.graphics.VertexDataStructurePTTest;
import net.ld.library.core.graphics.textures.TextureManagerTest;
import net.ld.library.core.time.GameTimeTest;
import net.ld.library.core.time.TimeSpanTest;

@RunWith(Suite.class)
@SuiteClasses({
	/* LWJGLCoreTest.class, */
	VertexDataStructurePCTest.class, 
	VertexDataStructurePCTest.class, 
	VertexDataStructurePTTest.class,

	TextureManagerTest.class,

	GameTimeTest.class, 
	TimeSpanTest.class,

	CellGridLevelTest.class, 
	CellTileTest.class,
	
	CameraTest.class

})
public class CoreTests {

}
