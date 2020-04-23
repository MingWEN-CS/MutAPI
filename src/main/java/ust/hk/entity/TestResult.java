package ust.hk.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ust.hk.config.Config;
import ust.hk.util.Pair;

public class TestResult {
	
	public String testClass;
	public List<String> failingTest;
	public List<String> failingException;
	public List<List<String>> failingStackTraces;
	
	public TestResult(String testClass) {
		this.testClass = testClass;
		failingTest = new ArrayList<String>();
		failingStackTraces = new ArrayList<List<String>>();
		failingException = new ArrayList<String>();
	}
	
	public void extractTestResult(String content) {
		
	}
	
	public HashSet<String> obtainFailingSignature() {
		HashSet<String> signatures = new HashSet<>();
		for (int i = 0; i < failingTest.size(); i++) {
			signatures.add(testClass + "_" + failingTest.get(i) + "_" + failingException.get(i));
		}
		return signatures;
	}
	
	public HashSet<String> obtainFailingTest() {
		HashSet<String> signatures = new HashSet<>();
		for (int i = 0; i < failingTest.size(); i++) {
			signatures.add(testClass + "_" + failingTest.get(i));
		}
		return signatures;
	}
	
	public void addFailMethod(String test, String exception, List<String> trace) {
		failingTest.add(test);
		failingException.add(exception);
		failingStackTraces.add(new ArrayList<String>(trace));
	}
	
	public void output() {
		for (int i = 0; i < failingTest.size(); i++) {
			System.out.println(failingTest.get(i) + "\t" + failingException.get(i) + "\t" + failingStackTraces.get(i).size());
		}
	}
	
	public List<List<Pair<Integer, String>>> obtainLibraryFrames(String targetAPI, String targetClient, HashSet<String> originalFailingTest) {
		List<List<Pair<Integer, String>>> Allframes = new ArrayList<List<Pair<Integer, String>>>();
		String prefix = Config.projectPrefix.get(Config.targetClient);
		for (int i = 0; i < failingStackTraces.size(); i++) {
			
			// If the original program has such failings, we do not add it into analysis
			String failingSignature = testClass + "_" + failingTest.get(i);
			if (originalFailingTest.contains(failingSignature)) continue;
			
			List<String> frames = failingStackTraces.get(i);
			List<Pair<Integer, String>> targetFrames = new ArrayList<>();
			targetFrames.add(new Pair<Integer, String>(-1, failingException.get(i)));
			for (int j = 0; j < frames.size(); j++) {
				String frame = frames.get(j);
				if (!frame.startsWith("at ")) continue;
				frame = frame.substring(3);
				
				if (frame.startsWith("org.junit.") || frame.startsWith("sun.reflect.") || frame.contains("junit")) continue;
				
				// to handle specific APIs unrelated to Java
				if (!(targetAPI.equals("org.apache.commons.lang3.text.StrBuilder.getNullText") && targetClient.equals("commons-lang"))
						
						&& !(targetAPI.equals("org.apache.commons.math4.geometry.euclidean.threed.Line.intersection") && targetClient.equals("commons-math"))
						
						&& !(targetAPI.startsWith("org.jfree") && targetClient.equals("jfreechart"))
						
						&& !(targetAPI.startsWith("com.itextpdf") && targetClient.equals("itextpdf/itext"))
						
						&& !(targetAPI.startsWith("org.apache.jackrabbit") && targetClient.equals("jackrabbit/jackrabbit-core"))) {
							if (frame.startsWith(prefix)) continue;
				}
				if (targetAPI.startsWith("java") && !(frame.startsWith("java") || frame.startsWith("com.sun."))) continue;
				targetFrames.add(new Pair<Integer, String>(j, frame));
			}
			//if (failingException.get(i).contains("Null"))
			//	System.out.println(targetFrames.toString());

			Allframes.add(targetFrames);
		}
		return Allframes;
	}
	
	public List<Boolean> analyzeFrames() {
		String prefix = Config.projectPrefix.get(Config.targetClient);
		List<Boolean> results = new ArrayList<Boolean>();
		for (int i = 0; i < failingStackTraces.size(); i++) {
			List<String> frames = failingStackTraces.get(i);
			for (String frame : frames) {
				if (!frame.startsWith("at ")) continue;
				frame = frame.substring(3);
				if (frame.startsWith("org.junit.") || frame.startsWith("sun.reflect.")) continue;
				if (frame.startsWith(prefix)) results.add(false);
				else {
                    results.add(true);
			        //System.out.println(i + "\t" + failingException.get(i) + "\t" + frame);
				}
				break;
			}
		}
		return results;
	}
}
