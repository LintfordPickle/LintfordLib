package net.ld.library.core.graphics.spritesheet.xml;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.ld.library.core.xml.XMLParser;

public class SpriteSheetMetaParser extends XMLParser<Map<String,String>> {

	// ===========================================================
	// Variables
	// ===========================================================

	private String mNewSpriteSheetName;
	private String mNewSpriteSheetFilename;

	// ===========================================================
	// Properties
	// ===========================================================

	public Map<String, String> blockfDefinition() {
		return mObjectToParse;
	}

	// ===========================================================
	// Constructor
	// ===========================================================

	public SpriteSheetMetaParser(String pAssetFilename) {
		mObjectToParse = new HashMap<String, String>();
		
		mNewSpriteSheetName = "";
		mNewSpriteSheetFilename = "";
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase(SpriteSheetMetaConstants.SPRITESHEET_ITEM)) {
			mNewSpriteSheetName = attributes.getValue(SpriteSheetMetaConstants.SPRITESHEET_NAME);
			mNewSpriteSheetFilename = attributes.getValue(SpriteSheetMetaConstants.SPRITESHEET_FILENAME);
			
			mObjectToParse.put(mNewSpriteSheetName, mNewSpriteSheetFilename);
		}
		// Clear the string builder ready for any values between the next
		// elements.
		mStringBuilder.delete(0, mStringBuilder.length());

	}
}
