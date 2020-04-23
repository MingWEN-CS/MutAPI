package ust.hk.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Config {
	public static HashMap<String, String> projectPrefix;
	public static HashMap<String, String> clientClassPath;
	
	public static String benchmark_dataset = "LOCATION_OF_BENCHMARK";
	public static String dataset = "LOCATION_OF_DATASET";
	public static String targetLibrary;
	public static List<String> targetClients;
	public static List<String> targetAPIs;
	public static String targetAPI;
	public static String targetClient;
	public static String targetClass;
	public static String targetMethod;
	public static boolean extractConcernedFiles = false;
	public static boolean mutateSourceCode = false;
	public static boolean runMutants = false;
	public static boolean analyzeResults = false;
	public static boolean runPIT = false;
	public static boolean isTargetClass = false;
	public static boolean enrichTest = false;
	public static String project;
	public static String location;
	public static String APIFile = "100API.csv";
	public static HashSet<String> targetClientTestFolder = new HashSet<String>();
	public static String targetFolder = "integrate";
	
	public static int targetSource;
	public static String classPath;
	public static String classPathBase;
	public static boolean updateData = false;
	public static int mutantSize = 1000;
	public static int randomSize = mutantSize * 10;
	public static int mutantBound = 1;
	public static int usageLimit = 2000;
	
	static {
		projectPrefix = new HashMap<String, String>();
		projectPrefix.put("Joda-Time", "org.joda.time");
		projectPrefix.put("Lucene-Core", "org.apache.lucene");
		projectPrefix.put("Gson", "com.google.gson");
		projectPrefix.put("commons-lang", "org.apache.commons.lang");
		projectPrefix.put("commons-math", "org.apache.commons.math");
		projectPrefix.put("commons-text", "org.apache.commons.text");
		projectPrefix.put("ntru", "net.sf.ntru");
		projectPrefix.put("pdfbox/pdfbox", "org.apache.pdfbox");
		projectPrefix.put("wildfly-elytron", "org.wildfly.security");
		projectPrefix.put("jfreechart", "org.jfree");
		projectPrefix.put("santuario-java", "org.apache");
		projectPrefix.put("fop/fop-core", "org.apache.fop");
		projectPrefix.put("swingx/swingx-core", "org.jdesktop.swingx");
		projectPrefix.put("itextpdf/itext", "com.itextpdf");
		projectPrefix.put("jackrabbit/jackrabbit-core", "org.apache.jackrabbit");
		projectPrefix.put("commons-bcel", "org.apache.bcel");
		
		projectPrefix.put("directory-fortress-core", "org.apache.directory.fortress");

	}

	public static void main(String[] args) {

	}
}
