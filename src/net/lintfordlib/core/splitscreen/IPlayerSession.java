package net.lintfordlib.core.splitscreen;

import java.util.List;

import net.lintfordlib.core.input.InputTypeMapper;

public interface IPlayerSession {

	void isPlayerEnabled(boolean playerEnabled);

	boolean isPlayerEnabled();

	PlayerSessionViewContainer getViewContainer();

	List<InputTypeMapper> inputMappers();

}
