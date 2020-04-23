package ust.hk.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtFieldWriteImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.Method;
import ust.hk.util.Pair;

public class ExpressionReplaceMutator extends CiviProcessor {

	
	public List<CtExpression<?>> ingredients;
	public HashMap<Integer, HashSet<Pair<String, String>>> positionVariables;
	public int sourceStart = 0;
	public int sourceEnd = 0;
	public Method targetMethod;
	
	public ExpressionReplaceMutator(List<CtExpression<?>> elements, 
			HashMap<Integer, HashSet<Pair<String, String>>> positionVariables, int sourceStart, int sourceEnd) {
		ingredients = new ArrayList<CtExpression<?>>(elements);
		this.positionVariables = new HashMap<Integer, HashSet<Pair<String, String>>> (positionVariables);
		this.sourceEnd = sourceEnd;
		this.sourceStart = sourceStart;
	}
	
	public ExpressionReplaceMutator(List<CtExpression<?>> elements, 
			HashMap<Integer, HashSet<Pair<String, String>>> positionVariables, Method targetMethod) {
		ingredients = new ArrayList<CtExpression<?>>(elements);
		this.positionVariables = new HashMap<Integer, HashSet<Pair<String, String>>> (positionVariables);
		this.targetMethod = targetMethod;
	}
	
	public CtExpression<?> getCandidateRandomly() {
		Random random = new Random();
		return ingredients.get(random.nextInt(ingredients.size()));
	}
	
	public boolean isWithinScope(CtElement candidate) {
		CtMethodImpl<?> enclosingMethod = null;
		CtElement parant = candidate.getParent();
		while (parant != null) {
			if (parant instanceof CtMethodImpl) 
				break;
			parant = parant.getParent();
		}
		
		boolean withinScope = false;
		if (parant instanceof CtMethodImpl) {
			enclosingMethod = (CtMethodImpl<?>) parant;
			//if (enclosingMethod.toString().contains("getRangeAxis")) {
				//System.out.println("ALL\t" + targetMethod.methodName + "\t" + targetMethod.paraList.toString());
				List<String> paraList = new ArrayList<>();
				for (CtParameter<?> parameter : enclosingMethod.getParameters()) {
					paraList.add(parameter.getSimpleName());
				}
				//System.out.println(enclosingMethod.getSimpleName() + "\t" + paraList);
			//}
			if (enclosingMethod.getSimpleName().equals(targetMethod.methodName) && paraList.size() == targetMethod.paraList.size()) {
				withinScope = true;
				for (int i = 0; i < paraList.size(); i++) {
					if (!paraList.get(i).equals(targetMethod.paraList.get(i))) withinScope = false;
				}
			}
		}
		return withinScope;
	}

	@Override
	public boolean isToBeProcessed(CtElement candidate) {
		
		// we only mutate those elements within the method
		return candidate instanceof CtExpression 
				&& isWithinScope(candidate);
	}
	
	public boolean variableResolved(int sourcePos, CtExpression<?> element) {
		
		HashSet<Pair<String, String>> variables = new HashSet<Pair<String,String>>();
		
		List<CtElement> elements  = element.filterChildren(new TypeFilter(CtVariableReadImpl.class)).list();
		
		if (!positionVariables.containsKey(sourcePos)) return false;
		
		for (CtElement tmp : elements) {
			CtVariableReadImpl variable = (CtVariableReadImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
            variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements  = element.filterChildren(new TypeFilter(CtVariableWriteImpl.class)).list();
		for (CtElement tmp : elements) {
			CtVariableWriteImpl variable = (CtVariableWriteImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
            variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements = element.filterChildren(new TypeFilter(CtFieldReadImpl.class)).list();
		for (CtElement tmp : elements) {
			CtFieldReadImpl variable = (CtFieldReadImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
			String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
			if (simpleName.contains("."))
				simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
			variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements = element.filterChildren(new TypeFilter(CtFieldWriteImpl.class)).list();
		for (CtElement tmp : elements) {
			CtFieldWriteImpl variable = (CtFieldWriteImpl) tmp;
			String simpleName = variable.toString();
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
			if (simpleName.contains("."))
				simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
			variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}

		return positionVariables.get(sourcePos).containsAll(variables);
	
	}
	
	public CtExpression<?> getTargetExpression(CtElement candidate) {
		if (!(candidate instanceof CtExpression)) {
			return null;
		}
		
		CtExpression<?> expr = (CtExpression<?>) candidate;
		CtExpression<?> texpr = getCandidateRandomly();
		
		int count = 0;
		
		while (true) { 
			
			count++;
			if (count > 100) break;
			if (expr.getType() == null || texpr.getType() == null) {
				texpr = getCandidateRandomly();
				continue;
			}
			if (!expr.toString().equals(texpr.toString()) && expr.getType().equals(texpr.getType()) && variableResolved(candidate.getPosition().getSourceStart(), texpr)) break;
			texpr = getCandidateRandomly();
			
			if (!positionVariables.containsKey(candidate.getPosition().getSourceStart())) continue;	
		}
		
		if (texpr != null && texpr.getType() != null && expr.getType() != null) {
		if (!expr.toString().equals(texpr.toString()) && expr.getType().equals(texpr.getType()) && variableResolved(candidate.getPosition().getSourceStart(), texpr))
			return (CtExpression<?>) texpr;
		}
		
		return null;
	}	
	
	@Override
	public void process(CtElement candidate) {
		if (!(candidate instanceof CtExpression)) {
			return;
		}
		
		CtExpression<?> expr = (CtExpression<?>) candidate;
		CtExpression<?> texpr = getCandidateRandomly();
	
		int count = 0;
		
		while (true) {
//			System.out.println("===\t" + expr.toString() + "\t" + expr.getType());
			count++;
			if (count > 100) break;
			if (expr.getType() == null || texpr.getType() == null) {
				texpr = getCandidateRandomly();
				continue;
			}
			if (!expr.toString().equals(texpr.toString()) && expr.getType().equals(texpr.getType()) && variableResolved(candidate.getPosition().getSourceStart(), texpr)) break;
			texpr = getCandidateRandomly();
			
			if (!positionVariables.containsKey(candidate.getPosition().getSourceStart())) continue;
			
//			System.out.println(count + "\t" + expr.toString() + "\t" + texpr.toString());
//			System.out.println(expr.getType() + "\t" + texpr.getType() + "\t" + positionVariables.get(candidate.getPosition().getSourceStart()).toString());
			
		}
		
		if (texpr != null && texpr.getType() != null && expr.getType() != null) {
		if (!expr.toString().equals(texpr.toString()) && expr.getType().equals(texpr.getType()) && variableResolved(candidate.getPosition().getSourceStart(), texpr))
			System.out.println("===\t has been replaced");
			System.out.println(expr.toString() + "\t" + texpr.toString());
			
//			replace(texpr, candidate);
			// how to replace a element with another
			CtExpression<?> ttexpr = (CtExpression<?>) candidate;
			expr.replace(ttexpr);
		
			// ((CtExpression<?>) texpr).replace((CtExpression<?>) candidate);
			// candidate.replace(texpr.clone());
			System.out.println(expr.toString() + "\t" + texpr.toString());
		}
	}
}


