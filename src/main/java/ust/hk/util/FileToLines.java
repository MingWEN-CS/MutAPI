package ust.hk.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FileToLines {

	public static List<String> fileToLines(String filename) {
		List<String> lines = new ArrayList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * retrieve lines from a file, starting from a given line
	 * @param filename
	 * @param startLine
	 * @return
	 */
	public static List<String> fileToLines(String filename, int startLine) {
		List<String> lines = fileToLines(filename);
		return lines.subList(startLine, lines.size());
	}
	
	
	public static String readResourceFile(String resourceFilePath) {
		StringBuffer buffer = new StringBuffer();
		try {
			InputStream is = FileToLines.class.getResourceAsStream(resourceFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("when reading resource file " + resourceFilePath + " come across exeception.");
			return null;
		}
		return buffer.toString();
	}

	public static String readFile(String file) {
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("when reading file " + file + " come across exeception.");
			return null;
		}
		return buffer.toString();
	}
	
	public static List<String> readString(String str){
		List<String> lines = new ArrayList<String>();
		try{
			BufferedReader reader = new BufferedReader(new StringReader(str));
			String line = reader.readLine();
			while(line!=null){
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e ){
			System.out.println("when reading String " + str +  " come across exeception.");
		}
		return lines;
	}
	
}
