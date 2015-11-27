package net.ld.library.core.graphics.spritesheet.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.ld.library.core.graphics.sprites.Sprite;
import net.ld.library.core.graphics.sprites.SpriteSheet;
import net.ld.library.core.xml.XMLParser;

public class SpriteSheetParser extends XMLParser<SpriteSheet> {

	// ===========================================================
	// Variables
	// ===========================================================

	private String mTextureFilename;
//	private int mTextureWidth;
//	private int mTextureHeight;
	
	private Sprite mNewSprite;
	private String mNewSpriteName;
	private int mNewSpriteX;
	private int mNewSpriteY;
	private int mNewSpriteW;
	private int mNewSpriteH;

	// ===========================================================
	// Properties
	// ===========================================================

	public SpriteSheet blockfDefinition() {
		return mObjectToParse;
	}

	// ===========================================================
	// Constructor
	// ===========================================================

	public SpriteSheetParser(String pAssetFilename, String pAssetName) {
		mObjectToParse = new SpriteSheet(pAssetName);
		
		mNewSpriteName = "";
		mNewSpriteX = 0;
		mNewSpriteY = 0;
		mNewSpriteW = 0;
		mNewSpriteH = 0;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		super.startElement(uri, localName, qName, attributes);

		if (qName.equalsIgnoreCase(SpriteSheetConstants.SPRITESHEET_ROOT)) {
			mTextureFilename = attributes.getValue("imagePath");
//			mTextureWidth = Integer.parseInt(attributes.getValue("width"));
//			mTextureHeight = Integer.parseInt(attributes.getValue("height"));
			
			if(mTextureFilename != null && mTextureFilename.length() > 0){
				mObjectToParse.setTextureFilename(mTextureFilename);
			}
		}
		
		else if (qName.equalsIgnoreCase(SpriteSheetConstants.SPRITESHEET_ITEM)) {
			mNewSpriteName = attributes.getValue("n");
			mNewSpriteX= Integer.parseInt(attributes.getValue("x"));
			mNewSpriteY= Integer.parseInt(attributes.getValue("y"));
			mNewSpriteW= Integer.parseInt(attributes.getValue("w"));
			mNewSpriteH= Integer.parseInt(attributes.getValue("h"));
			
			mNewSprite = new Sprite(mNewSpriteX, mNewSpriteY, mNewSpriteW, mNewSpriteH);
			
			if(mNewSpriteName != null && mNewSpriteName.length() > 0){
				mObjectToParse.addSpriteDefinition(mNewSpriteName, mNewSprite);
			}
		}
		
		// Clear the string builder ready for any values between the next
		// elements.
		mStringBuilder.delete(0, mStringBuilder.length());

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		super.endElement(uri, localName, qName);

		checkStandardEndElements(uri, localName, qName);

	}

	private void checkStandardEndElements(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase(SpriteSheetConstants.SPRITESHEET_ROOT)) {
			// There is nothing to do for the root tag but to acknowledge it.
		}
	}
}
