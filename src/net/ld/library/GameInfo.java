package net.ld.library;

public interface GameInfo {

	public default String applicationName() {
		return "unnamed";
	}

	public default String windowTitle() {
		return "unamed";
	}

	public default String configFileLocation() {
		return "";
	}

	public default int windowWidth(){
		return 640;
	}
	
	public default int windowHeight(){
		return 480;
	}
	
	public default boolean windowResizeable(){
		return false;
	}
	
	public default boolean windowCanBeFullscreen(){
		return false;
	}
	
}
