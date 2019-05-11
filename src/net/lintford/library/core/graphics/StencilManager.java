package net.lintford.library.core.graphics;

public class StencilManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public class StencilObject {

		public static final int NO_OWNER_ID = -1;

		private char stencilMask;
		private boolean mIsInUse;
		private int mOwnerHash;

		public boolean isInUse() {
			return mIsInUse;
		}

		public void acquire() {

		}

		public void free() {
			mOwnerHash = NO_OWNER_ID;
		}

	}

	public static final int MAX_NUM_STENCIL_OBJECTS = 8;
	
	// --------------------------------------
	// Variables
	// --------------------------------------
	
	
	
	// --------------------------------------
	// Properties
	// --------------------------------------
	
	// --------------------------------------
	// Constructor
	// --------------------------------------
	
	public StencilManager() {
		
	}
	
	// --------------------------------------
	// Core-Methods
	// --------------------------------------
	
}
