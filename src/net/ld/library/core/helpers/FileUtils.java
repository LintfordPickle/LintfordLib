package net.ld.library.core.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static String loadAsString(String pFile){
	
		System.out.println("Loading as string: " + pFile);
		
		String lResult = "";
		try{
			
			InputStream lInputStream = FileUtils.class.getResourceAsStream(pFile);
			
			BufferedReader lReader = new BufferedReader(new InputStreamReader(lInputStream));
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
