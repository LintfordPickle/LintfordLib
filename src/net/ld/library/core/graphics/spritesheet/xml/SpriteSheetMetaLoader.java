package net.ld.library.core.graphics.spritesheet.xml;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SpriteSheetMetaLoader {

	public Map<String, String> loadSpriteSheetMeta(final String pAssetFilename) {
		try {
			final SAXParserFactory loSPF = SAXParserFactory.newInstance();
			final SAXParser loSP = loSPF.newSAXParser();

			final SpriteSheetMetaParser lSpriteSheetParser = new SpriteSheetMetaParser(pAssetFilename);

			loSP.parse(pAssetFilename, lSpriteSheetParser);

			return lSpriteSheetParser.parsedObject();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Failed parsing sprite sheet (file : " + pAssetFilename + ").");

		// If we hit this, we have failed.
		return null;
	}

}
