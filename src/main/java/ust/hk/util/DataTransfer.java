package ust.hk.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ust.hk.entity.TestResult;

public class DataTransfer {
	
	public static List<TestResult> readTestResult(String filename) {
		String content = FileToLines.readFile(filename);
		//System.out.println(content);
		String[] splits = content.split("TEST:");
		List<TestResult> mutantResults = new ArrayList<TestResult>();
		for (String split : splits) {
			String[] tmps = split.split("\n");
			String testCase = tmps[0].trim();
			//System.out.println(testCase + "\t" + tmps.length);
			TestResult testResult = new TestResult(testCase);
			String testMethod = "";
			String failException = "";
			List<String> stackTrace = new ArrayList<String>();
			for (int k = 1; k < tmps.length; k++) {
				if (tmps[k].contains("(" + testCase + ")")) {
					if (!testMethod.equals("")) {
						testResult.addFailMethod(testCase.trim() + "." + testMethod.trim(), failException, stackTrace);
						stackTrace.clear();
					}
					testMethod = tmps[k].substring(tmps[k].indexOf(" "), tmps[k].indexOf("("));
					if (k + 1 < tmps.length)
                        failException = tmps[k+1].trim();
					else failException = "";
                    k++;
				} else if (tmps[k].trim().startsWith("at")) {
					stackTrace.add(tmps[k].trim());
				}	
			}
			
			if (!testMethod.equals("")) {
				testResult.addFailMethod(testCase.trim() + "." + testMethod.trim(), failException, stackTrace);
				stackTrace.clear();
			}
			mutantResults.add(testResult);
		}
		return mutantResults;
	}
	
	public static HashSet<Integer> stringToIntSet(String line) {
		HashSet<Integer> ids = new HashSet<Integer>();
		line = line.substring(1, line.length() - 1);
		if (!line.contains(",")) {
			if (!line.equals(""))
				ids.add(Integer.parseInt(line));
			return ids;
		}
		String[] splits = line.split(",");
		
		for (String split : splits) {
			ids.add(Integer.parseInt(split.trim()));
		}
		return ids;
	}
	
	public static HashSet<String> stringToStringSet(String line) {
		HashSet<String> ids = new HashSet<String>();
		line = line.substring(1, line.length() - 1);
		if (!line.contains(",")) {
			if (!line.equals(""))
				ids.add(line.trim());
			return ids;
		}
		String[] splits = line.split(",");
		
		for (String split : splits) {
			ids.add(split.trim());
		}
		return ids;
	}
	
	public static HashSet<String> turnStringToHashSetStringWithType(String content) {
		HashSet<String> elements = new HashSet<String>();
        if (content.equals("")) return elements;
        
        content = content.trim().substring(1, content.length() - 1);
		int index = 0;
		int nextIndex = content.indexOf(":");
		String element;
		while (true) {
			if (nextIndex < 0) 
				break;
			element = content.substring(index, nextIndex);
			content = content.substring(nextIndex);
			index = 0;
			nextIndex = content.indexOf(",");
			if (nextIndex < 0) {
				element += content.substring(index);
				elements.add(element.trim());
				break;
			} else {
				element += content.substring(index, nextIndex);
				elements.add(element.trim());
				content = content.substring(nextIndex);
				index = 1;
				nextIndex = content.indexOf(":");
			}
			
		}
		
		return elements;
	}
	
	public static HashSet<Pair<String, String>> turnStringToHashSetPair(String content) {
		HashSet<String> tmps = turnStringToHashSetStringWithType(content);
		HashSet<Pair<String, String>> pairs = new HashSet<Pair<String, String>>();
		for (String tmp : tmps) {
			String[] split2 = tmp.split(":");
			if (split2.length < 2) continue;
			pairs.add(new Pair<String,String>(split2[0].trim(), split2[1].trim()));
		}
		return pairs;
	}
	
	public static HashSet<String> turnStringToHashSetString(String content) {
		HashSet<String> elements = new HashSet<String>();
		content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.equals(""))
				elements.add(content.trim());
			return elements;
		}
		String[] tmp = content.split(",");
		
//		System.out.println(content);
		int index = 0;
		while (index < tmp.length) {
			if (tmp[index].contains("<") && !tmp[index].contains(":")) {
				elements.add(tmp[index] + "," + tmp[index+1]);
				index = index + 2;
			}
			else {
				elements.add(tmp[index]);
				index++;
			}
		}
		return elements;
	}
	
	public static HashSet<Integer> turnStringToHashSetInteger(String content) {
		HashSet<String> elements = turnStringToHashSetString(content);
		HashSet<Integer> ids = new HashSet<Integer>();
		for (String element : elements) {
			ids.add(Integer.parseInt(element.trim()));
		}
		return ids;
	}
	
	public static List<String> turnStringToListString(String content) {
		List<String> elements = new ArrayList<String>();
		if (content.equals("")) return elements;
        content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.equals(""))
				elements.add(content);
			return elements;
		}
		String[] tmp = content.split(",");
		
//		System.out.println(content);
		for (String element : tmp)
			elements.add(element.trim());
		return elements;
	}
	
	public static HashSet<String> obtainCatchExceptions(String content) {
		HashSet<String> exceptions = new HashSet<String>();
		Pattern pattern = Pattern.compile("catch\\s+[(].+?[)]");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String exception = content.substring(matcher.start(), matcher.end());
			exception = exception.substring(exception.indexOf("(") + 1, exception.lastIndexOf(" "));
			if (exception.contains(" "))
				exception = exception.substring(exception.indexOf(" ") + 1);
			System.out.println(exception);
			exceptions.add(exception);
		}
		return exceptions;
	}
}
