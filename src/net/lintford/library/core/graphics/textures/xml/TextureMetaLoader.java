package net.lintford.library.core.graphics.textures.xml;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.textures.TextureMetaItem;

public class TextureMetaLoader {

	public ArrayList<TextureMetaItem> loadTextureMetaFile(final String pAssetFilename) {
		try {
			final SAXParserFactory loSPF = SAXParserFactory.newInstance();
			final SAXParser loSP = loSPF.newSAXParser();

			final TextureMetaParser lTextureMetaParser = new TextureMetaParser(pAssetFilename);

			loSP.parse(pAssetFilename, lTextureMetaParser);

			return lTextureMetaParser.parsedObject();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed parsing texture meta file (filename : " + pAssetFilename + ").");

		// If we hit this, we have failed.
		return null;
	}

}
