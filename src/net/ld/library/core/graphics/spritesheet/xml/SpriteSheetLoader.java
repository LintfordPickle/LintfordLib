package net.ld.library.core.graphics.spritesheet.xml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import net.ld.library.core.graphics.sprites.SpriteSheet;

public class SpriteSheetLoader {
	
	public SpriteSheet loadSpriteSheet(final String pAssetFilename, final String pSpriteSheetName)
	{
		try
		{
			final SAXParserFactory loSPF = SAXParserFactory.newInstance();
			final SAXParser loSP = loSPF.newSAXParser();

			final SpriteSheetParser lSpriteSheetParser = new SpriteSheetParser(pAssetFilename, pSpriteSheetName);

			loSP.parse( pAssetFilename , lSpriteSheetParser );

			return lSpriteSheetParser.parsedObject();

		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// If we hit this, we have failed.
		return null;
	}
	
}
