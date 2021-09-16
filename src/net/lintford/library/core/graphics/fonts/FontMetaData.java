package net.lintford.library.core.graphics.fonts;

import java.util.ArrayList;
import java.util.List;

public class FontMetaData {

	public class BitmapFontDataDefinition {
		public String fontName;
		public String filepath;
		public boolean reloadable;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public List<BitmapFontDataDefinition> items = new ArrayList<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontMetaData() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public BitmapFontDataDefinition AddOrUpdate(String pBitmapFontDefinition, String pFontDefinitionFilepath) {
		BitmapFontDataDefinition lReturnBitmapFontDataDefinition;
		final int lNumCount = items.size();
		for (int i = 0; i < lNumCount; i++) {
			if (items.get(i).fontName.equals(pBitmapFontDefinition)) {
				lReturnBitmapFontDataDefinition = items.get(i);
				lReturnBitmapFontDataDefinition.filepath = pFontDefinitionFilepath;
				return lReturnBitmapFontDataDefinition;
			}
		}

		var lNewDefinition = new BitmapFontDataDefinition();
		lNewDefinition.fontName = pBitmapFontDefinition;
		lNewDefinition.filepath = pFontDefinitionFilepath;

		items.add(lNewDefinition);
		return lNewDefinition;
	}

	public BitmapFontDataDefinition AddIfNotExists(String pBitmapFontDefinition, String pFontDefinitionFilepath) {
		final int lNumCount = items.size();
		for (int i = 0; i < lNumCount; i++) {
			if (items.get(i).fontName.equals(pBitmapFontDefinition)) {
				return items.get(i);
			}
		}

		var lNewDefinition = new BitmapFontDataDefinition();
		lNewDefinition.fontName = pBitmapFontDefinition;
		lNewDefinition.filepath = pFontDefinitionFilepath;

		items.add(lNewDefinition);
		return lNewDefinition;
	}

	public void loadFromMetaFile(String pFilepath) {

	}

}
