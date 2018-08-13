package net.lintford.library.core.xml.helpers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.xml.XMLParser;

public class StringArrayParser extends XMLParser<String[]> {

	private ArrayList<String> mListOfStrings = new ArrayList<>();

	// ===========================================================
	// Properties
	// ===========================================================

	public String[] stringArray() {

		if (mListOfStrings.size() == 0)
			return null;

		return mListOfStrings.toArray(new String[mListOfStrings.size()]);
	}

	// ===========================================================
	// Constructor
	// ===========================================================

	public StringArrayParser(String assetFilename) {

	}

	// ===========================================================
	// Methods
	// ===========================================================

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
		if (qName.equalsIgnoreCase(StringArrayConstants.STRINGARRAY_START_TAG)) {
			// There is nothing todo for the root tag but to acknowledge it.
		}

		else if (qName.equalsIgnoreCase(StringArrayConstants.STRINGARRAY_ITEM_TAG)) {
			mListOfStrings.add(mStringBuilder.toString());

		} else {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "StringArrayParser unrecognized tag in XML : " + qName);

		}
	}

}