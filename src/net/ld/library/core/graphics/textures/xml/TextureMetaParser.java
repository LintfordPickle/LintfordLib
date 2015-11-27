package net.ld.library.core.graphics.textures.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.ld.library.core.graphics.textures.TextureMetaItem;
import net.ld.library.core.xml.XMLParser;

public class TextureMetaParser extends XMLParser<ArrayList<TextureMetaItem>> {

	// ===========================================================
	// Variables
	// ===========================================================

	private String name = null;
	private String location = null;
	private int filter = -1;

	// ===========================================================
	// Properties
	// ===========================================================

	public ArrayList<TextureMetaItem> blockfDefinition() {
		return mObjectToParse;
	}

	// ===========================================================
	// Constructor
	// ===========================================================

	public TextureMetaParser(String assetFilename) {
		mObjectToParse = new ArrayList<>();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		super.startElement(uri, localName, qName, attributes);

		// Clear the string builder ready for any values between the next
		// elements.
		mStringBuilder.delete(0, mStringBuilder.length());

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		super.endElement(uri, localName, qName);

		checkStandardEndElements(uri, localName, qName);

	}

	private void checkStandardEndElements(String uri, String localName,
			String qName) throws SAXException {
		if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_ROOT)) {
			// There is nothing to do for the root tag but to acknowledge it.
		}

		else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_NAME)) {
			name = mStringBuilder.toString();
		}

		else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_LOCATION)) {
			location = mStringBuilder.toString();
		}
		
		else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_FILTER)) {
			filter = Integer.parseInt(mStringBuilder.toString());
		}
		
		else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_ITEM)) {
			if(name == null || location == null || filter == -1){
				throw new RuntimeException("Error loading TextureMetaItem");
			}
			
			mObjectToParse.add(new TextureMetaItem(name, location, filter));
			name = null;
			location = null;
			filter = -1;
		}

		else {

		}
	}
}
