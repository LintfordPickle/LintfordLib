package net.lintfordlib.core.xml.helpers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class StringArrayLoader {

	public String[] loadStringArray(final String assetFilename) {
		try {
			final var lParserFactory = SAXParserFactory.newInstance();
			final var lSaxParser = lParserFactory.newSAXParser();
			final var lBlockParser = new StringArrayParser(assetFilename);

			lSaxParser.parse(assetFilename, lBlockParser);

			return lBlockParser.stringArray();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
