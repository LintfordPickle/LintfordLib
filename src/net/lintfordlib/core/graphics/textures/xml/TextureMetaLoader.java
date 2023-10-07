package net.lintfordlib.core.graphics.textures.xml;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.textures.TextureMetaItem;

public class TextureMetaLoader {

	public ArrayList<TextureMetaItem> loadTextureMetaFile(final String assetFilename) {
		try {
			final var loSPF = SAXParserFactory.newInstance();
			final var loSP = loSPF.newSAXParser();
			final var lTextureMetaParser = new TextureMetaParser(assetFilename);

			loSP.parse(assetFilename, lTextureMetaParser);

			return lTextureMetaParser.parsedObject();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed parsing texture meta file (filename : " + assetFilename + ").");

		// If we hit this, we have failed.
		return null;
	}
}
