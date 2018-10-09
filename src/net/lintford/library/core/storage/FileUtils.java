package net.lintford.library.core.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

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

	/** Copies the source folder, and all its contents, to the destination folder. The destination folder is created if it doesn't already exist. */
	public static void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}
			
		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} catch (Exception e) {
				try {
					if(in != null)
						in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					if(out != null)
						out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}