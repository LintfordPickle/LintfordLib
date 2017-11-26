package net.lintford.library.core.xml.helpers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class StringArrayLoader {
	
	public String[] loadStringArray(final String pAssetFilename)
	{
		try
		{
			final SAXParserFactory loSPF = SAXParserFactory.newInstance();
			final SAXParser loSP = loSPF.newSAXParser();

			final StringArrayParser blockParser = new StringArrayParser(pAssetFilename);

			loSP.parse( pAssetFilename , blockParser );

			return blockParser.stringArray();

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
