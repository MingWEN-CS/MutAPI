package ust.hk.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;


import ust.hk.entity.MutatedClass.MutantType;
import ust.hk.util.DataTransfer;
import ust.hk.util.Pair;
import ust.hk.util.PairNP;

public class Mutant {
	public String api;
	public String mid;
	public List<APIUsage> usages;
	public List<MutantType> mutantTypes;
	public List<PairNP<String, String>> modifications;
	public List<Pair<Integer, Integer>> modificationLocations;
	public HashMap<String, String> usageAPI;
	public String instanceVaraible = "this";
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

	
	private String getKeywordsOrNumber(String content) {
		if (content.equals("null")) return null;
		else if (Mutant.isNumeric(content)) return content;
		else return "variable";
	}
	
	
	
	private String getObject(String content, String variableName) {
		if (content.contains(".")) {
			return "instance" + content.substring(content.indexOf("."));
		} else return content.replace(variableName, "instance");
	}
	
	public List<Pair<Integer, Integer>> matchBrackets(String expression) {
		List<Pair<Integer, Integer>> brackets = new ArrayList<>();
		
		Stack<Integer> stack = new Stack<>();
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(')
				stack.push(i);
			if (expression.charAt(i) == ')') {
				if (stack.size() == 0) return brackets;
				int previousIndex = stack.pop();
				if (i == previousIndex + 1) continue;
				brackets.add(new Pair<Integer, Integer>(previousIndex, i));
			}
		}
		
		return brackets;
	} 
	
	public String abstractCondition(String original, boolean isChecker) {
		original = original.trim();
		if (original.startsWith("(") && original.endsWith(")"))
			original = original.substring(1, original.length() - 1);
		if (original.contains("&&") || original.contains("||")) return "";
		String abstracted = "";
		String condition = original;
		condition = condition.replaceAll(" ", "");
		if (isChecker) {
			if (condition.contains("==null") || condition.contains("!=null")) 
				abstracted = "Checking Null of Call Receiver";
		}
		if (!condition.contains(instanceVaraible)) return abstracted;
		String[] split;
		if (condition.contains("==")) {
			split = condition.split("==");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + "==" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + "==" + getObject(split[1], instanceVaraible);
		} else if (condition.contains("!=")) {
			split = condition.split("!=");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + "!=" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + "!=" + getObject(split[1], instanceVaraible);
		} else if (condition.contains(">=")) {
			split = condition.split(">=");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + ">=" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + ">=" + getObject(split[1], instanceVaraible);
		} else if (condition.contains("<=")) {
			split = condition.split("<=");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + "<=" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + "<=" + getObject(split[1], instanceVaraible);
		} else if (condition.contains(">")) {
			split = condition.split(">");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + ">" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + ">" + getObject(split[1], instanceVaraible);
		} else if (condition.contains("<")) {
			split = condition.split("<");
			if (split[0].contains(instanceVaraible))
				condition = getObject(split[0], instanceVaraible) + "<" + getKeywordsOrNumber(split[1]);
			else 
				condition = getKeywordsOrNumber(split[0]) + "<" + getObject(split[1], instanceVaraible);
		} else {
			condition = getObject(condition, instanceVaraible);;
		}
		abstracted = condition;
		return abstracted;
	}	
	
