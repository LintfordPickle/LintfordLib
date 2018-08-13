package net.lintford.library.core.graphics.textures.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.TextureMetaItem;
import net.lintford.library.core.xml.XMLParser;

public class TextureMetaParser extends XMLParser<ArrayList<TextureMetaItem>> {

	// --------------------------------------==============
	// Variables
	// --------------------------------------==============

	private String mAssetFilename;
	private String mName = null;
	private String mLocation = null;
	private int mFilter = -1;

	// --------------------------------------==============
	// Properties
	// --------------------------------------==============

	public ArrayList<TextureMetaItem> blockfDefinition() {
		return mObjectToParse;
	}

	// --------------------------------------==============
	// Constructor
	// --------------------------------------==============

	public TextureMetaParser(String pAssetFilename) {
		mObjectToParse = new ArrayList<>();
		mAssetFilename = pAssetFilename;

	}

	// --------------------------------------==============
	// Methods
	// --------------------------------------==============

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

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
		if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_ROOT)) {
			// There is nothing to do for the root tag but to acknowledge it.
		} else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_NAME)) {
			mName = mStringBuilder.toString();
		} else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_LOCATION)) {
			mLocation = mStringBuilder.toString();
		} else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_FILTER)) {
			mFilter = Integer.parseInt(mStringBuilder.toString());
		} else if (qName.equalsIgnoreCase(TextureMetaConstants.TEXTUREMETA_ITEM)) {
			if (mName == null || mLocation == null || mFilter == -1) {
				throw new RuntimeException("Error loading TextureMetaItem");
			}

			mObjectToParse.add(new TextureMetaItem(mName, mLocation, mFilter));
			mName = null;
			mLocation = null;
			mFilter = -1;

		} else {
			// Report that this tag was not recognized in the file //
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unrecognised xml tag in TextureMetaParser (" + mAssetFilename + ")");

		}
	}

}