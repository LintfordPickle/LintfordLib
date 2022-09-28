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

	public BitmapFontDataDefinition AddOrUpdate(String bitmapFontDefinition, String fontDefinitionFilepath) {
		BitmapFontDataDefinition lReturnBitmapFontDataDefinition;
		final int lNumCount = items.size();
		for (int i = 0; i < lNumCount; i++) {
			if (items.get(i).fontName.equals(bitmapFontDefinition)) {
				lReturnBitmapFontDataDefinition = items.get(i);
				lReturnBitmapFontDataDefinition.filepath = fontDefinitionFilepath;
				return lReturnBitmapFontDataDefinition;
			}
		}

		var lNewDefinition = new BitmapFontDataDefinition();
		lNewDefinition.fontName = bitmapFontDefinition;
		lNewDefinition.filepath = fontDefinitionFilepath;

		items.add(lNewDefinition);
		return lNewDefinition;
	}

	public BitmapFontDataDefinition AddIfNotExists(String bitmapFontDefinition, String fontDefinitionFilepath) {
		final int lNumCount = items.size();
		for (int i = 0; i < lNumCount; i++) {
			if (items.get(i).fontName.equals(bitmapFontDefinition)) {
				return items.get(i);
			}
		}

		var lNewDefinition = new BitmapFontDataDefinition();
		lNewDefinition.fontName = bitmapFontDefinition;
		lNewDefinition.filepath = fontDefinitionFilepath;

		items.add(lNewDefinition);
		return lNewDefinition;
	}
}