	public String abstractCondition2(String original, boolean isChecker) {
		String abstracted = "";
		
		List<String> conditions = new ArrayList<String>();
		List<String> operators = new ArrayList<String>();
		operators.add("");
		int previousIndex = 0;
		
		for (int i = 0; i < original.length() - 1; i++) {
			if ((original.charAt(i) == '&' && original.charAt(i + 1) == '&') || (original.charAt(i) == '|' && original.charAt(i+1) == '|')) {
				//System.out.println(original + "\t" + previousIndex + "\t" + i);
				conditions.add(original.substring(previousIndex, i).trim());
				operators.add(original.charAt(i) == '&' ? "&&" : "||");
				previousIndex = i + 2;
				i = i + 2;
			}
		}
		//System.out.println(previousIndex);
		if (previousIndex < original.length())
			conditions.add(original.substring(previousIndex));
		//System.out.println(conditions.toString());
		for (int i = 0; i < conditions.size(); i++) {
			String condition = conditions.get(i);
			condition = condition.replaceAll(" ", "");
			if (isChecker) {
				
				if (condition.contains("==null")) {
					abstracted += operators.get(i) + "Rec==null"; 
				} else if (condition.contains("!=null")) {
					abstracted += operators.get(i) + "Rec!=null";
				}
			} else {
				if (!condition.contains(instanceVaraible)) continue;
				String[] split;
				if (condition.contains("==")) {
					split = condition.split("==");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + "==" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + "==" + getObject(split[1], instanceVaraible);
				} else if (condition.contains("!=")) {
					split = condition.split("!=");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + "!=" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + "!=" + getObject(split[1], instanceVaraible);
				} else if (condition.contains(">=")) {
					split = condition.split(">=");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + ">=" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + ">=" + getObject(split[1], instanceVaraible);
				} else if (condition.contains("<=")) {
					split = condition.split("<=");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + "<=" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + "<=" + getObject(split[1], instanceVaraible);
				} else if (condition.contains(">")) {
					split = condition.split(">");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + ">" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + ">" + getObject(split[1], instanceVaraible);
				} else if (condition.contains("<")) {
					split = condition.split("<");
					if (split[0].contains(instanceVaraible))
						condition = getObject(split[0], instanceVaraible) + "<" + getKeywordsOrNumber(split[1]);
					else 
						condition = getKeywordsOrNumber(split[0]) + "<" + getObject(split[1], instanceVaraible);
				} else {
					condition = getObject(condition, instanceVaraible);;
				}
				
				abstracted += operators.get(i) + condition;
			}
		}
		System.out.println("CONDITION:\t" + original + "\t" + abstracted);
		return abstracted;
	} 
	
	public Mutant() {
		
	}
	
	public Mutant(String mid, String api, String instanceVariable, List<APIUsage> usages, List<MutantType> mutantTypes, List<PairNP<String, String>> modifications, 
			List<Pair<Integer, Integer>> modificationLocation) {
		this.mid = mid;
		this.api = api;
		if (!instanceVariable.equals(""))
			this.instanceVaraible = instanceVariable;
		this.usages = new ArrayList<>(usages);
		this.mutantTypes = new ArrayList<>(mutantTypes);
		this.modifications = new ArrayList<>(modifications);
		this.modificationLocations = new ArrayList<>(modificationLocation);
		usageAPI = new HashMap<String, String>();
		for (APIUsage usage : usages) {
			if (!usageAPI.containsKey(usage.instance)) {
				String methodName = usage.instance;
				//System.out.println(methodName);
				if (methodName.contains("."))
					methodName = methodName.substring(methodName.indexOf("."));
				else methodName = "." + methodName;
				methodName = methodName.substring(0, methodName.indexOf("("));
				usageAPI.put(usage.instance, api.substring(0, api.lastIndexOf(".")) + methodName);
			}
		}
	}
	
