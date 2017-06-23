package net.ld.library.core.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	/** Loads a binary file as a string, from a file. */
	public static String loadStringFromFile(String pFilepath) throws FileNotFoundException, IOException {

		System.out.println("Loading file as string: " + pFilepath);

		File lFile = new File(pFilepath);
		if (!lFile.exists()) {
			throw new FileNotFoundException("Cannot find the specified file (" + pFilepath + ")!");

		}

		String lResult = "";
		BufferedReader lReader = null;

		try {
			lReader = new BufferedReader(new FileReader(lFile));
			String lBuffer = "";

			while ((lBuffer = lReader.readLine()) != null) {
				lResult += lBuffer + "\n";

			}

		} finally {
			if (lReader != null) {
				lReader.close();

			}

		}

		return lResult;
	}

	/** Loads a binary file as a string, from a resource in the jar file. */
	public static String loadStringFromResource(String pFile) {

		System.out.println("Loading resource as string: " + pFile);

		String lResult = "";
		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFile);

			BufferedReader lReader = new BufferedReader(new InputStreamReader(lInputStream));
			String lBuffer = "";

			while ((lBuffer = lReader.readLine()) != null) {
				lResult += lBuffer + "\n";
			}

			lReader.close();

		} catch (IOException e) {
			System.err.println("Error loading file as String : " + e);
			throw new RuntimeException("Error loading file as String");
		}

		return lResult;
	}

}
