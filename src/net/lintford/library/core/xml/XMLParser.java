package net.lintford.library.core.xml;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser<T> extends DefaultHandler {
	
	// ===========================================================
    // Variables
    // ===========================================================
	
	protected T 					mObjectToParse;
	protected final StringBuilder 	mStringBuilder = new StringBuilder();
	
	// ===========================================================
    // Properties
    // ===========================================================
	
	/**
	 * 
	 * @return The object parsed from the XML file.
	 */
	public T parsedObject(){
		return mObjectToParse;
	}
	
	// ===========================================================
    // Inherited Methods
    // ===========================================================
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		mStringBuilder.append(ch, start, length);
	}
	
}
