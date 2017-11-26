package net.lintford.library.screenmanager.transitions;

import net.lintford.library.core.time.GameTime;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;

public abstract class BaseTransition {

	// --------------------------------------
	// Variables
	// --------------------------------------
	
	protected float mProgress;
	protected TimeSpan mTransitionTime;
	
	// --------------------------------------
	// Properties
	// --------------------------------------
	
	public boolean isFinished(){
		return mProgress >= mTransitionTime.milliseconds();
	}
	
	public TimeSpan timeSpan(){
		return mTransitionTime;
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------
	
	public BaseTransition(TimeSpan pTransitionTime){
		mTransitionTime = pTransitionTime;
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------
	
	public void updateTransition(Screen pScreen, GameTime pGameTime){
		final float deltaTime = (float) pGameTime.elapseGameTimeMilli();
		mProgress += deltaTime;
		
	}
	
	public void reset(){
		mProgress = 0;
	}
	
}