package net.ld.library.core.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static String loadAsString(String pFile){
	
		String lResult = "";
		try{
			
			BufferedReader lReader = new BufferedReader(new FileReader(pFile));
			String lBuffer = "";
			
			while((lBuffer = lReader.readLine()) != null){
				lResult += lBuffer + "\n";
			}
			
			lReader.close();
			
		}
		catch(IOException e){
			System.err.println( "Error loading file as String : " + e );
			throw new RuntimeException("Error loading file as String");
		}
		
		return lResult;
	}
}
