package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseData;

public abstract class BaseDefinition extends BaseData {

	private static final long serialVersionUID = -1448531772258448590L;

	public String mDefinitionName; // Static, unique from data-file.
	public int mDefinitionID; // Calculated and cached. Re-Calculated in block/mod change detected.

}
