package net.lintford.library.core.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static String loadString(String pPathName) {
		if (pPathName == null || pPathName.length() == 0) {
			return null;
		}

		if (pPathName.charAt(0) == '/') {
			return loadStringFromResource(pPathName);
		} else {
			return loadStringFromFile(pPathName);
		}

	}

	/** Loads a binary file as a string, from a file. */
	public static String loadStringFromFile(String pPathName) {
		String lResult = "";

		try {

			BufferedReader lReader = new BufferedReader(new FileReader(pPathName));
			String lBuffer = "";

			while ((lBuffer = lReader.readLine()) != null) {
				lResult += lBuffer + "\n";
			}

			lReader.close();

		} catch (IOException e) {
			System.err.println("Error loading file " + pPathName.toString());
			System.err.println(e.getMessage());
			e.printStackTrace();

		}

		return lResult;
	}

	/** Loads a binary file as a string, from a resource in the jar file. */
	public static String loadStringFromResource(String pPathName) {
		String lResult = "";

		try {

			InputStream lInputStream = FileUtils.class.getResourceAsStream(pPathName);

			BufferedReader lReader = new BufferedReader(new InputStreamReader(lInputStream));
			String lBuffer = "";

			while ((lBuffer = lReader.readLine()) != null) {
				lResult += lBuffer + "\n";
			}

			lReader.close();

		} catch (IOException e) {
			System.err.println("Error loading text resource " + pPathName.toString());
			System.err.println(e.getMessage());
			e.printStackTrace();

		}

		return lResult;
	}

}