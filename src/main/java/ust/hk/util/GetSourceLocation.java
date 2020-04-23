package ust.hk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetSourceLocation {
	public static String getPath(String prefix) {
	    List<String> pathes = new ArrayList<String>();
		pathes.add("src/main/java");
		pathes.add("src/java");
		pathes.add("source");
		pathes.add("src");
		String valid = "";
		for (String path : pathes) {
			File file = new File(prefix + File.separator + path);
			if (file.exists()) {
				valid = path;
				break;
			}
		}
		if (valid.equals(""))
			System.err.println("could not find valid path");
		return valid;
	}
	
	public static String getBinFolder(String prefix) {
		List<String> pathes = new ArrayList<String>();
		pathes.add("build/classes/main");
		pathes.add("target/classes");
		pathes.add("build/classes");
		pathes.add("build");
		
		String valid = "";
		for (String path : pathes) {
			File file = new File(prefix + File.separator + path);
			if (file.exists() && file.isDirectory()) {
				valid = path;
				break;
			}
		}
		if (valid.equals(""))
			System.err.println("could not find valid path");
		return valid;
	}
	
	public static String getBinTestFolder(String prefix) {
		List<String> pathes = new ArrayList<String>();
		pathes.add("build/classes/test");
		pathes.add("target/test-classes");
		pathes.add("build-tests");
		pathes.add("build/tests");
		pathes.add("target/tests");
		String valid = "";
		for (String path : pathes) {
			File file = new File(prefix + File.separator + path);
			if (file.exists() && file.isDirectory()) {
				valid = path;
				break;
			}
		}
		if (valid.equals(""))
			System.err.println("could not find valid path");
		return valid;
	}
	
	public static List<String> getFailingTests(String loc, String project, int bid) {
		String infoFile = loc + File.separator + project + "_" + bid + "_info.txt";
		List<String> failingTests = new ArrayList<>();
		List<String> lines = FileToLines.fileToLines(infoFile);
		for (String line : lines) {
			String[] split = line.split("\t");
			if (split[0].equals("t")) {
				failingTests.add(split[1].split("::")[0]);
			}
		}	
		return failingTests;
	}
	
	public static List<Pair<String, String>> getFailingTestMethods(String loc, String project, int bid) {
		String infoFile = loc + File.separator + project + "_" + bid + "_info.txt";
		List<Pair<String, String>> failingTests = new ArrayList<>();
		List<String> lines = FileToLines.fileToLines(infoFile);
		for (String line : lines) {
			String[] split = line.split("\t");
			if (split[0].equals("t")) {
				split = split[1].split("::");
				failingTests.add(new Pair<String, String>(split[0], split[1]));
			}
		}	
		return failingTests;
	}
	
	public static HashSet<String> getConcernedClass(String loc, String project, int bid) {
		HashSet<String> classes = new HashSet<String>();
		String filename = loc + File.separator + "FSAccuracy" + File.separator + "originalTest.txt";
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			classes.add(line.split("\t")[0]);
		}
		return classes;
	}
	
	public static List<String> getFailingSourceFiles(String loc, String project, int bid) {
		String infoFile = loc + File.separator + project + "_" + bid + "_info.txt";
		List<String> failingSourceFiles = new ArrayList<>();
		List<String> lines = FileToLines.fileToLines(infoFile);
		for (String line : lines) {
			String[] split = line.split("\t");
			if (split[0].equals("s")) {
				failingSourceFiles.add(split[1]);
			}
		}	
		return failingSourceFiles;
	}
	
	
	public static void transformSourceFilesToUTF8(String path) throws IOException {
		String charset = "ISO-8859-1"; // or what corresponds
		BufferedReader in = new BufferedReader( 
		      new InputStreamReader (new FileInputStream(new File(path)), charset));
		String line;
		String newContnet = in.readLine();
		while( (line = in.readLine()) != null) { 
			newContnet = newContnet + "\n" + line;
		}
		in.close();
		WriteLinesToFile.writeToFiles(newContnet, path);
	}
	
	public static List<String> getRequiredSourceFiles(String loc, String project, int bid) {
		String infoFile = loc + File.separator + project + "_" + bid + "_info.txt";
		List<String> failingSourceFiles = new ArrayList<>();
		List<String> lines = FileToLines.fileToLines(infoFile);
		for (String line : lines) {
			String[] split = line.split("\t");
			if (split[0].equals("s") || split[0].equals("d")) {
				failingSourceFiles.add(split[1]);
			}
		}	
		return failingSourceFiles;
	}
	
	public static String getTest(String prefix) {
		List<String> pathes = new ArrayList<String>();
		pathes.add("src/main/tests");pathes.add("src/main/test");
		pathes.add("src/java/tests");pathes.add("src/java/test");
		pathes.add("src/test/java");
		pathes.add("src/tests");
		pathes.add("src/test");
		pathes.add("tests");
		pathes.add("test");
		String valid = "";
		for (String path : pathes) {
			File file = new File(prefix + File.separator + path);
			if (file.exists()) {
				valid = path;
				break;
			}
		}
		if (valid.equals(""))
			System.err.println("could not find valid path");
		return valid;
	}
}