	public HashSet<Pair<String, String>> getMissingMethodCalls() {
		HashSet<Pair<String, String>> missingCalls = new HashSet<Pair<String, String>>();	
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.DELETE_STRUCTURE) || mt.equals(MutantType.DELETE_USAGE) || mt.equals(MutantType.SWAP_USAGE)) {
				for (APIUsage usage : usages) {
					if (modifications.get(i).getKey().contains(usage.instance)) {
						missingCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
					}
				}
			}
		}
		return missingCalls;
	}
	
	public HashSet<Pair<String, String>> getRedundantUsage() {
		HashSet<Pair<String, String>> redundantCalls = new HashSet<Pair<String, String>>();	
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.ADD_USAGE)) {
				String content = modifications.get(i).getValue().trim();
				// only consider those method invocation
				if (content.contains("if") || content.contains("try") || content.contains("while") || content.contains("for")) continue;
				for (APIUsage usage : usages) {
					if (modifications.get(i).getValue().contains(usage.instance)) {
						redundantCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
					}
				}
			}
		}
		return redundantCalls;
	}
	

	public HashSet<Pair<String, String>> getMissingException() {
		HashSet<Pair<String, String>> missingControl = new HashSet<Pair<String, String>>();
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.DELETE_CONTROL)) {
				if (modifications.get(i).getKey().trim().startsWith("try")) {
					HashSet<String> exceptions = DataTransfer.obtainCatchExceptions(modifications.get(i).getKey());
					for (String exception : exceptions) {
						missingControl.add(new Pair<String, String>(mt.toString(), "try catch " + exception));
					}
				}
 			}
		}
		return missingControl;
	}	
	
	public HashSet<Pair<String, String>> getIncorrectCondiction() {
		HashSet<Pair<String, String>> incorrectCondictions = new HashSet<Pair<String, String>>();
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.MUTATE_CONDITION)) {
				String condition = modifications.get(i).getKey().trim();
				String abstractCondition = abstractCondition(condition, false);
				if (!abstractCondition.equals("")) {
					
					incorrectCondictions.add(new Pair<String, String>(mt.toString(), abstractCondition));
				}
					
			}
		}
		return incorrectCondictions;
	}
	
	public HashSet<Pair<String, String>> getIncorrectMethodCallsWrongParameter() {
		HashSet<Pair<String, String>> incorrectCalls = new HashSet<Pair<String, String>>();
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.SWAP_PARAMETER)) {
				for (APIUsage usage : usages) {
					if (modifications.get(i).getValue().contains(usage.instance)) {
						incorrectCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
					}
				}
			}
		}
		return incorrectCalls;
	}
	
	public HashSet<Pair<String, String>> getIncorrectMethodCallAfter() {
		HashSet<Pair<String, String>> incorrectCalls = new HashSet<Pair<String, String>>();
		
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if ((mt.equals(MutantType.SWAP_USAGE) && modificationLocations.get(i).getValue() < modificationLocations.get(i).getKey())) {
				for (APIUsage usage : usages) {
					if (modifications.get(i).getValue().contains(usage.instance)) {
						incorrectCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
					}
				}
			}
		}
		return incorrectCalls;
	}	
	
//	public HashSet<Pair<String, String>> getRedundantCall() {
//		HashSet<Pair<String, String>> incorrectCalls = new HashSet<Pair<String, String>>();
//		
//		for (int i = 0; i < modifications.size(); i++) {
//			MutantType mt = mutantTypes.get(i);
//			if (mt.equals(MutantType.ADD_USAGE)) {
//				for (APIUsage usage : usages) {
//					if (modifications.get(i).getValue().contains(usage.instance)) {
//						incorrectCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
//					}
//				}
//			}
//		}
//		return incorrectCalls;
//	}
	
	public HashSet<Pair<String, String>> getIncorrectMethodCallsBefore() {
		HashSet<Pair<String, String>> incorrectCalls = new HashSet<Pair<String, String>>();	
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if ((mt.equals(MutantType.SWAP_USAGE) && modificationLocations.get(i).getValue() > modificationLocations.get(i).getKey()) || mt.equals(MutantType.ADD_USAGE)) {
				for (APIUsage usage : usages) {
					if (modifications.get(i).getValue().contains(usage.instance)) {
						incorrectCalls.add(new Pair<String, String>(mt.toString(), usageAPI.get(usage.instance)));
					}
				}
			}
		}
		return incorrectCalls;
	}
	
	public HashSet<Pair<String, String>> getMissingChecker() {
		HashSet<Pair<String, String>> missingChecker = new HashSet<Pair<String, String>>();
		for (int i = 0; i < modifications.size(); i++) {
			MutantType mt = mutantTypes.get(i);
			if (mt.equals(MutantType.DELETE_CHECKER)) {
				if (modifications.get(i).getKey().trim().startsWith("if")) {
					String content = modifications.get(i).getKey();
					String condition = content.substring(content.indexOf("(") + 1, content.indexOf(")")).trim();
					String abstractCondition = abstractCondition(condition, true);
					if (!abstractCondition.equals(""))
					missingChecker.add(new Pair<String, String>(mt.toString(), abstractCondition));
				}
			}
		}
		return missingChecker;
	}
	
	public static void main(String[] args) {
		Mutant mutant = new Mutant();
//		List<Pair<Integer, Integer>> brackets = mutant.matchBrackets("	(plotState != null) && ((plotState.getOwner()) != null)");
//		String condition = mutant.abstractCondition2(iterator.hasNext(), isChecker)
		//String a = "  A B   C";
//		System.out.println(brackets.toString());
	}
}
